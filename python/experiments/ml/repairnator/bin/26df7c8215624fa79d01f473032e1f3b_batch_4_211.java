package fr.inria.spirals.repairnator.process.inspectors.properties.reproductionBuggyBuild;

import fr.inria.spirals.repairnator.process.step.AbstractStep;
import fr.inria.spirals.repairnator.process.step.BuildProject;
import fr.inria.spirals.repairnator.process.
step.CloneRepository;importfr
. inria.spirals.repairnator.process.step.TestProject;importfr
. inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuild;importfr
. inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuildSourceCode;importfr
. inria.spirals.repairnator.process.step.checkoutrepository.CheckoutPatchedBuild;importfr
. inria.spirals.repairnator.process.step.checkoutrepository.CheckoutType;importfr
. inria.spirals.repairnator.process.step.paths.ComputeSourceDir;importfr
. inria.spirals.repairnator.process.step.paths.ComputeTestDir;importfr
. inria.spirals.repairnator.process.step.repair.AbstractRepairStep;importfr
. inria.spirals.repairnator.states.ScannedBuildStatus;publicclass

ProcessDurations { private GlobalStepInfo

    cloning ; privateGlobalStepInfo
    building ; privateGlobalStepInfo
    testing ; privateGlobalStepInfo
    fixing ; // this property is specific for repairnator.jsonProcessDurations (

    ){this .
        cloning=new GlobalStepInfo ( );this.
        building=new GlobalStepInfo ( );this.
        testing=new GlobalStepInfo ( );}public
    GlobalStepInfo

    getCloning ( ){return cloning
        ; }public
    GlobalStepInfo

    getBuilding ( ){return building
        ; }public
    GlobalStepInfo

    getTesting ( ){return testing
        ; }public
    GlobalStepInfo

    getFixing ( ){return fixing
        ; }public
    void

    addGlobalStepInfo ( AbstractStepstep) {String stepName
        = step . getName();intstepDuration
        = step . getDuration();if(
        step instanceofCloneRepository ) {this .
            cloning.addStep(stepName,stepDuration) ;}else
        if ( step instanceofAbstractRepairStep ) {if (
            this .fixing==null ) {this .
                fixing=new GlobalStepInfo ( );}this
            .
            fixing.addStep(stepName,stepDuration) ;}else
        if ( step instanceofTestProject && ( step
                .getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step .
                getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE))){this .
            testing.addStep(stepName,stepDuration) ;}else
        if ( step instanceofBuildProject && ( step
                .getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD)||step .
                getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE))){this .
            building.addStep(stepName,stepDuration) ;}else
        { if (
            step .getInspector().getBuildToBeInspected().getStatus()==ScannedBuildStatus . ONLY_FAIL||step .
                    getInspector().getBuildToBeInspected().getStatus()==ScannedBuildStatus . FAILING_AND_PASSING){if (
                step instanceofCheckoutBuggyBuild ) {this .
                    building.addStep(stepName,stepDuration) ;}}
                else
            { // PASSING_AND_PASSING_WITH_TEST_CHANGES if (
                step instanceofCheckoutPatchedBuild || step instanceof ComputeSourceDir || step instanceof
                        ComputeTestDir || step instanceof CheckoutBuggyBuildSourceCode ) {this .
                    building.addStep(stepName,stepDuration) ;}}
                }
            }
        }
    