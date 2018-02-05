package com.mithuns.dependencydiscoverer.scm.git;

import java.util.Set;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import com.mithuns.dependencydiscoverer.exception.GitConnectionException;
import com.mithuns.dependencydiscoverer.exception.GitServiceException;
import com.mithuns.dependencydiscoverer.exception.RepositoryNotFoundException;
import com.mithuns.dependencydiscoverer.scm.SourceCodeRetriever;

public interface GitInterface extends SourceCodeRetriever{


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
    public GHRepository connectToGit(String password, String apiUrl, String userName, String repoName) throws GitConnectionException, RepositoryNotFoundException;

    /**
     * Function that returns the list of the login names of the users who are the Collaborators for a particular repository.
     * @param gitRepo The GHRepository object on retrieving a valid repository after connecting to GitHub using the password, username, apiUrl.
     * @return Set<String> Set of the Login Ids of the Collaborators for that particular repository.
     * @throws GitServiceException If the information from GitHub cannot be retrieved if GitHub is down.
     * @throws IllegalArgumentException If the parameter conditions are not met.
     */
    public Set<String> getCollaboratorLoginIds(GHRepository gitRepo) throws GitServiceException;

    /**
     * Function that returns the pull requests for a particular repository.
     * @param gitRepo The GHRepository object on retrieving a valid repository after connecting to GitHub using the password, user name, apiUrl.
     * @return Set<GHPullRequest> Set of the GHPullRequest Objects of the Collaborators for that particular repository.
     * @throws GitServiceException GitServiceException If the information from GitHub cannot be retrieved if GitHub is down.
     * @throws IllegalArgumentException If the parameter conditions are not met.
     */
    public Set<GHPullRequest> getPullRequests(GHRepository gitRepo) throws GitServiceException;

    /**
     * Function that returns the List of the file paths pertaining to the entered project name.
     * @param projectName String of the project name whose file paths are needed (cannot be null).
     * @param gitRepo The GHRepository object on retrieving a valid repository after connecting to GitHubusing the password, user name, apiUrl.
     * @return Set<String> Set of the file paths of all the files for a particular project.
     * @throws GitServiceException If the information from GitHub cannot be retrieved.
     * @throws IllegalArgumentException If the parameter conditions are not met.
     */
    public Set<String> getFilePaths(String projectName, GHRepository gitRepo) throws GitServiceException;


}
