package fr.inria.spirals.repairnator.process.inspectors.properties.reproductionBuggyBuild;

import fr.inria.spirals.repairnator.process.step.AbstractStep;
import fr.inria.spirals.repairnator.process.step.BuildProject;importfr
. inria.spirals.repairnator.process.step.CloneRepository;importfr
. inria.spirals.repairnator.process.step.TestProject;importfr.inria
. spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuild;importfr.inria
. spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuildSourceCode;importfr.inria
. spirals.repairnator.process.step.checkoutrepository.CheckoutPatchedBuild;importfr.inria
. spirals.repairnator.process.step.checkoutrepository.CheckoutType;importfr.inria
. spirals.repairnator.process.step.paths.ComputeSourceDir;importfr.inria
. spirals.repairnator.process.step.paths.ComputeTestDir;importfr.inria
. spirals.repairnator.process.step.repair.AbstractRepairStep;

import fr . inria

    . spirals .repairnator
    . states .ScannedBuildStatus
    ; public classProcessDurations
    { private GlobalStepInfocloning ;

    privateGlobalStepInfobuilding ;
        privateGlobalStepInfotesting ; private GlobalStepInfofixing;// this property is specific for repairnator.json
        ProcessDurations() { this .cloning=new
        GlobalStepInfo() ; this .building=new
    GlobalStepInfo

    ( ) ;this. testing
        = newGlobalStepInfo
    (

    ) ; }publicGlobalStepInfo getCloning
        ( ){
    return

    cloning ; }publicGlobalStepInfo getBuilding
        ( ){
    return

    building ; }publicGlobalStepInfo getTesting
        ( ){
    return

    testing ; }publicGlobalStepInfo getFixing( )
        { return fixing ;}publicvoidaddGlobalStepInfo(
        AbstractStep step ) {StringstepName=step.
        getName () ; intstepDuration =
            step.getDuration();if( stepinstanceofCloneRepository
        ) { this .cloning . addStep( stepName
            , stepDuration);} else if( step
                instanceofAbstractRepairStep) { if (this.fixing
            ==
            null){this.fixing=new GlobalStepInfo()
        ; } this .fixing . addStep (
                stepName,stepDuration);}elseif(stepinstanceofTestProject&&(step.getInspector (
                ).getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step.getInspector() .
            getCheckoutType().equals(CheckoutType. CHECKOUT_BUGGY_BUILD_SOURCE_CODE))
        ) { this .testing . addStep (
                stepName,stepDuration);}elseif(stepinstanceofBuildProject&&(step.getInspector (
                ).getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step.getInspector() .
            getCheckoutType().equals(CheckoutType. CHECKOUT_BUGGY_BUILD_SOURCE_CODE))
        ) { this
            . building.addStep(stepName,stepDuration);}else{if( step .getInspector( )
                    .getBuildToBeInspected().getStatus()==ScannedBuildStatus.ONLY_FAIL|| step .getInspector() .
                getBuildToBeInspected () . getStatus( )
                    ==ScannedBuildStatus.FAILING_AND_PASSING){if( stepinstanceofCheckoutBuggyBuild
                )
            { this . building
                . addStep( stepName , stepDuration ) ; } }
                        else { // PASSING_AND_PASSING_WITH_TEST_CHANGES if ( step instanceofCheckoutPatchedBuild ||
                    stepinstanceofComputeSourceDir||stepinstanceofComputeTestDir|| stepinstanceofCheckoutBuggyBuildSourceCode
                )
            {
        this
    .
building
