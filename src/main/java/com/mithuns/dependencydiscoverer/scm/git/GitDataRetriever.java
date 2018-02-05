package com.mithuns.dependencydiscoverer.scm.git;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import com.google.common.base.Preconditions;
import com.mithuns.dependencydiscoverer.exception.GitConnectionException;
import com.mithuns.dependencydiscoverer.exception.GitServiceException;
import com.mithuns.dependencydiscoverer.exception.RepositoryNotFoundException;


/**
 * Handles the Git API operations to ease the interactions with GitHub to fetch certain pieces of data about the repositories.
 * @Author Mithun Singh
 */
final public class GitDataRetriever implements GitInterface
{

    private Set<String> filePaths;

    /**
     * Helps connect to the GitHub repository using the password, user name and the apiUrl. The repository of a particular user is then fetched.
     * @param password String of the user's password (cannot be null).
     * @param apiUrl apiUrl String of the API url (cannot be null).
     * @param userName String of the user login id of the user whose repository should be accessed (cannot be null).
     * @param repoName String of the name of the repository the user wants to retrieve data from (cannot be null).
     * @return GHRepository The GHRepository object on retrieving a valid repository after connecting to GitHub using the password, user name, apiUrl and returns null if the repository could not
     *         be found.
     * @throws GitConnectionException If the connection to GitHub failed due to invalid password, user name, api Url, user name or if GitHub is down.
     * @throws RepositoryNotFoundException If the repository cannot be retrieved for a given repository name.
     * @throws IllegalArgumentException If the parameter conditions are not met.
     */

    public GHRepository connectToGit(final String password, final String apiUrl, final String userName, final String repoName) throws GitConnectionException, RepositoryNotFoundException
    {

        Preconditions.checkArgument(password != null, "Invalid password.");
        Preconditions.checkArgument(apiUrl != null, "Invalid apiurl.");
        Preconditions.checkArgument(userName != null, "Invalid user name.");
        Preconditions.checkArgument(repoName != null, "Invalid repository name.");

        GHRepository ghRepository = null;
        GitHub github;
        GHUser ghUser;

        try
        {

            github = GitHub.connectToEnterprise(apiUrl, userName, password);
            ghUser = github.getUser(userName);
            ghRepository = ghUser.getRepository(repoName.trim());

        }
        catch (final IOException e)
        {
            throw new GitConnectionException("Connection to Git Failed: " + e.getMessage());
        }

        if (ghRepository == null)
        {
            throw new RepositoryNotFoundException("Repository not found: " + " repositoryname:" + repoName);
        }

        return ghRepository;
    }

    /**
     * Function that returns the list of the login names of the users who are the Collaborators for a particular repository.
     * @param gitRepo The GHRepository object on retrieving a valid repository after connecting to GitHub using the password, user name, api Url.
     * @return Set<String> Set of the Login Ids of the Collaborators for that particular repository.
     * @throws GitServiceException If the information from GitHub cannot be retrieved if GitHub is down.
     * @throws IllegalArgumentException If the parameter conditions are not met.
     */

    public Set<String> getCollaboratorLoginIds(final GHRepository gitRepo) throws GitServiceException
    {

        Preconditions.checkArgument(gitRepo != null, "Repository not found: Invalid repository name.");

        GHPersonSet<GHUser> users;

        try
        {
            users = gitRepo.getCollaborators();
        }
        catch (final IOException e)
        {
            throw new GitServiceException("Cannot retrieve the list of collaborators from repository " + gitRepo.getName());
        }

        final Set<String> collaborators = new HashSet<String>(users.size());
        for (final GHUser user : users)
        {
            collaborators.add(user.getLogin());
        }

        return collaborators;
    }

    /**
     * Function that returns the number of watchers for a particular repository.
     * @param gitRepo The GHRepository object on retrieving a valid repository after connecting to GitHubusing the password, user name, apiUrl.
     * @return int the number of watchers for a particular repository.
     * @throws IllegalArgumentException If the parameter conditions are not met.
     */

    public int getWatchersCount(final GHRepository gitRepo)
    {

        Preconditions.checkArgument(gitRepo != null, "Repository not found: Invalid repository name.");

        return gitRepo.getWatchers();

    }

    /**
     * Function that returns the pull requests for a particular repository.
     * @param gitRepo The GHRepository object on retrieving a valid repository after connecting to GitHub using the password, user name, apiUrl.
     * @return Set<GHPullRequest> Set of the GHPullRequest Objects of the Collaborators for that particular repository.
     * @throws GitServiceException GitServiceException If the information from GitHub cannot be retrieved if GitHub is down.
     * @throws IllegalArgumentException If the parameter conditions are not met.
     */

    public Set<GHPullRequest> getPullRequests(final GHRepository gitRepo) throws GitServiceException
    {

        Preconditions.checkArgument(gitRepo != null, "Repository not found: Invalid repository name.");
        final Set<GHPullRequest> ghPullRequests = new HashSet<GHPullRequest>();

        try
        {
            ghPullRequests.addAll(gitRepo.getPullRequests(GHIssueState.OPEN));
            ghPullRequests.addAll(gitRepo.getPullRequests(GHIssueState.CLOSED));
        }
        catch (final IOException e)
        {
            throw new GitServiceException("Cannot retrieve from Git the pull requests from " + gitRepo.getName());
        }

        return ghPullRequests;
    }

    /**
     * Function that returns the List of the file paths pertaining to the entered project name.
     * @param projectName String of the project name whose file paths are needed (cannot be null).
     * @param gitRepo The GHRepository object on retrieving a valid repository after connecting to GitHubusing the password, user name, apiUrl.
     * @return Set<String> Set of the file paths of all the files for a particular project.
     * @throws GitServiceException If the information from GitHub cannot be retrieved.
     * @throws IllegalArgumentException If the parameter conditions are not met.
     */

    public Set<String> getFilePaths(final String projectName, final GHRepository gitRepo) throws GitServiceException
    {

        Preconditions.checkArgument(projectName != null, "Directory not found: Invalid project name.");
        try
        {
            final List<GHContent> contents = gitRepo.getDirectoryContent(projectName);
            filePaths = new HashSet<String>();

            for (final GHContent cont : contents)
            {

                if (cont.isDirectory())
                {
                    getFilePathsWithinSubDirectory(cont.listDirectoryContent().asList());
                }
                else
                {
                    filePaths.add(cont.getPath());
                }
            }
        }
        catch (final IOException e)
        {
            throw new GitServiceException("Cannot retrieve from Git the file paths for project: " + projectName);
        }

        return filePaths;
    }

    /**
     * Helper method that recurses into the bottom level of the directories where the files are present.
     * @param ghContents List of the GHContent Objects.
     * @throws GitServiceException GitServiceException If the information from GitHub cannot be retrieved if GitHub is down.
     */

    private void getFilePathsWithinSubDirectory(final List<GHContent> ghContents) throws GitServiceException
    {

        for (final GHContent cont : ghContents)
        {

            if (cont.isDirectory())
            {
                try
                {
                    getFilePathsWithinSubDirectory(cont.listDirectoryContent().asList());
                }
                catch (final IOException e)
                {
                    throw new GitServiceException("Cannot retrieve the directory content");
                }
            }
            else
            {
                filePaths.add(cont.getPath());
            }
        }
    }

	@Override
	public void getContributors() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getWatchers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getCollaborators() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getAdmins() {
		// TODO Auto-generated method stub
		
	}

}
