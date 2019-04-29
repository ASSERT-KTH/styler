package fr.inria.spirals.repairnator.process.step;

import fr.inria.spirals.jtravis.entities.BuildStatus;
import fr.inria.spirals.repairnator.process.ProjectState;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;

/**
 * Created by fermadeiral.
 */
public class BuildShouldFail implements ContractForGatherTestInformation {

    @Override
    public boolean shouldBeStopped(GatherTestInformation gatherTestInformation) {
        ProjectInspector inspector = gatherTestInformation.getInspector();
        if (gatherTestInformation.getState() == ProjectState.HASTESTFAILURE) {
            inspector.setReproducedAsFail(true);
            if (inspector.isAboutAPreviousBuild()) {
                if (inspector.getPreviousBuild().getBuildStatus() == BuildStatus.FAILED) {
                    // So, 1) the current passing build can be reproduced and 2)
                    // its previous build is a failing build with failing tests
                    // and it can also be reproduced
                    gatherTestInformation.setState(ProjectState.FIXERBUILD_CASE1);
                } else {
                    // So, 1) the current passing build can be reproduced and 2)
                    // its previous build is a passing build that fails when
                    // tested with new tests and it can also be reproduced
                    gatherTestInformation.setState(ProjectState.FIXERBUILD_CASE2);
                }
            }
            return false;
        } else {
            if (gatherTestInformation.getState() == ProjectState.HASTESTERRORS) {
                if (inspector.isAboutAPreviousBuild()) {
                    return true;
                } else {
                    gatherTestInformation
                            .addStepError("Only get test errors, no failing tests. It will try to repair it.");
                    inspector.setReproducedAsError(true);
                    return false;
                }
            } else {
                return true;
            }
        }
    }

}
