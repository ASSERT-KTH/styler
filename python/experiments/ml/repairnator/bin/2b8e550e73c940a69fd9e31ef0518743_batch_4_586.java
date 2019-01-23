package fr.inria.spirals.repairnator.process.step.checkoutrepository; importfr.inria.spirals .repairnator.process .step.StepStatus;

import fr.inria.spirals.repairnator.states.PipelineState;importfr
. inria.spirals.repairnator.process.inspectors.ProjectInspector;
public classCheckoutBuggyBuildTestCodeextendsCheckoutRepository{publicCheckoutBuggyBuildTestCode(ProjectInspectorinspector,booleanblockingStep)

{ super ( inspector , blockingStep

    ) ;}protected StepStatusbusinessExecute ( ){ this
        .getLogger() .debug(
    "Checking out the test code of the buggy build candidate..."

    ) ; if(this .
        getInspector().getJobStatus().getTestDir()

        == null){this.addStepError("Test code dir is null: it is therefore impossible to continue.");returnStepStatus.buildError ( this, PipelineState
            .TESTDIRNOTCOMPUTED);}super.
            setCheckoutType (CheckoutType.CHECKOUT_BUGGY_BUILD_TEST_CODE); StepStatusstepStatus=super.
        businessExecute

        ();this.getInspector().

        setCheckoutType ( CheckoutType .CHECKOUT_BUGGY_BUILD_TEST_CODE);returnstepStatus

        ;}}