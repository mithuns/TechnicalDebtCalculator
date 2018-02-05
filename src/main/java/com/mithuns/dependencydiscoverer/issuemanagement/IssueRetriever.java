package com.mithuns.dependencydiscoverer.issuemanagement;

public interface IssueRetriever {
	void getIssues();
	void getIssuesForRepo();
	void getIssuesForUser();
	void getIssuesForUsers();
}
