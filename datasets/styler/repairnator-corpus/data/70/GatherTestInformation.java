package fr.inria.spirals.repairnator.process.step;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.surefire.report.ReportTestCase;
import org.apache.maven.plugins.surefire.report.ReportTestSuite;
import org.apache.maven.plugins.surefire.report.SurefireReportParser;
import org.apache.maven.reporting.MavenReportException;

import fr.inria.spirals.repairnator.process.ProjectState;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import fr.inria.spirals.repairnator.process.testinformation.FailureLocation;
import fr.inria.spirals.repairnator.process.testinformation.FailureType;

/**
 * Created by urli on 05/01/2017.
 */
public class GatherTestInformation extends AbstractStep {

    private static final String SUREFIREREPORT_PATH = "/target/surefire-reports";

    private int nbTotalTests;
    private int nbSkippingTests;
    private int nbFailingTests;
    private int nbErroringTests;
    private Set<FailureLocation> failureLocations;
    private Set<String> failureNames;
    private String failingModulePath;

    private ContractForGatherTestInformation contract;

    public GatherTestInformation(ProjectInspector inspector, ContractForGatherTestInformation contract) {
        super(inspector);
        this.failureLocations = new HashSet<>();
        this.failureNames = new HashSet<>();
        this.contract = contract;
    }

    public Set<FailureLocation> getFailureLocations() {
        return failureLocations;
    }

    public Set<String> getFailureNames() {
        return failureNames;
    }

    public int getNbFailingTests() {
        return nbFailingTests;
    }

    public int getNbErroringTests() {
        return nbErroringTests;
    }

    public int getNbTotalTests() {
        return nbTotalTests;
    }

    public int getNbSkippingTests() {
        return nbSkippingTests;
    }

    public String getFailingModulePath() {
        return failingModulePath;
    }

    @Override
    protected void businessExecute() {
        this.getLogger().debug("Start gathering test information...");
        File rootRepo = new File(this.inspector.getRepoLocalPath());
        final List<File> surefireDirs = new ArrayList<File>();

        try {
            Files.walkFileTree(rootRepo.toPath(), new SimpleFileVisitor<Path>() {
                public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(SUREFIREREPORT_PATH)) {
                        surefireDirs.add(file.toFile());
                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }
            });
        } catch (IOException e) {
            this.getLogger().warn("Error while traversing files to get surefire reports: " + e);
            this.addStepError(e.getMessage());
        }

        for (File surefireDir : surefireDirs) {
            SurefireReportParser parser = new SurefireReportParser(Arrays.asList(new File[] { surefireDir }),
                    Locale.ENGLISH, null);
            try {
                List<ReportTestSuite> testSuites = parser.parseXMLReportFiles();
                for (ReportTestSuite testSuite : testSuites) {
                    if (testSuite.getNumberOfFailures() > 0) {
                        File failingModule = surefireDir.getParentFile().getParentFile();
                        this.failingModulePath = failingModule.getCanonicalPath();
                        this.writeProperty("failingModule", this.failingModulePath);
                    }

                    this.nbTotalTests += testSuite.getNumberOfTests();
                    this.nbSkippingTests += testSuite.getNumberOfSkipped();
                    if (testSuite.getNumberOfFailures() > 0 || testSuite.getNumberOfErrors() > 0) {
                        for (ReportTestCase testCase : testSuite.getTestCases()) {
                            if (testCase.hasFailure()) {
                                this.failureNames.add(testCase.getFailureType());
                                FailureType typeTof = new FailureType(testCase.getFailureType(),
                                        testCase.getFailureDetail(), testCase.isError());
                                FailureLocation failureLocation = null;

                                for (FailureLocation location : this.failureLocations) {
                                    if (location.getClassName().equals(testCase.getFullClassName())) {
                                        failureLocation = location;
                                        break;
                                    }
                                }

                                if (failureLocation != null) {
                                    failureLocation.addFailure(typeTof);
                                } else {
                                    failureLocation = new FailureLocation(testCase.getFullClassName());
                                    failureLocation.addFailure(typeTof);
                                    this.failureLocations.add(failureLocation);
                                }
                            }
                        }
                    }
                    this.nbErroringTests += testSuite.getNumberOfErrors();
                    this.nbFailingTests += testSuite.getNumberOfFailures();
                }

                if (this.nbFailingTests > 0) {
                    this.setState(ProjectState.HASTESTFAILURE);
                } else if (this.nbErroringTests > 0) {
                    this.setState(ProjectState.HASTESTERRORS);
                } else {
                    this.setState(ProjectState.NOTFAILING);
                }
            } catch (MavenReportException e) {
                this.getLogger().warn("Error while parsing files to get test information: " + e);
                this.addStepError(e.getMessage());
            } catch (IOException e) {
                this.getLogger().warn("Error while getting the failing module path: " + e);
                this.addStepError(e.getMessage());
            }
        }

        this.writeProperty("error-types", StringUtils.join(this.failureNames, ","));
        this.writeProperty("failing-test-cases", StringUtils.join(this.failureLocations, ","));

        this.shouldStop = contract.shouldBeStopped(this);
    }
}
