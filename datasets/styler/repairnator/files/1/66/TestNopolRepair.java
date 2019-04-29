package fr.inria.spirals.repairnator.process.step;

import ch.qos.logback.classic.Level;
import fr.inria.jtravis.entities.Build;
import fr.inria.jtravis.helpers.BuildHelper;
import fr.inria.spirals.repairnator.BuildToBeInspected;
import fr.inria.spirals.repairnator.states.PipelineState;
import fr.inria.spirals.repairnator.states.ScannedBuildStatus;
import fr.inria.spirals.repairnator.Utils;
import fr.inria.spirals.repairnator.config.RepairnatorConfig;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import fr.inria.spirals.repairnator.process.step.checkoutrepository.CheckoutBuggyBuild;
import fr.inria.spirals.repairnator.process.step.gatherinfo.BuildShouldFail;
import fr.inria.spirals.repairnator.process.step.gatherinfo.GatherTestInformation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by urli on 07/03/2017.
 */
public class TestNopolRepair {

    private static final String SOLVER_PATH_DIR = "src/test/resources/z3/";
    private static final String SOLVER_NAME_LINUX = "z3_for_linux";
    private static final String SOLVER_NAME_MAC = "z3_for_mac";

    @Before
    public void setup() {
        String solverPath;
        if (isMac()) {
            solverPath = SOLVER_PATH_DIR+SOLVER_NAME_MAC;
        } else {
            solverPath = SOLVER_PATH_DIR+SOLVER_NAME_LINUX;
        }

        RepairnatorConfig config = RepairnatorConfig.getInstance();
        config.setZ3solverPath(solverPath);
        Utils.setLoggersLevel(Level.ERROR);
    }

    @After
    public void tearDown() {
        RepairnatorConfig.deleteInstance();
    }

    public static boolean isMac() {
        String OS = System.getProperty("os.name").toLowerCase();
        return (OS.indexOf("mac") >= 0);
    }

    @Test
    public void testNopolRepair() throws IOException {
        int buildId = 207890790; // surli/failingProject build

        Optional<Build> optionalBuild = RepairnatorConfig.getInstance().getJTravis().build().fromId(buildId);
        assertTrue(optionalBuild.isPresent());
        Build build = optionalBuild.get();
        assertThat(build, notNullValue());
        assertThat(buildId, is(build.getId()));

        Path tmpDirPath = Files.createTempDirectory("test_nopolrepair");
        File tmpDir = tmpDirPath.toFile();
        tmpDir.deleteOnExit();
        System.out.println("Dirpath : "+tmpDirPath);

        BuildToBeInspected toBeInspected = new BuildToBeInspected(build, null, ScannedBuildStatus.ONLY_FAIL, "");

        ProjectInspector inspector = new ProjectInspector(toBeInspected, tmpDir.getAbsolutePath(), null, null);

        CloneRepository cloneStep = new CloneRepository(inspector);
        NopolRepair nopolRepair = new NopolRepair(inspector);
        NopolRepair.TOTAL_MAX_TIME = 2;

        cloneStep.setNextStep(new CheckoutBuggyBuild(inspector))
                .setNextStep(new TestProject(inspector))
                .setNextStep(new GatherTestInformation(inspector, new BuildShouldFail(), false))
                .setNextStep(new ComputeClasspath(inspector))
                .setNextStep(new ComputeSourceDir(inspector, false))
                .setNextStep(nopolRepair);
        cloneStep.execute();

        assertThat(nopolRepair.shouldStop, is(false));
        assertThat(nopolRepair.getPipelineState(), is(PipelineState.NOPOL_PATCHED));
        assertThat(nopolRepair.getNopolInformations().size(), is(11));

        // The following assertion is working when the test is launched in the current module
        // however it does not work properly when launched from the root module,
        // so it breaks the CI. The nopol logs should be treaten differently inside Nopol
        //File nopolLog = new File(inspector.getRepoLocalPath(), "repairnator.nopol.log");
        //assertTrue(nopolLog.exists());
    }
}
