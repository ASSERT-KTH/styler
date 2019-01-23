package fr.inria.spirals.repairnator.serializer;

import fr.inria.spirals.repairnator.process.inspectors.JobStatus;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector4Bears;
import fr.inria.spirals.repairnator.process.step.
StepStatus ;importfr.inria.spirals.repairnator.serializer.engines.

SerializerEngine ;importjava.util.


List
; /**
 * Created by urli on 20/01/2017.
 */ public abstract class AbstractDataSerializer extends



    Serializer {publicAbstractDataSerializer(List< SerializerEngine> engines ,SerializerType type
        ){super( engines,type
    )

    ; } public staticStringgetPrettyPrintState (ProjectInspector inspector

        ) { JobStatus jobStatus=inspector.getJobStatus(

        ) ;if ( inspectorinstanceof ProjectInspector4Bears
            ) { ProjectInspector4Bears inspector4Bears=( ProjectInspector4Bears)
            inspector ;if(inspector4Bears.isBug( )
                ) {returninspector4Bears.getBugType(
            ) ; } elseif(inspector4Bears.getJobStatus().isReproducedAsFail( )
                ) {return
            "BUG REPRODUCED"
        ;

        } }if(jobStatus.isHasBeenPatched( )
            ) {return
        "PATCHED"

        ; }if(jobStatus.isReproducedAsFail( )
            ) {return
        "test failure"

        ;}List< StepStatus > stepStatuses=jobStatus.getStepStatuses(

        ) ;for ( int i=stepStatuses.size()- 1 ; i>= 0;i --
            ) { StepStatus stepStatus=stepStatuses.get(i
            ) ;if(stepStatus.getStatus ( )==StepStatus.StatusKind. FAILURE
                ) {returnstepStatus.getDiagnostic(
            )
        ;

        } }return
    "UNKNOWN"

    ; } public abstractvoidserializeData (ProjectInspectorinspector
)
