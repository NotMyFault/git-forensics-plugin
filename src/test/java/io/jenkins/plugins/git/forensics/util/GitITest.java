package io.jenkins.plugins.git.forensics.util;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.junit.Before;
import org.junit.Rule;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import jenkins.plugins.git.GitSampleRepoRule;

/**
 * Base class for Git integration tests. Provides a Git repository that will b e initialized for each test.
 *
 * @author Ullrich Hafner
 */
public class GitITest {
    /** File name of a source file that will be modified by two authors. */
    protected static final String FILE_NAME = "source.txt";
    /** Author 1 name. */
    protected static final String FOO_NAME = "Foo";
    /** Author 1 email. */
    protected static final String FOO_EMAIL = "foo@jenkins.io";
    /** Author 2 name. */
    protected static final String BAR_NAME = "Bar";
    /** Author 2 email. */
    protected static final String BAR_EMAIL = "bar@jenkins.io";

    /** Git repository in a temporary folder. */
    @Rule
    public GitSampleRepoRule sampleRepo = new GitSampleRepoRule();

    /**
     * Initializes the Git repository.
     *
     * @throws Exception
     *         if the initialization fails
     */
    @Before
    @SuppressWarnings({"IllegalCatch", "PMD.SignatureDeclareThrowsException"})
    public void init() throws Exception {
        sampleRepo.init();
    }

    /**
     * Returns the object ID of the current HEAD commit.
     *
     * @return the head commit
     */
    @SuppressWarnings("IllegalCatch")
    protected ObjectId getHeadCommit() {
        try {
            return ObjectId.fromString(getHead());
        }
        catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    /**
     * Returns the commit ID of the current HEAD commit.
     *
     * @return the head commit (as String)
     */
    @SuppressWarnings("IllegalCatch")
    protected String getHead() {
        try {
            return sampleRepo.head();
        }
        catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    /**
     * Returns the sample repository.
     *
     * @return the sample repository
     */
    @SuppressFBWarnings("BC")
    protected Repository createRepository() {
        try {
            RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
            return repositoryBuilder.setWorkTree(sampleRepo.getRoot()).setMustExist(true).build();
        }
        catch (IOException exception) {
            throw new AssertionError(exception);
        }
    }

    /**
     * Writes a file to repository.
     *
     * @param relativePath
     *         relative path ({@code /}-separated)
     * @param content
     *         the text content of the file
     */
    protected void writeFile(final String relativePath, final String content) {
        try {
            sampleRepo.write(relativePath, content);
        }
        catch (IOException exception) {
            throw new AssertionError(exception);
        }
    }

    /**
     * Writes a file to repository.
     *
     * @param commands
     *         git commands and parameters
     */
    @SuppressWarnings("IllegalCatch")
    protected void git(final String... commands) {
        try {
            sampleRepo.git(commands);
        }
        catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    /**
     * Change the content of the repository file using the name and email of author 1.
     *
     * @param content
     *         the new content of the file
     */
    protected void writeFileAsAuthorFoo(final String content) {
        writeFile(FILE_NAME, content);
        git("add", FILE_NAME);
        git("config", "user.name", FOO_NAME);
        git("config", "user.email", FOO_EMAIL);
        git("commit", "--message=Foo");
    }

    /**
     * Change the content of the repository file using the name and email of author 2.
     *
     * @param content
     *         the new content of the file
     */
    protected void writeFileAsAuthorBar(final String content) {
        writeFile(FILE_NAME, content);
        git("add", FILE_NAME);
        git("config", "user.name", BAR_NAME);
        git("config", "user.email", BAR_EMAIL);
        git("commit", "--message=Bar");
    }
}
