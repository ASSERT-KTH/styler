package fr.inria.spirals.repairnator.process.step;

import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import fr.inria.spirals.repairnator.process.maven.MavenHelper;
import fr.inria.spirals.repairnator.states.PipelineState;

import java.util.Properties;

/**
 * Created by bloriot97 on 04/10/2018.
 * The idea is to run a maven build with checkstyle:checkstyle
 * to check for CS errors.
 */
public class Checkstyle extends AbstractStep {

    public Checkstyle(ProjectInspectorinspector ){super( inspector,true
)

; }publicCheckstyle (ProjectInspector inspector ,boolean blockingStep ,String stepName
    ){super( inspector, blockingStep,stepName
)


; } protectedStepStatusbusinessExecute (
    ){this.getLogger().debug("Run checkstyle on the project"

    ) ; Properties properties =newProperties(
    );properties.setProperty(MavenHelper. SKIP_TEST_PROPERTY,"true"

    ) ; MavenHelper helper =newMavenHelper(this.getPom( ), "checkstyle:checkstyle", properties,this.getClass().getSimpleName( ),this.getInspector( ), true,false

    ) ;int
    result ;
        try { result=helper.run(
    ) ; }catch (InterruptedException e
        ){this.addStepError( "Error while running checkstyle",e
        ) ; result=MavenHelper.
    MAVEN_ERROR

    ; }if ( result==MavenHelper. MAVEN_SUCCESS
        ) {returnStepStatus.buildError( this,PipelineState.NOTFAILING
    ) ; }
        else{this.addStepError ( "Repository "+this.getInspector().getRepoSlug ( )+" has (maybe ?) checkstyle errors."
        ) ;returnStepStatus.buildError( this,PipelineState.CHECKSTYLE_ERRORS
    )
;

}
