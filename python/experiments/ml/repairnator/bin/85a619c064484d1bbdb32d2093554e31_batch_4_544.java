package fr.inria.spirals.repairnator.process.inspectors.properties.reproductionBuggyBuild;

import fr.inria.spirals.repairnator.process.step.AbstractStep;
import fr.inria.spirals.repairnator.process.step.BuildProject;
import fr.inria.spirals.repairnator.process.

step.CloneRepository;importfr.inria
. spirals.repairnator.process.step.TestProject;importfr.inria
. spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuild;importfr.inria
. spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuildSourceCode;importfr.inria
. spirals.repairnator.process.step.checkoutrepository.CheckoutPatchedBuild;importfr.inria
. spirals.repairnator.process.step.checkoutrepository.CheckoutType;importfr.inria
. spirals.repairnator.process.step.paths.ComputeSourceDir;importfr.inria
. spirals.repairnator.process.step.paths.ComputeTestDir;importfr.inria
. spirals.repairnator.process.step.repair.AbstractRepairStep;importfr.inria
. spirals.repairnator.states.ScannedBuildStatus;publicclassProcessDurations{

private GlobalStepInfo cloning ;

    private GlobalStepInfo building;
    private GlobalStepInfo testing;
    private GlobalStepInfo fixing;
    // this property is specific for repairnator.json ProcessDurations () {

    this.cloning =
        newGlobalStepInfo( ) ; this.building=
        newGlobalStepInfo( ) ; this.testing=
        newGlobalStepInfo( ) ; }publicGlobalStepInfogetCloning
    (

    ) { returncloning; }
        public GlobalStepInfogetBuilding
    (

    ) { returnbuilding; }
        public GlobalStepInfogetTesting
    (

    ) { returntesting; }
        public GlobalStepInfogetFixing
    (

    ) { returnfixing; }
        public voidaddGlobalStepInfo
    (

    AbstractStep step ){String stepName= step
        . getName ( );intstepDuration=step
        . getDuration ( );if(stepinstanceof
        CloneRepository ){ this .cloning .
            addStep(stepName,stepDuration);} elseif(
        step instanceof AbstractRepairStep ){ if (this .
            fixing ==null){ this .fixing =
                newGlobalStepInfo( ) ; }this.fixing
            .
            addStep(stepName,stepDuration);} elseif(
        step instanceof TestProject &&( step . getInspector
                ().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step.getInspector (
                ).getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE))){this.testing .
            addStep(stepName,stepDuration);} elseif(
        step instanceof BuildProject &&( step . getInspector
                ().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step.getInspector (
                ).getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE))){this.building .
            addStep(stepName,stepDuration);} else{if
        ( step .
            getInspector ().getBuildToBeInspected().getStatus()==ScannedBuildStatus.ONLY_FAIL || step.getInspector (
                    ).getBuildToBeInspected().getStatus()==ScannedBuildStatus.FAILING_AND_PASSING ) {if(step instanceof
                CheckoutBuggyBuild ){ this .building .
                    addStep(stepName,stepDuration);} }else{
                // PASSING_AND_PASSING_WITH_TEST_CHANGES
            if ( step instanceof
                CheckoutPatchedBuild ||step instanceof ComputeSourceDir || step instanceof ComputeTestDir ||
                        step instanceof CheckoutBuggyBuildSourceCode ) { this .building .
                    addStep(stepName,stepDuration);} }}}
                }
            