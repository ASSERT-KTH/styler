package fr.inria.spirals.repairnator.

process .step;importfr.inria.spirals.repairnator.process.
inspectors .ProjectInspector;importfr.inria.spirals.repairnator.process.
maven .MavenHelper;importfr.inria.spirals.repairnator.

states .PipelineState;importjava.

util
. Properties ; /**
 * Created by bloriot97 on 04/10/2018.
 * The idea is to run a maven build with checkstyle:checkstyle
 * to check for CS errors.
 */ public class

    Checkstyle extendsAbstractStep{ publicCheckstyle (
        ProjectInspectorinspector){ super(inspector
    ,

    true );} publicCheckstyle ( ProjectInspectorinspector , booleanblockingStep ,
        StringstepName){ super( inspector,blockingStep
    ,


    stepName ) ;}protected StepStatus
        businessExecute(){this.getLogger().debug

        ( "Run checkstyle on the project" ) ; Propertiesproperties=new
        Properties();properties.setProperty( MavenHelper.SKIP_TEST_PROPERTY

        , "true" ) ; MavenHelperhelper=newMavenHelper(this. getPom( ), "checkstyle:checkstyle",properties,this.getClass(). getSimpleName(),this. getInspector( ),true

        , false)
        ; int
            result ; try{result=helper.
        run ( ); }catch (
            InterruptedExceptione){this. addStepError("Error while running checkstyle"
            , e );result=
        MavenHelper

        . MAVEN_ERROR; } if(result== MavenHelper
            . MAVEN_SUCCESS){returnStepStatus. buildError(this,PipelineState
        . NOTFAILING )
            ;}else{this . addStepError("Repository "+this.getInspector() . getRepoSlug()
            + " has (maybe ?) checkstyle errors.");returnStepStatus. buildError(this,PipelineState
        .
    CHECKSTYLE_ERRORS

)
