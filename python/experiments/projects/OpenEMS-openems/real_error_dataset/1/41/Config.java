package io.openems.edge.evcs.keba.kecontact;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition( //
		name = "EVCS KEBA KeContact", //
		description = "Implements the KEBA KeContact P20/P30 electric vehicle charging station.")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "evcs0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "IP-Address", description = "The IP address of the charging station.")
	String ip() default "192.168.25.11";

	String webconsole_configurationFactory_nameHint() default "EVCS KEBA KeContact [{id}]";
}