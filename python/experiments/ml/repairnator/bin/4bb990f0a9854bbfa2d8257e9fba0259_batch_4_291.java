package fr.inria.spirals.repairnator.process.inspectors.properties.reproductionBuggyBuild;

import fr.inria.spirals.repairnator.process.step.AbstractStep;
import fr.inria.spirals.repairnator.process.

step .BuildProject;importfr.inria.spirals.repairnator.process.
step .CloneRepository;importfr.inria.spirals.repairnator.process.
step .TestProject;importfr.inria.spirals.repairnator.process.step.
checkoutrepository .CheckoutBuggyBuild;importfr.inria.spirals.repairnator.process.step.
checkoutrepository .CheckoutBuggyBuildSourceCode;importfr.inria.spirals.repairnator.process.step.
checkoutrepository .CheckoutPatchedBuild;importfr.inria.spirals.repairnator.process.step.
checkoutrepository .CheckoutType;importfr.inria.spirals.repairnator.process.step.
paths .ComputeSourceDir;importfr.inria.spirals.repairnator.process.step.
paths .ComputeTestDir;importfr.inria.spirals.repairnator.process.step.
repair .AbstractRepairStep;importfr.inria.spirals.repairnator.

states . ScannedBuildStatus ;

    public class ProcessDurations{
    private GlobalStepInfo cloning;
    private GlobalStepInfo building;
    private GlobalStepInfo testing; private

    GlobalStepInfofixing; // this property is specific for repairnator.json
        ProcessDurations() { this .cloning=new
        GlobalStepInfo() ; this .building=new
        GlobalStepInfo() ; this .testing=new
    GlobalStepInfo

    ( ) ;}public GlobalStepInfo
        getCloning ()
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

    return fixing ;}public voidaddGlobalStepInfo (
        AbstractStep step ) {StringstepName=step.
        getName ( ) ;intstepDuration=step.
        getDuration () ; if( step
            instanceofCloneRepository){this.cloning. addStep(stepName
        , stepDuration ) ;} else if( step
            instanceof AbstractRepairStep){if ( this. fixing
                ==null) { this .fixing=new
            GlobalStepInfo
            ();}this.fixing. addStep(stepName
        , stepDuration ) ;} else if (
                stepinstanceofTestProject&&(step.getInspector().getCheckoutType().equals( CheckoutType
                .CHECKOUT_BUGGY_BUILD)||step.getInspector().getCheckoutType().equals(CheckoutType. CHECKOUT_BUGGY_BUILD_SOURCE_CODE
            ))){this.testing. addStep(stepName
        , stepDuration ) ;} else if (
                stepinstanceofBuildProject&&(step.getInspector().getCheckoutType().equals( CheckoutType
                .CHECKOUT_BUGGY_BUILD)||step.getInspector().getCheckoutType().equals(CheckoutType. CHECKOUT_BUGGY_BUILD_SOURCE_CODE
            ))){this.building. addStep(stepName
        , stepDuration )
            ; }else{if(step.getInspector().getBuildToBeInspected() . getStatus() ==
                    ScannedBuildStatus.ONLY_FAIL||step.getInspector().getBuildToBeInspected() . getStatus()== ScannedBuildStatus
                . FAILING_AND_PASSING) { if( step
                    instanceofCheckoutBuggyBuild){this.building. addStep(stepName
                ,
            stepDuration ) ; }
                } else{ // PASSING_AND_PASSING_WITH_TEST_CHANGES if ( step instanceof CheckoutPatchedBuild ||
                        step instanceof ComputeSourceDir || step instanceof ComputeTestDir|| step
                    instanceofCheckoutBuggyBuildSourceCode){this.building. addStep(stepName
                ,
            stepDuration
        )
    ;
}
