package fr.inria.spirals.jtravis.helpers;

import fr.inria.spirals.jtravis.entities.Build;
import fr.inria.spirals.jtravis.entities.Commit;
import fr.inria.spirals.jtravis.entities.PRInformation;
import fr.inria.spirals.jtravis.entities.Repository;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitPointer;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by urli on 04/01/2017.
 */
public class PRInformationHelper extends AbstractHelper {

    private static PRInformationHelper instance;

    private PRInformationHelper() {
        super();
    }

    protected static PRInformationHelper getInstance() {
        if (instance == null) {
            instance = new PRInformationHelper();
        }
        return instance;
    }

    public static PRInformation getPRInformationFromBuild(Build build) {
        try {
            if (build.isPullRequest()) {
                GitHub github = getInstance().getGithub();
                GHRateLimit rateLimit = github.getRateLimit();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                getInstance().getLogger().debug("GitHub ratelimit: Limit: " + rateLimit.limit + " Remaining: " + rateLimit.remaining + " Reset hour: " + dateFormat.format(rateLimit.reset));

                if (rateLimit.remaining > 2) {
                    GHRepository ghRepo = github.getRepository(build.getRepository().getSlug());
                    GHPullRequest pullRequest = ghRepo.getPullRequest(build.getPullRequestNumber());
                    PRInformation prInformation = new PRInformation();

                    GHRepository headRepo = pullRequest.getHead().getRepository();

                    if (headRepo == null) {
                        getInstance().getLogger().warn("The head repository is null: maybe it has been deleted from GitHub");
                        return null;
                    }

                    GHCommit base, head;
                    try {
                        GHCommit commitMerge = ghRepo.getCommit(build.getCommit().getSha());
                        base = commitMerge.getParents().get(0);
                        head = commitMerge.getParents().get(1);
                    } catch (FileNotFoundException e) {
                        getInstance().getLogger().warn("The merge commit was deleted from Github, get the commits from the PR base/head");
                        base = pullRequest.getBase().getCommit();
                        head = pullRequest.getHead().getCommit();
                    }


                    Repository repoPR = new Repository();
                    repoPR.setId(headRepo.getId());
                    repoPR.setDescription(headRepo.getDescription());
                    repoPR.setActive(true);
                    repoPR.setSlug(headRepo.getFullName());

                    prInformation.setOtherRepo(repoPR);

                    Commit commitHead = new Commit();
                    commitHead.setSha(head.getSHA1());
                    commitHead.setBranch(pullRequest.getHead().getRef());
                    commitHead.setCompareUrl(head.getHtmlUrl().toString());

                    GHCommit.ShortInfo infoCommit = head.getCommitShortInfo();

                    commitHead.setMessage(infoCommit.getMessage());
                    commitHead.setCommitterEmail(infoCommit.getAuthor().getEmail());
                    commitHead.setCommitterName(infoCommit.getAuthor().getName());
                    commitHead.setCommittedAt(infoCommit.getCommitDate());
                    prInformation.setHead(commitHead);

                    Commit commitBase = new Commit();
                    commitBase.setSha(base.getSHA1());
                    commitBase.setBranch(pullRequest.getBase().getRef());
                    commitBase.setCompareUrl(base.getHtmlUrl().toString());

                    infoCommit = base.getCommitShortInfo();

                    commitBase.setMessage(infoCommit.getMessage());
                    commitBase.setCommitterEmail(infoCommit.getAuthor().getEmail());
                    commitBase.setCommitterName(infoCommit.getAuthor().getName());
                    commitBase.setCommittedAt(infoCommit.getCommitDate());
                    prInformation.setBase(commitBase);

                    return prInformation;
                } else {
                    getInstance().getLogger().warn("You reach your rate limit for github, you have to wait " + rateLimit.reset + " to get datas. PRInformation will be null for build "+build.getId());
                }
            } else {
                getInstance().getLogger().info("Getting PRInformation return null for build id "+build.getId()+" as it does not come from a PR.");
            }
        } catch (IOException e) {
            getInstance().getLogger().warn("Error when getting PRInformation for build id "+build.getId()+" : "+e.getMessage());
        }
        return null;
    }
}
