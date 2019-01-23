package fr.inria.spirals.repairnator.process.inspectors.properties.reproductionBuggyBuild;

import fr.inria.spirals.repairnator.process.step.AbstractStep;
import fr.inria.spirals.repairnator.process.step.BuildProject;
import fr.inria.spirals.repairnator.process.step.CloneRepository;
import fr.inria.spirals.repairnator.process.step.TestProject;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuild;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuildSourceCode;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutPatchedBuild;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.

CheckoutType;importfr.inria.spirals
. repairnator.process.step.paths.ComputeSourceDir;importfr.inria.spirals
. repairnator.process.step.paths.ComputeTestDir;importfr.inria.spirals
. repairnator.process.step.repair.AbstractRepairStep;importfr.inria.spirals
. repairnator.states.ScannedBuildStatus;publicclassProcessDurations{privateGlobalStepInfo

cloning ; private GlobalStepInfo

building ; privateGlobalStepInfo
testing ; privateGlobalStepInfo
fixing ; // this property is specific for repairnator.jsonProcessDurations
( ) {this .

cloning=new GlobalStepInfo
(); this . building=newGlobalStepInfo
(); this . testing=newGlobalStepInfo
(); } public GlobalStepInfogetCloning()
{

return cloning ;}public GlobalStepInfo
getBuilding ()
{

return building ;}public GlobalStepInfo
getTesting ()
{

return testing ;}public GlobalStepInfo
getFixing ()
{

return fixing ;}public void
addGlobalStepInfo (AbstractStep
step

) { StringstepName= step. getName
( ) ; intstepDuration=step.getDuration
( ) ; if(stepinstanceofCloneRepository)
{ this. cloning .addStep (
    stepName,stepDuration);}elseif (stepinstanceof
AbstractRepairStep ) { if( this .fixing ==
    null ){this. fixing =new GlobalStepInfo
        (); } this .fixing.addStep
    (
    stepName,stepDuration);}elseif (stepinstanceof
TestProject && ( step. getInspector ( )
        .getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step.getInspector() .
        getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE))){this.testing.addStep (
    stepName,stepDuration);}elseif (stepinstanceof
BuildProject && ( step. getInspector ( )
        .getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step.getInspector() .
        getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE))){this.building.addStep (
    stepName,stepDuration);}else{ if(step
. getInspector (
    ) .getBuildToBeInspected().getStatus()==ScannedBuildStatus.ONLY_FAIL||step . getInspector() .
            getBuildToBeInspected().getStatus()==ScannedBuildStatus.FAILING_AND_PASSING){ if (stepinstanceofCheckoutBuggyBuild )
        { this. building .addStep (
            stepName,stepDuration);}}else {// PASSING_AND_PASSING_WITH_TEST_CHANGESif
        (
    step instanceof CheckoutPatchedBuild ||
        step instanceofComputeSourceDir || step instanceof ComputeTestDir || step instanceof
                CheckoutBuggyBuildSourceCode ) { this . building .addStep (
            stepName,stepDuration);}}} }}