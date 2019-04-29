package fr.inria.spirals.repairnator.process.step;

import fr.inria.spirals.jtravis.entities.Build;
import fr.inria.spirals.jtravis.entities.Commit;
import fr.inria.spirals.jtravis.entities.PRInformation;
import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import fr.inria.spirals.repairnator.process.ProjectState;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.PatchApplyException;
import org.eclipse.jgit.api.errors.PatchFormatException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.URIish;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCompare;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by urli on 03/01/2017.
 */
public class CloneRepository extends AbstractStep {
    public static final String GITHUB_ROOT_REPO = "https://github.com/";
    private static final String GITHUB_PATCH_ACCEPT = "application/vnd.github.v3.patch";

    protected Build build;

    public CloneRepository(ProjectInspector inspector) {
        super(inspector);
        this.build = inspector.getBuild();
    }

    private void showGitHubRateInformation(GitHub gh) throws IOException {
        GHRateLimit rateLimit = gh.getRateLimit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        this.getLogger().info("GitHub ratelimit: Limit: " + rateLimit.limit + " Remaining: " + rateLimit.remaining
                + " Reset hour: " + dateFormat.format(rateLimit.reset));
    }

    private String getLastKnowParent(GitHub gh, GHRepository ghRepo, Git git, String oldCommitSha) throws IOException {
        this.showGitHubRateInformation(gh);
        GHCommit commit = ghRepo.getCommit(oldCommitSha); // get the deleted
                                                          // commit from GH
        List<String> commitParents = commit.getParentSHA1s();

        if (commitParents.isEmpty()) {
            this.addStepError("The following commit does not have any parent in GitHub: " + oldCommitSha
                    + ". It cannot be resolved.");
            return null;
        }

        if (commitParents.size() > 1) {
            this.getLogger().debug("The commit has more than one parent : " + commit.getHtmlUrl());
        }

        String parent = commitParents.get(0);

        try {
            ObjectId commitObject = git.getRepository().resolve(parent);
            git.getRepository().open(commitObject);

            return parent;
        } catch (MissingObjectException e) {
            return getLastKnowParent(gh, ghRepo, git, parent);
        }
    }

    /**
     * When a commit has been force deleted it still can be retrieved from
     * GitHub API. This function intend to retrieve a patch from the Github API
     * and to apply it back on the repo
     *
     * @param git
     * @param oldCommitSha
     * @return the SHA of the commit created after applying the patch or null if
     *         an error occured.
     */
    private String retrieveAndApplyCommitFromGithub(Git git, String oldCommitSha) {
        try {
            GitHub gh = GitHubBuilder.fromEnvironment().build();
            GHRepository ghRepo = gh.getRepository(this.build.getRepository().getSlug());

            String lastKnowParent = this.getLastKnowParent(gh, ghRepo, git, oldCommitSha);

            // checkout the repo to the last known parent of the deleted commit
            git.checkout().setName(lastKnowParent).call();

            // get from github a patch between that commit and the targeted
            // commit
            // note that this patch could contain changes of multiple commits
            GHCompare compare = ghRepo.getCompare(lastKnowParent, oldCommitSha);

            this.showGitHubRateInformation(gh);

            URL patchUrl = compare.getPatchUrl();

            this.getLogger().debug("Retrieve commit patch from the following URL: " + patchUrl);

            // retrieve it through a simple HTTP request
            // some errors occurs when applying patch from snippets contained in
            // GHCompare object
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(patchUrl).build();
            Call call = client.newCall(request);
            Response response = call.execute();

            File tempFile = File.createTempFile(this.build.getRepository().getSlug(), "patch");

            // apply the patch and commit changes using message and authors of
            // the referenced commit.
            if (response.code() == 200) {

                FileWriter writer = new FileWriter(tempFile);
                writer.write(response.body().string());
                writer.flush();
                writer.close();

                this.getLogger().info("Exec following command: git apply " + tempFile.getAbsolutePath());
                ProcessBuilder processBuilder = new ProcessBuilder("git", "apply", tempFile.getAbsolutePath())
                        .directory(new File(this.inspector.getRepoLocalPath())).inheritIO();

                Process p = processBuilder.start();
                try {
                    p.waitFor();

                    // Applying patch does not work as the move of file is
                    // broken in JGit
                    // It assumes the target directory exists.
                    // ApplyResult result =
                    // git.apply().setPatch(response.body().byteStream()).call();

                    Commit buildCommit = this.build.getCommit();

                    // add all file for the next commit
                    git.add().addFilepattern(".").call();

                    // do the commit
                    RevCommit ref = git.commit().setAll(true)
                            .setAuthor(buildCommit.getAuthorName(), buildCommit.getAuthorEmail())
                            .setCommitter(buildCommit.getCommitterName(), buildCommit.getCommitterEmail())
                            .setMessage(buildCommit.getMessage()
                                    + "\n(This is a retrieve from the following deleted commit: " + oldCommitSha + ")")
                            .call();

                    tempFile.delete();

                    return ref.getName();
                } catch (InterruptedException e) {
                    this.addStepError("Error while executing git command to apply patch: " + e);
                }

            }
        } catch (IOException e) {
            this.addStepError("Error while getting commit from Github: " + e);
        } catch (PatchFormatException e) {
            this.addStepError("Error while getting patch from Github: " + e);
        } catch (PatchApplyException e) {
            this.addStepError("Error while applying patch from Github: " + e);
        } catch (GitAPIException e) {
            this.addStepError("Error with Git API: " + e);
        }
        return null;
    }

    /**
     * Test if a commit exists in the given git repository
     *
     * @param git
     * @param oldCommitSha
     * @return oldCommitSha if the commit exists in the repo, a new commit SHA
     *         if the commit has been retrieved from GitHub and applied back, or
     *         null if the retrieve failed.
     */
    protected String testCommitExistence(Git git, String oldCommitSha) {
        try {
            ObjectId commitObject = git.getRepository().resolve(oldCommitSha);
            git.getRepository().open(commitObject);
            return oldCommitSha;
        } catch (MissingObjectException e) {
            return retrieveAndApplyCommitFromGithub(git, oldCommitSha);
        } catch (IncorrectObjectTypeException e) {
            this.addStepError("Error while testing commit: " + e);
        } catch (AmbiguousObjectException e) {
            this.addStepError("Error while testing commit: " + e);
        } catch (IOException e) {
            this.addStepError("Error while testing commit: " + e);
        }
        return null;
    }

    protected void businessExecute() {
        String repository = this.inspector.getRepoSlug();
        String repoRemotePath = GITHUB_ROOT_REPO + repository + ".git";
        String repoLocalPath = this.inspector.getRepoLocalPath();

        // start cloning
        try {
            this.getLogger()
                    .debug("Cloning repository " + repository + " has in the following directory: " + repoLocalPath);
            Git git = Git.cloneRepository().setURI(repoRemotePath).setDirectory(new File(repoLocalPath)).call();

            this.writeProperty("workspace", this.inspector.getWorkspace());
            this.writeProperty("buildid", this.build.getId() + "");
            this.writeProperty("repo", this.build.getRepository().getSlug());

            if (this.build.isPullRequest()) {
                this.writeProperty("is-pr", "true");

                PRInformation prInformation = this.build.getPRInformation();

                this.writeProperty("pr-remote-repo", this.build.getPRInformation().getOtherRepo().getSlug());
                this.writeProperty("pr-head-commit-id", this.build.getPRInformation().getHead().getSha());
                this.writeProperty("pr-base-commit-id", this.build.getPRInformation().getBase().getSha());
                this.writeProperty("pr-id", this.build.getPullRequestNumber() + "");

                this.getLogger()
                        .debug("Reproduce the PR for " + repository + " by fetching remote branch and merging.");
                String remoteBranchPath = GITHUB_ROOT_REPO + prInformation.getOtherRepo().getSlug() + ".git";

                RemoteAddCommand remoteBranchCommand = git.remoteAdd();
                remoteBranchCommand.setName("PR");
                remoteBranchCommand.setUri(new URIish(remoteBranchPath));
                remoteBranchCommand.call();

                git.fetch().setRemote("PR").call();

                String commitHeadSha = testCommitExistence(git, prInformation.getHead().getSha());
                String commitBaseSha = testCommitExistence(git, prInformation.getBase().getSha());

                if (commitHeadSha == null) {
                    this.addStepError("Commit head ref cannot be retrieved in the repository: "
                            + prInformation.getHead().getSha() + ". Operation aborted.");
                    this.getLogger().debug(prInformation.getHead().toString());
                    this.shouldStop = true;
                    return;
                }

                if (commitBaseSha == null) {
                    this.addStepError("Commit base ref cannot be retrieved in the repository: "
                            + prInformation.getBase().getSha() + ". Operation aborted.");
                    this.getLogger().debug(prInformation.getBase().toString());
                    this.shouldStop = true;
                    return;
                }

                this.getLogger().debug("Get the commit " + commitHeadSha + " for repo " + repository);
                git.checkout().setName(commitHeadSha).call();

                RevWalk revwalk = new RevWalk(git.getRepository());
                RevCommit revCommitBase = revwalk.lookupCommit(git.getRepository().resolve(commitBaseSha));

                this.getLogger().debug("Do the merge with the PR commit for repo " + repository);
                git.merge().include(revCommitBase).setFastForward(MergeCommand.FastForwardMode.NO_FF).call();
            } else {
                String commitCheckout = this.build.getCommit().getSha();

                commitCheckout = this.testCommitExistence(git, commitCheckout);

                if (commitCheckout != null) {
                    this.getLogger().debug("Get the commit " + commitCheckout + " for repo " + repository);
                    git.checkout().setName(commitCheckout).call();
                } else {
                    this.addStepError("Error while getting the commit to checkout from the repo.");
                    this.shouldStop = true;
                    return;
                }

            }

        } catch (Exception e) {
            this.getLogger().warn("Repository " + repository + " cannot be cloned.");
            this.getLogger().debug(e.toString());
            this.addStepError(e.getMessage());
            this.shouldStop = true;
            return;
        }

        this.state = ProjectState.CLONABLE;
    }

    protected void cleanMavenArtifacts() {
    }
}
