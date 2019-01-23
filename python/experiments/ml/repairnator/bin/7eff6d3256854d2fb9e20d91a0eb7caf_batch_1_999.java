package fr.inria.spirals.repairnator.serializer;

import fr.inria.spirals.repairnator.process.inspectors.JobStatus;
import fr. inria.spirals.repairnator.process.inspectors.ProjectInspector;importfr
. inria.spirals.repairnator.process.inspectors.ProjectInspector4Bears;importfr
. inria.spirals.repairnator.process.step.StepStatus;importfr

. inria.spirals.repairnator.


serializer
. engines . SerializerEngine ; import java



    . util.List;/**
 * Created by urli on 20/01/2017.
 */public abstractclass AbstractDataSerializer extendsSerializer {
        publicAbstractDataSerializer(List <SerializerEngine>
    engines

    , SerializerType type ){super (engines ,

        type ) ; }publicstaticStringgetPrettyPrintState(

        ProjectInspector inspector) { JobStatusjobStatus =
            inspector . getJobStatus (); if(
            inspector instanceofProjectInspector4Bears){ProjectInspector4Bearsinspector4Bears= (
                ProjectInspector4Bears )inspector;if(inspector4Bears
            . isBug ( )){returninspector4Bears.getBugType();} else
                if (inspector4Bears
            .
        getJobStatus

        ( ).isReproducedAsFail()){ return
            "BUG REPRODUCED" ;}
        }

        if (jobStatus.isHasBeenPatched()) {
            return "PATCHED";
        }

        if(jobStatus. isReproducedAsFail ( )){return"test failure";

        } List< StepStatus > stepStatuses=jobStatus.getStepStatuses(); for ( inti =stepStatuses. size
            ( ) - 1;i>=0;i
            -- ){StepStatusstepStatus=stepStatuses . get(i);if (
                stepStatus .getStatus()==StepStatus
            .
        StatusKind

        . FAILURE)
    {

    return stepStatus . getDiagnostic() ;}}
return
