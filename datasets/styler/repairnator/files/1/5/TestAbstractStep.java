package fr.inria.spirals.repairnator.process.step;

import ch.qos.logback.classic.Level;
import fr.inria.spirals.repairnator.states.PipelineState;
import fr.inria.spirals.repairnator.Utils;
import fr.inria.spirals.repairnator.config.RepairnatorConfig;
import fr.inria.spirals.repairnator.process.inspectors.JobStatus;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by urli on 21/02/2017.
 */
public class TestAbstractStep {

    public class AbstractStepNop extends AbstractStep {

        public AbstractStepNop(ProjectInspector inspector) {
            super(inspector);
        }

        @Override
        protected void businessExecute() {
            // do nothing
        }
    }

    @Before
    public void setup() {
        Utils.setLoggersLevel(Level.ERROR);
    }

    @After
    public void tearDown() {
        RepairnatorConfig.deleteInstance();
    }

    @Test
    public void testSetPropertiesWillGivePropertiesToOtherSteps() {
        ProjectInspector mockInspector = mock(ProjectInspector.class);
        JobStatus jobStatus = new JobStatus("");
        when(mockInspector.getJobStatus()).thenReturn(jobStatus);

        AbstractStep step1 = new AbstractStepNop(mockInspector);
        AbstractStep step2 = new AbstractStepNop(mockInspector);
        AbstractStep step3 = new AbstractStepNop(mockInspector);

        Properties properties = new Properties();
        properties.setProperty("testvalue", "toto");
        properties.setProperty("anotherone","foo");

        step1.setNextStep(step2).setNextStep(step3);
        step1.setProperties(properties);

        assertThat(step3.getProperties(), is(properties));
    }

    @Test
    public void testSetStateWillGiveStateToOtherSteps() {
        ProjectInspector mockInspector = mock(ProjectInspector.class);
        JobStatus jobStatus = new JobStatus("");
        when(mockInspector.getJobStatus()).thenReturn(jobStatus);

        AbstractStep step1 = new AbstractStepNop(mockInspector);
        AbstractStep step2 = new AbstractStepNop(mockInspector);
        AbstractStep step3 = new AbstractStepNop(mockInspector);



        PipelineState state = PipelineState.NOTFAILING;

        step1.setNextStep(step2).setNextStep(step3);
        step1.setPipelineState(state);

        assertThat(step3.getPipelineState(), is(state));
    }

    @Test
    public void testGetPomOnSimpleProject() {
        ProjectInspector mockInspector = mock(ProjectInspector.class);

        String localRepoPath = "./src/test/resources/test-abstractstep/simple-maven-project";
        when(mockInspector.getRepoLocalPath()).thenReturn(localRepoPath);

        JobStatus jobStatus = new JobStatus(localRepoPath);
        when(mockInspector.getJobStatus()).thenReturn(jobStatus);

        AbstractStep step1 = new AbstractStepNop(mockInspector);

        String expectedPomPath = localRepoPath+"/pom.xml";

        assertThat(step1.getPom(), is(expectedPomPath));
    }

    @Test
    public void testGetPomWhenNotFoundShouldSetStopFlag() {
        ProjectInspector mockInspector = mock(ProjectInspector.class);

        String localRepoPath = "./unkown-path";
        when(mockInspector.getRepoLocalPath()).thenReturn(localRepoPath);

        JobStatus jobStatus = new JobStatus(localRepoPath);
        when(mockInspector.getJobStatus()).thenReturn(jobStatus);

        AbstractStep step1 = new AbstractStepNop(mockInspector);

        String expectedPomPath = localRepoPath+"/pom.xml";

        // return this path but set the flag to stop
        assertThat(step1.getPom(), is(expectedPomPath));
        assertThat(step1.shouldStop, is(true));
    }

    @Test
    public void testGetPomWithComplexMavenProjectShouldSetRepoPath() {
        ProjectInspector mockInspector = mock(ProjectInspector.class);

        String localRepoPath = "./src/test/resources/test-abstractstep/complex-maven-project";
        when(mockInspector.getRepoLocalPath()).thenReturn(localRepoPath);

        JobStatus jobStatus = new JobStatus(localRepoPath);
        when(mockInspector.getJobStatus()).thenReturn(jobStatus);

        AbstractStep step1 = new AbstractStepNop(mockInspector);

        String expectedPomPath = localRepoPath+"/a-submodule";

        String obtainedPom = step1.getPom();
        assertThat(jobStatus.getPomDirPath(), is(expectedPomPath));
    }

}
