package fr.inria.spirals.repairnator.process.inspectors.properties.reproductionBuggyBuild;

import fr.inria.spirals.repairnator.process.step.AbstractStep;
import fr.inria.spirals.repairnator.process.step.BuildProject;
import fr.inria.spirals.repairnator.process.step.CloneRepository;
import fr.inria.spirals.repairnator.process.step.TestProject;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuild;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuildSourceCode;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutPatchedBuild;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutType;
import fr.inria.spirals.repairnator.process.step.paths.ComputeSourceDir;
import fr.inria.spirals.repairnator.process.step.paths.ComputeTestDir;
import fr.inria.spirals.repairnator.process.step.repair.

AbstractRepairStep;importfr.inria.spirals.
repairnator .states.ScannedBuildStatus;publicclassProcessDurations{privateGlobalStepInfocloning

; private GlobalStepInfo building

; private GlobalStepInfotesting
; private GlobalStepInfofixing
; // this property is specific for repairnator.json ProcessDurations(
) { this. cloning

=newGlobalStepInfo (
);this . building =newGlobalStepInfo(
);this . testing =newGlobalStepInfo(
);} public GlobalStepInfo getCloning(){
return

cloning ; }publicGlobalStepInfo getBuilding
( ){
return

building ; }publicGlobalStepInfo getTesting
( ){
return

testing ; }publicGlobalStepInfo getFixing
( ){
return

fixing ; }publicvoid addGlobalStepInfo
( AbstractStepstep
)

{ String stepName=step .getName (
) ; int stepDuration=step.getDuration(
) ; if (stepinstanceofCloneRepository){
this .cloning . addStep( stepName
    ,stepDuration);}elseif( stepinstanceofAbstractRepairStep
) { if (this . fixing== null
    ) {this.fixing = newGlobalStepInfo (
        );} this . fixing.addStep(
    stepName
    ,stepDuration);}elseif( stepinstanceofTestProject
&& ( step .getInspector ( ) .
        getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step.getInspector(). getCheckoutType
        ().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE))){this.testing.addStep( stepName
    ,stepDuration);}elseif( stepinstanceofBuildProject
&& ( step .getInspector ( ) .
        getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step.getInspector(). getCheckoutType
        ().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE))){this.building.addStep( stepName
    ,stepDuration);}else{if (step.
getInspector ( )
    . getBuildToBeInspected().getStatus()==ScannedBuildStatus.ONLY_FAIL||step. getInspector (). getBuildToBeInspected
            ().getStatus()==ScannedBuildStatus.FAILING_AND_PASSING){if ( stepinstanceofCheckoutBuggyBuild) {
        this .building . addStep( stepName
            ,stepDuration);}}else{ // PASSING_AND_PASSING_WITH_TEST_CHANGESif(
        step
    instanceof CheckoutPatchedBuild || step
        instanceof ComputeSourceDir|| step instanceof ComputeTestDir || step instanceof CheckoutBuggyBuildSourceCode
                ) { this . building . addStep( stepName
            ,stepDuration);}}}} }