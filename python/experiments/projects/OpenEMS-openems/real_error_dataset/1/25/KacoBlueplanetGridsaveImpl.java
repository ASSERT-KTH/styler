package io.openems.edge.batteryinverter.kaco.blueplanetgridsave;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.battery.api.Battery;
import io.openems.edge.batteryinverter.api.BatteryInverterConstraint;
import io.openems.edge.batteryinverter.api.ManagedSymmetricBatteryInverter;
import io.openems.edge.batteryinverter.api.SymmetricBatteryInverter;
import io.openems.edge.batteryinverter.kaco.blueplanetgridsave.KacoSunSpecModel.S64201.S64201CurrentState;
import io.openems.edge.batteryinverter.kaco.blueplanetgridsave.KacoSunSpecModel.S64202.S64202EnLimit;
import io.openems.edge.batteryinverter.kaco.blueplanetgridsave.statemachine.Context;
import io.openems.edge.batteryinverter.kaco.blueplanetgridsave.statemachine.StateMachine;
import io.openems.edge.batteryinverter.kaco.blueplanetgridsave.statemachine.StateMachine.State;
import io.openems.edge.batteryinverter.sunspec.AbstractSunSpecBatteryInverter;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.bridge.modbus.sunspec.DefaultSunSpecModel;
import io.openems.edge.bridge.modbus.sunspec.SunSpecModel;
import io.openems.edge.bridge.modbus.sunspec.SunSpecPoint;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.EnumReadChannel;
import io.openems.edge.common.channel.EnumWriteChannel;
import io.openems.edge.common.channel.FloatWriteChannel;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.channel.StateChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.cycle.Cycle;
import io.openems.edge.common.startstop.StartStop;
import io.openems.edge.common.startstop.StartStoppable;
import io.openems.edge.common.sum.GridMode;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.ess.power.api.Phase;
import io.openems.edge.ess.power.api.Pwr;
import io.openems.edge.ess.power.api.Relationship;
import io.openems.edge.timedata.api.Timedata;
import io.openems.edge.timedata.api.TimedataProvider;
import io.openems.edge.timedata.api.utils.CalculateEnergyFromPower;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Battery-Inverter.Kaco.BlueplanetGridsave", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class KacoBlueplanetGridsaveImpl extends AbstractSunSpecBatteryInverter implements KacoBlueplanetGridsave,
		ManagedSymmetricBatteryInverter, SymmetricBatteryInverter, OpenemsComponent, TimedataProvider, StartStoppable {

	private static final int UNIT_ID = 1;
	private static final int READ_FROM_MODBUS_BLOCK = 1;

	@Reference
	private Cycle cycle;

	@Reference
	private ConfigurationAdmin cm;

	@Reference
	private ComponentManager componentManager;

	@Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.OPTIONAL)
	private volatile Timedata timedata = null;

	private final Logger log = LoggerFactory.getLogger(KacoBlueplanetGridsaveImpl.class);

	private final CalculateEnergyFromPower calculateChargeEnergy = new CalculateEnergyFromPower(this,
			SymmetricBatteryInverter.ChannelId.ACTIVE_CHARGE_ENERGY);
	private final CalculateEnergyFromPower calculateDischargeEnergy = new CalculateEnergyFromPower(this,
			SymmetricBatteryInverter.ChannelId.ACTIVE_DISCHARGE_ENERGY);

	/**
	 * Manages the {@link State}s of the StateMachine.
	 */
	private final StateMachine stateMachine = new StateMachine(State.UNDEFINED);

	private Config config;

	/**
	 * Kaco 92 does not have model 64203.
	 */
	private boolean hasSunSpecModel64203 = false;

	/**
	 * Active SunSpec models for KACO blueplanet gridsave. Commented models are
	 * available but not used currently.
	 */
	private static final Map<SunSpecModel, Priority> ACTIVE_MODELS = ImmutableMap.<SunSpecModel, Priority>builder()
			.put(DefaultSunSpecModel.S_1, Priority.LOW) //
			.put(DefaultSunSpecModel.S_103, Priority.LOW) //
			.put(DefaultSunSpecModel.S_121, Priority.LOW) //
			.put(KacoSunSpecModel.S_64201, Priority.HIGH) //
			.put(KacoSunSpecModel.S_64202, Priority.LOW) //
			.put(KacoSunSpecModel.S_64203, Priority.LOW) //
			.put(KacoSunSpecModel.S_64204, Priority.LOW) //
			.build();

	// Further available SunSpec blocks provided by KACO blueplanet are:
	// .put(SunSpecModel.S_113, Priority.LOW) //
	// .put(SunSpecModel.S_120, Priority.LOW) //
	// .put(SunSpecModel.S_122, Priority.LOW) //
	// .put(SunSpecModel.S_123, Priority.LOW) //
	// .put(SunSpecModel.S_126, Priority.LOW) //
	// .put(SunSpecModel.S_129, Priority.LOW) //
	// .put(SunSpecModel.S_130, Priority.LOW) //
	// .put(SunSpecModel.S_132, Priority.LOW) //
	// .put(SunSpecModel.S_135, Priority.LOW) //
	// .put(SunSpecModel.S_136, Priority.LOW) //
	// .put(SunSpecModel.S_160, Priority.LOW) //

	@Activate
	public KacoBlueplanetGridsaveImpl() throws OpenemsException {
		super(//
				ACTIVE_MODELS, //
				OpenemsComponent.ChannelId.values(), //
				SymmetricBatteryInverter.ChannelId.values(), //
				ManagedSymmetricBatteryInverter.ChannelId.values(), //
				StartStoppable.ChannelId.values(), //
				KacoBlueplanetGridsave.ChannelId.values() //
		);
		this._setGridMode(GridMode.ON_GRID);
	}

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	@Activate
	void activate(ComponentContext context, Config config) throws OpenemsException {
		if (super.activate(context, config.id(), config.alias(), config.enabled(), UNIT_ID, this.cm, "Modbus",
				config.modbus_id(), READ_FROM_MODBUS_BLOCK)) {
			return;
		}
		this.config = config;
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void run(Battery battery, int setActivePower, int setReactivePower) throws OpenemsNamedException {
		// Store the current State
		this.channel(KacoBlueplanetGridsave.ChannelId.STATE_MACHINE).setNextValue(this.stateMachine.getCurrentState());

		// Initialize 'Start-Stop' Channel
		this._setStartStop(StartStop.UNDEFINED);

		// Stop early if initialization is not finished
		if (!this.isSunSpecInitializationCompleted()) {
			return;
		}

		// Set Display Information
		this.setDisplayInformation(battery);

		// Set Battery Limits
		this.setBatteryLimits(battery);

		// Calculate the Energy values from ActivePower.
		this.calculateEnergy();

		if (this.config.activateWatchdog()) {
			// Trigger the Watchdog
			this.triggerWatchdog();
		}

		// Set State-Channels
		this.setStateChannels();

		// Prepare Context
		Context context = new Context(this, battery, this.config, setActivePower, setReactivePower);

		// Call the StateMachine
		try {
			this.stateMachine.run(context);

			this.channel(KacoBlueplanetGridsave.ChannelId.RUN_FAILED).setNextValue(false);

		} catch (OpenemsNamedException e) {
			this.channel(KacoBlueplanetGridsave.ChannelId.RUN_FAILED).setNextValue(true);
			this.logError(this.log, "StateMachine failed: " + e.getMessage());
		}
	}

	@Override
	public BatteryInverterConstraint[] getStaticConstraints() throws OpenemsException {
		if (this.stateMachine.getCurrentState() == State.RUNNING) {
			return BatteryInverterConstraint.NO_CONSTRAINTS;

		} else {
			// Block any power as long as we are not RUNNING
			return new BatteryInverterConstraint[] { //
					new BatteryInverterConstraint("KACO inverter not ready", Phase.ALL, Pwr.REACTIVE, //
							Relationship.EQUALS, 0d), //
					new BatteryInverterConstraint("KACO inverter not ready", Phase.ALL, Pwr.ACTIVE, //
							Relationship.EQUALS, 0d) //
			};
		}
	}

	/**
	 * Sets the Battery Limits.
	 * 
	 * @param battery the linked {@link Battery}
	 * @throws OpenemsNamedException on error
	 */
	private void setBatteryLimits(Battery battery) throws OpenemsNamedException {
		// Discharge Min Voltage
		FloatWriteChannel disMinVChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64202.DIS_MIN_V_0);
		Integer dischargeMinVoltage = battery.getDischargeMinVoltage().get();
		if (Objects.equal(dischargeMinVoltage, 0)) {
			dischargeMinVoltage = null; // according to setup manual DIS_MIN_V must not be zero
		}
		disMinVChannel.setNextWriteValueFromObject(dischargeMinVoltage);

		// Charge Max Voltage
		FloatWriteChannel chaMaxVChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64202.CHA_MAX_V_0);
		Integer chargeMaxVoltage = battery.getChargeMaxVoltage().get();
		if (Objects.equal(chargeMaxVoltage, 0)) {
			chargeMaxVoltage = null; // according to setup manual CHA_MAX_V must not be zero
		}
		chaMaxVChannel.setNextWriteValueFromObject(chargeMaxVoltage);

		// Discharge Max Current
		// negative value is corrected as zero
		FloatWriteChannel disMaxAChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64202.DIS_MAX_A_0);
		disMaxAChannel.setNextWriteValue(Math.max(0F, battery.getDischargeMaxCurrent().orElse(0)));

		// Charge Max Current
		// negative value is corrected as zero
		FloatWriteChannel chaMaxAChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64202.CHA_MAX_A_0);
		chaMaxAChannel.setNextWriteValue(Math.max(0F, battery.getChargeMaxCurrent().orElse(0)));

		// Activate Battery values
		EnumWriteChannel enLimitChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64202.EN_LIMIT_0);
		enLimitChannel.setNextWriteValue(S64202EnLimit.ACTIVATE);
	}

	/**
	 * Sets the information that is shown on the Display, like State-of-Charge,
	 * State-of-Health and Max-Cell-Temperature.
	 * 
	 * @param battery the linked {@link Battery}
	 * @throws OpenemsNamedException on error
	 */
	private void setDisplayInformation(Battery battery) throws OpenemsNamedException {
		if (this.hasSunSpecModel64203) {
			// State-of-Charge
			FloatWriteChannel batSocChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64203.BAT_SOC_0);
			batSocChannel.setNextWriteValueFromObject(battery.getSoc().get());

			// State-of-Health
			FloatWriteChannel batSohChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64203.BAT_SOH_0);
			batSohChannel.setNextWriteValueFromObject(battery.getSoh().get());

			// Max-Cell-Temperature
			FloatWriteChannel batTempChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64203.BAT_TEMP_0);
			batTempChannel.setNextWriteValueFromObject(battery.getMaxCellTemperature().get());
		}
	}

	private Instant lastTriggerWatchdog = Instant.MIN;

	/**
	 * Triggers the Watchdog after WATCHDOG_TRIGGER passed.
	 * 
	 * @throws OpenemsNamedException on error
	 */
	private void triggerWatchdog() throws OpenemsNamedException {
		Instant now = Instant.now(this.componentManager.getClock());
		if (Duration.between(this.lastTriggerWatchdog, now)
				.getSeconds() >= KacoBlueplanetGridsave.WATCHDOG_TRIGGER_SECONDS) {
			IntegerWriteChannel watchdogChannel = this.getSunSpecChannelOrError(KacoSunSpecModel.S64201.WATCHDOG);
			watchdogChannel.setNextWriteValue(KacoBlueplanetGridsave.WATCHDOG_TIMEOUT_SECONDS);
			this.lastTriggerWatchdog = now;
		}
	}

	/**
	 * Sets the State-Channels, e.g. Warnings and Faults.
	 * 
	 * @throws OpenemsNamedException on error
	 */
	private void setStateChannels() throws OpenemsNamedException {
		/*
		 * INVERTER_CURRENT_STATE_FAULT
		 */
		StateChannel inverterCurrentStateChannel = this
				.channel(KacoBlueplanetGridsave.ChannelId.INVERTER_CURRENT_STATE_FAULT);
		switch (this.getCurrentState()) {
		case FAULT:
		case UNDEFINED:
		case NO_ERROR_PENDING:
			inverterCurrentStateChannel.setNextValue(true);
			break;
		case GRID_CONNECTED:
		case GRID_PRE_CONNECTED:
		case MPPT:
		case OFF:
		case PRECHARGE:
		case SHUTTING_DOWN:
		case SLEEPING:
		case STANDBY:
		case STARTING:
		case THROTTLED:
			inverterCurrentStateChannel.setNextValue(false);
			break;
		}
	}

	/**
	 * Mark SunSpec initialization completed; this takes some time at startup.
	 */
	@Override
	protected void onSunSpecInitializationCompleted() {
		this.addCopyListener(//
				this.getSunSpecChannel(DefaultSunSpecModel.S121.W_MAX).get(), //
				SymmetricBatteryInverter.ChannelId.MAX_APPARENT_POWER //
		);
		this.addCopyListener(//
				this.getSunSpecChannel(KacoSunSpecModel.S64201.W).get(), //
				SymmetricBatteryInverter.ChannelId.ACTIVE_POWER //
		);
		this.addCopyListener(//
				this.getSunSpecChannel(KacoSunSpecModel.S64201.V_AR).get(), //
				SymmetricBatteryInverter.ChannelId.REACTIVE_POWER //
		);
	}

	@Override
	public S64201CurrentState getCurrentState() {
		Optional<EnumReadChannel> channel = this.getSunSpecChannel(KacoSunSpecModel.S64201.CURRENT_STATE);
		if (channel.isPresent()) {
			return channel.get().value().asEnum();
		} else {
			return S64201CurrentState.UNDEFINED;
		}
	}

	@Override
	protected SunSpecModel getSunSpecModel(int blockId) throws IllegalArgumentException {
		return KacoSunSpecModel.valueOf("S_" + blockId);
	}

	/**
	 * Calculate the Power-Precision from the Max Apparent Power using the SetPoint
	 * scale-factor.
	 */
	@Override
	public int getPowerPrecision() {
		Optional<IntegerReadChannel> scalefactorChannel = this.getSunSpecChannel(KacoSunSpecModel.S64201.W_SET_PCT_SF);
		if (!scalefactorChannel.isPresent()) {
			return 1;
		}
		Value<Integer> scalefactor = scalefactorChannel.get().value();
		Value<Integer> maxApparentPower = this.getMaxApparentPower();
		if (!scalefactor.isDefined() || !maxApparentPower.isDefined()) {
			return 1;
		}
		// Take one percent (0.01) of MaxApparentPower and then apply scalefactor
		return (int) (maxApparentPower.get() * 0.01 * Math.pow(10, scalefactor.get()));
	}

	@Override
	public String debugLog() {
		return this.stateMachine.getCurrentState().asCamelCase() + //
				"|" + this.getCurrentState().asCamelCase();
	}

	private final AtomicReference<StartStop> startStopTarget = new AtomicReference<StartStop>(StartStop.UNDEFINED);

	@Override
	public void setStartStop(StartStop value) {
		if (this.startStopTarget.getAndSet(value) != value) {
			// Set only if value changed
			this.stateMachine.forceNextState(State.UNDEFINED);
		}
	}

	@Override
	public StartStop getStartStopTarget() {
		switch (this.config.startStop()) {
		case AUTO:
			// read StartStop-Channel
			return this.startStopTarget.get();

		case START:
			// force START
			return StartStop.START;

		case STOP:
			// force STOP
			return StartStop.STOP;
		}

		assert false;
		return StartStop.UNDEFINED; // can never happen
	}

	/**
	 * Adds a Copy-Listener. It listens on setNextValue() and copies the value to
	 * the target channel.
	 * 
	 * @param <T>             the Channel type
	 * @param sourceChannel   the source Channel
	 * @param targetChannelId the target ChannelId
	 */
	private <T> void addCopyListener(Channel<T> sourceChannel,
			io.openems.edge.common.channel.ChannelId targetChannelId) {
		Consumer<Value<T>> callback = (value) -> {
			Channel<T> targetChannel = this.channel(targetChannelId);
			targetChannel.setNextValue(value);
		};
		sourceChannel.onSetNextValue(callback);
		callback.accept(sourceChannel.getNextValue());
	}

	@Override
	public <T extends Channel<?>> T getSunSpecChannelOrError(SunSpecPoint point) throws OpenemsException {
		return super.getSunSpecChannelOrError(point);
	}

	/**
	 * Calculate the Energy values from ActivePower.
	 */
	private void calculateEnergy() {
		// Calculate Energy
		Integer activePower = this.getActivePower().get();
		if (activePower == null) {
			// Not available
			this.calculateChargeEnergy.update(null);
			this.calculateDischargeEnergy.update(null);
		} else if (activePower > 0) {
			// Buy-From-Grid
			this.calculateChargeEnergy.update(0);
			this.calculateDischargeEnergy.update(activePower);
		} else {
			// Sell-To-Grid
			this.calculateChargeEnergy.update(activePower * -1);
			this.calculateDischargeEnergy.update(0);
		}
	}

	@Override
	public Timedata getTimedata() {
		return this.timedata;
	}

	protected void addBlock(int startAddress, SunSpecModel model, Priority priority) throws OpenemsException {
		super.addBlock(startAddress, model, priority);

		// Mark S_64203 as available
		if (model.equals(KacoSunSpecModel.S_64203)) {
			this.hasSunSpecModel64203 = true;
		}
	}
}
