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
import fr.inria.spirals.repairnator.process.step.repair.AbstractRepairStep;
import fr.inria.
spirals.repairnator.states.

ScannedBuildStatus ; public class

    ProcessDurations { privateGlobalStepInfo
    cloning ; privateGlobalStepInfo
    building ; privateGlobalStepInfo
    testing ; privateGlobalStepInfo fixing

    ;// this property is specific for repairnator.jsonProcessDurations (
        ){this . cloning =newGlobalStepInfo(
        );this . building =newGlobalStepInfo(
        );this . testing =newGlobalStepInfo(
    )

    ; } publicGlobalStepInfogetCloning (
        ) {return
    cloning

    ; } publicGlobalStepInfogetBuilding (
        ) {return
    building

    ; } publicGlobalStepInfogetTesting (
        ) {return
    testing

    ; } publicGlobalStepInfogetFixing (
        ) {return
    fixing

    ; } publicvoidaddGlobalStepInfo (AbstractStep step
        ) { String stepName=step.getName(
        ) ; int stepDuration=step.getDuration(
        ) ;if ( stepinstanceof CloneRepository
            ){this.cloning.addStep( stepName,stepDuration
        ) ; } elseif ( stepinstanceof AbstractRepairStep
            ) {if(this . fixing== null
                ){this . fixing =newGlobalStepInfo(
            )
            ;}this.fixing.addStep( stepName,stepDuration
        ) ; } elseif ( step instanceof
                TestProject&&(step.getInspector().getCheckoutType().equals(CheckoutType. CHECKOUT_BUGGY_BUILD
                )||step.getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE) )
            ){this.testing.addStep( stepName,stepDuration
        ) ; } elseif ( step instanceof
                BuildProject&&(step.getInspector().getCheckoutType().equals(CheckoutType. CHECKOUT_BUGGY_BUILD
                )||step.getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE) )
            ){this.building.addStep( stepName,stepDuration
        ) ; }
            else {if(step.getInspector().getBuildToBeInspected().getStatus ( )==ScannedBuildStatus .
                    ONLY_FAIL||step.getInspector().getBuildToBeInspected().getStatus ( )==ScannedBuildStatus. FAILING_AND_PASSING
                ) {if ( stepinstanceof CheckoutBuggyBuild
                    ){this.building.addStep( stepName,stepDuration
                )
            ; } } else
                { // PASSING_AND_PASSING_WITH_TEST_CHANGESif ( step instanceof CheckoutPatchedBuild || step instanceof
                        ComputeSourceDir || step instanceof ComputeTestDir || stepinstanceof CheckoutBuggyBuildSourceCode
                    ){this.building.addStep( stepName,stepDuration
                )
            ;
        }
    }
}
