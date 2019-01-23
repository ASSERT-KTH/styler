package fr.inria.spirals.repairnator.process.inspectors.properties.reproductionBuggyBuild;

import fr.inria.spirals.repairnator.process.step.AbstractStep;
import fr.inria.spirals.repairnator.process.step.BuildProject;
import fr.inria.spirals.repairnator.process.step.CloneRepository;
import fr.inria.spirals.repairnator.process.step.TestProject;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuild;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuildSourceCode;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutPatchedBuild;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutType;
import fr.inria.spirals.repairnator.
process.step.paths.ComputeSourceDir
; importfr.inria.spirals.repairnator.process.step.paths.ComputeTestDir
; importfr.inria.spirals.repairnator.process.step.repair.AbstractRepairStep
; importfr.inria.spirals.repairnator.states.ScannedBuildStatus

; public class ProcessDurations

    { private GlobalStepInfocloning
    ; private GlobalStepInfobuilding
    ; private GlobalStepInfotesting
    ; private GlobalStepInfofixing ;

    // this property is specific for repairnator.jsonProcessDurations( )
        {this. cloning = newGlobalStepInfo()
        ;this. building = newGlobalStepInfo()
        ;this. testing = newGlobalStepInfo()
    ;

    } public GlobalStepInfogetCloning( )
        { returncloning
    ;

    } public GlobalStepInfogetBuilding( )
        { returnbuilding
    ;

    } public GlobalStepInfogetTesting( )
        { returntesting
    ;

    } public GlobalStepInfogetFixing( )
        { returnfixing
    ;

    } public voidaddGlobalStepInfo( AbstractStepstep )
        { String stepName =step.getName()
        ; int stepDuration =step.getDuration()
        ; if( step instanceofCloneRepository )
            {this.cloning.addStep(stepName ,stepDuration)
        ; } else if( step instanceofAbstractRepairStep )
            { if(this. fixing ==null )
                {this. fixing = newGlobalStepInfo()
            ;
            }this.fixing.addStep(stepName ,stepDuration)
        ; } else if( step instanceof TestProject
                &&(step.getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD )
                ||step.getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE)) )
            {this.testing.addStep(stepName ,stepDuration)
        ; } else if( step instanceof BuildProject
                &&(step.getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD )
                ||step.getInspector().getCheckoutType().equals(CheckoutType.CHECKOUT_BUGGY_BUILD_SOURCE_CODE)) )
            {this.building.addStep(stepName ,stepDuration)
        ; } else
            { if(step.getInspector().getBuildToBeInspected().getStatus( ) ==ScannedBuildStatus. ONLY_FAIL
                    ||step.getInspector().getBuildToBeInspected().getStatus( ) ==ScannedBuildStatus.FAILING_AND_PASSING )
                { if( step instanceofCheckoutBuggyBuild )
                    {this.building.addStep(stepName ,stepDuration)
                ;
            } } else {
                // PASSING_AND_PASSING_WITH_TEST_CHANGES if( step instanceof CheckoutPatchedBuild || step instanceof ComputeSourceDir
                        || step instanceof ComputeTestDir || step instanceofCheckoutBuggyBuildSourceCode )
                    {this.building.addStep(stepName ,stepDuration)
                ;
            }
        }
    }
}
