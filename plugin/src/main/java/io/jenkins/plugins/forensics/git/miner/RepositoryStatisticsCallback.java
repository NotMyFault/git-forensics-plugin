package io.jenkins.plugins.forensics.git.miner;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import hudson.remoting.VirtualChannel;

import io.jenkins.plugins.forensics.git.util.AbstractRepositoryCallback;
import io.jenkins.plugins.forensics.git.util.RemoteResultWrapper;
import io.jenkins.plugins.forensics.miner.CommitDiffItem;

/**
 * Analyzes all commits starting from HEAD up to a specified commit ID. If no previous commit ID is given,
 * then the repository will be scanned until the initial commit is reached.
 *
 * @author Ullrich Hafner
 */
class RepositoryStatisticsCallback
        extends AbstractRepositoryCallback<RemoteResultWrapper<ArrayList<CommitDiffItem>>> {
    private static final long serialVersionUID = 7667073858514128136L;

    private final String previousCommitId;

    RepositoryStatisticsCallback(final String previousCommitId) {
        super();

        this.previousCommitId = previousCommitId;
    }

    @Override
    @SuppressWarnings("PMD.UseTryWithResources")
    public RemoteResultWrapper<ArrayList<CommitDiffItem>> invoke(
            final Repository repository, final VirtualChannel channel) {
        ArrayList<CommitDiffItem> commits = new ArrayList<>();
        RemoteResultWrapper<ArrayList<CommitDiffItem>> wrapper = new RemoteResultWrapper<>(
                commits, "Errors while mining the Git repository:");

        try {
            try (Git git = new Git(repository)) {
                CommitAnalyzer commitAnalyzer = new CommitAnalyzer();
                commits.addAll(commitAnalyzer.run(repository, git, previousCommitId, wrapper));
            }
            catch (IOException | GitAPIException exception) {
                wrapper.logException(exception,
                        "Can't analyze commits for the repository " + repository.getIdentifier());
            }
        }
        finally {
            repository.close();
        }

        return wrapper;
    }
}
