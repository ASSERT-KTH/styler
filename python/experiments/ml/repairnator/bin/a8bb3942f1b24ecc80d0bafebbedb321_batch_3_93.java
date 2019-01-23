package fr.inria.spirals.repairnator.process.step.checkoutrepository;

import fr.inria.spirals.repairnator.process.

step.StepStatus;importfr.inria
. spirals.repairnator.states.PipelineState;importfr.inria
. spirals.repairnator.process.inspectors.ProjectInspector;publicclassCheckoutBuggyBuildTestCodeextends

CheckoutRepository { public CheckoutBuggyBuildTestCode ( ProjectInspector

    inspector ,booleanblockingStep ){ super (inspector ,
        blockingStep);} protectedStepStatusbusinessExecute
    (

    ) { this.getLogger (
        ).debug("Checking out the test code of the buggy build candidate...");if(this.

        getInspector ().getJobStatus().getTestDir()==null){ this .addStepError (
            "Test code dir is null: it is therefore impossible to continue.");returnStepStatus.buildError
            ( this,PipelineState.TESTDIRNOTCOMPUTED) ;}super.setCheckoutType
        (

        CheckoutType.CHECKOUT_BUGGY_BUILD_TEST_CODE);StepStatusstepStatus=super

        . businessExecute ( );this.getInspector(

        ).setCheckoutType(CheckoutType.CHECKOUT_BUGGY_BUILD_TEST_CODE);returnstepStatus;}

        } 