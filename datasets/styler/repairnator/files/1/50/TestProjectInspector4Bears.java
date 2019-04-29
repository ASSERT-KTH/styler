package fr.inria.spirals.repairnator.process.inspectors;

import ch.qos.logback.classic.Level;
import fr.inria.jtravis.entities.Build;
import fr.inria.jtravis.helpers.BuildHelper;
import fr.inria.spirals.repairnator.BuildToBeInspected;
import fr.inria.spirals.repairnator.states.LauncherMode;
import fr.inria.spirals.repairnator.states.PipelineState;
import fr.inria.spirals.repairnator.states.PushState;
import fr.inria.spirals.repairnator.states.ScannedBuildStatus;
import fr.inria.spirals.repairnator.Utils;
import fr.inria.spirals.repairnator.config.RepairnatorConfig;
import fr.inria.spirals.repairnator.notifier.AbstractNotifier;
import fr.inria.spirals.repairnator.notifier.FixerBuildNotifier;
import fr.inria.spirals.repairnator.notifier.engines.NotifierEngine;
import fr.inria.spirals.repairnator.serializer.AbstractDataSerializer;
import fr.inria.spirals.repairnator.serializer.InspectorSerializer4Bears;
import fr.inria.spirals.repairnator.serializer.SerializerType;
import fr.inria.spirals.repairnator.serializer.engines.SerializedData;
import fr.inria.spirals.repairnator.serializer.engines.SerializerEngine;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by urli on 24/04/2017.
 */
public class TestProjectInspector4Bears {

    private static final String SOLVER_PATH_DIR = "src/test/resources/z3/";
    private static final String SOLVER_NAME_LINUX = "z3_for_linux";
    private static final String SOLVER_NAME_MAC = "z3_for_mac";

    @Before
    public void setUp() {
        String solverPath;
        if (isMac()) {
            solverPath = SOLVER_PATH_DIR+SOLVER_NAME_MAC;
        } else {
            solverPath = SOLVER_PATH_DIR+SOLVER_NAME_LINUX;
        }

        RepairnatorConfig config = RepairnatorConfig.getInstance();
        config.setZ3solverPath(solverPath);
        config.setPush(true);
        config.setPushRemoteRepo("");
        Utils.setLoggersLevel(Level.ERROR);
    }

    public static boolean isMac() {
        String OS = System.getProperty("os.name").toLowerCase();
        return (OS.contains("mac"));
    }

    @After
    public void tearDown() {
        RepairnatorConfig.deleteInstance();
    }

    @Test
    public void testFailingPassingProject() throws IOException, GitAPIException {
        int buildIdPassing = 203800961;
        int buildIdFailing = 203797975;


        Path tmpDirPath = Files.createTempDirectory("test_bears1");
        File tmpDir = tmpDirPath.toFile();
        tmpDir.deleteOnExit();

        Optional<Build> optionalBuildPassing = RepairnatorConfig.getInstance().getJTravis().build().fromId(buildIdPassing);
        assertTrue(optionalBuildPassing.isPresent());
        Build passingBuild = optionalBuildPassing.get();

        Optional<Build> optionalBuildFailing = RepairnatorConfig.getInstance().getJTravis().build().fromId(buildIdFailing);
        assertTrue(optionalBuildFailing.isPresent());
        Build failingBuild = optionalBuildFailing.get();


        BuildToBeInspected buildToBeInspected = new BuildToBeInspected(failingBuild, passingBuild, ScannedBuildStatus.FAILING_AND_PASSING, "test");

        List<AbstractDataSerializer> serializers = new ArrayList<>();
        List<AbstractNotifier> notifiers = new ArrayList<>();

        List<SerializerEngine> serializerEngines = new ArrayList<>();
        SerializerEngine serializerEngine = mock(SerializerEngine.class);
        serializerEngines.add(serializerEngine);

        List<NotifierEngine> notifierEngines = new ArrayList<>();
        NotifierEngine notifierEngine = mock(NotifierEngine.class);
        notifierEngines.add(notifierEngine);

        serializers.add(new InspectorSerializer4Bears(serializerEngines));

        notifiers.add(new FixerBuildNotifier(notifierEngines));

        RepairnatorConfig config = RepairnatorConfig.getInstance();
        config.setLauncherMode(LauncherMode.BEARS);

        ProjectInspector4Bears inspector = new ProjectInspector4Bears(buildToBeInspected, tmpDir.getAbsolutePath(), serializers, notifiers);
        inspector.run();

        JobStatus jobStatus = inspector.getJobStatus();
        assertThat(jobStatus.getPipelineState(), is(PipelineState.FIXERBUILDCASE1));
        assertThat(jobStatus.getPushState(), is(PushState.PATCH_COMMITTED));
        assertThat(inspector.isFixerBuildCase1(), is(true));
        assertThat(jobStatus.getFailureLocations().size(), is(1));
        assertThat(jobStatus.getMetrics().getFailureNames().size(), is(1));

        verify(notifierEngine, times(1)).notify(anyString(), anyString());
        verify(serializerEngine, times(1)).serialize(anyListOf(SerializedData.class), eq(SerializerType.INSPECTOR4BEARS));

        Git gitDir = Git.open(new File(inspector.getRepoToPushLocalPath()));
        Iterable<RevCommit> logs = gitDir.log().call();

        Iterator<RevCommit> iterator = logs.iterator();
        assertThat(iterator.hasNext(), is(true));

        RevCommit commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("End of the repairnator process"));

        commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("Human patch"));

        commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("Automatic repair"));

        commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("Bug commit"));

        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void testPassingPassingProject() throws IOException, GitAPIException {
        int buildIdPassing = 201938881;
        int buildIdPreviousPassing = 201938325;


        Path tmpDirPath = Files.createTempDirectory("test_bears2");
        File tmpDir = tmpDirPath.toFile();
        tmpDir.deleteOnExit();

        Optional<Build> optionalBuildPassing = RepairnatorConfig.getInstance().getJTravis().build().fromId(buildIdPassing);
        assertTrue(optionalBuildPassing.isPresent());
        Build passingBuild = optionalBuildPassing.get();

        Optional<Build> optionalPreviousBuildPassing = RepairnatorConfig.getInstance().getJTravis().build().fromId(buildIdPreviousPassing);
        assertTrue(optionalPreviousBuildPassing.isPresent());
        Build previousPassingBuild = optionalPreviousBuildPassing.get();

        BuildToBeInspected buildToBeInspected = new BuildToBeInspected(previousPassingBuild, passingBuild, ScannedBuildStatus.PASSING_AND_PASSING_WITH_TEST_CHANGES, "test");

        List<AbstractDataSerializer> serializers = new ArrayList<>();
        List<AbstractNotifier> notifiers = new ArrayList<>();

        List<SerializerEngine> serializerEngines = new ArrayList<>();
        SerializerEngine serializerEngine = mock(SerializerEngine.class);
        serializerEngines.add(serializerEngine);

        List<NotifierEngine> notifierEngines = new ArrayList<>();
        NotifierEngine notifierEngine = mock(NotifierEngine.class);
        notifierEngines.add(notifierEngine);

        serializers.add(new InspectorSerializer4Bears(serializerEngines));

        notifiers.add(new FixerBuildNotifier(notifierEngines));

        RepairnatorConfig config = RepairnatorConfig.getInstance();
        config.setLauncherMode(LauncherMode.BEARS);

        ProjectInspector4Bears inspector = new ProjectInspector4Bears(buildToBeInspected, tmpDir.getAbsolutePath(), serializers, notifiers);
        inspector.run();

        JobStatus jobStatus = inspector.getJobStatus();
        assertThat(jobStatus.getPipelineState(), is(PipelineState.FIXERBUILDCASE2));
        assertThat(jobStatus.getPushState(), is(PushState.PATCH_COMMITTED));
        assertThat(inspector.isFixerBuildCase2(), is(true));
        assertThat(jobStatus.getFailureLocations().size(), is(1));
        assertThat(jobStatus.getMetrics().getFailureNames().size(), is(1));

        verify(notifierEngine, times(1)).notify(anyString(), anyString());
        verify(serializerEngine, times(1)).serialize(anyListOf(SerializedData.class), eq(SerializerType.INSPECTOR4BEARS));

        Git gitDir = Git.open(new File(inspector.getRepoToPushLocalPath()));
        Iterable<RevCommit> logs = gitDir.log().call();

        Iterator<RevCommit> iterator = logs.iterator();
        assertThat(iterator.hasNext(), is(true));

        RevCommit commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("End of the repairnator process"));

        commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("Human patch"));

        commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("Automatic repair"));

        commit = iterator.next();
        assertThat(commit.getShortMessage(), containsString("Bug commit"));

        assertThat(iterator.hasNext(), is(false));
    }
}
