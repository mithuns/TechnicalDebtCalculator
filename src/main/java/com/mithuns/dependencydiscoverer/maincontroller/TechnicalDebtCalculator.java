package com.mithuns.dependencydiscoverer.maincontroller;

public interface TechnicalDebtCalculator {
	
	void findCoreProjectDependencies();
	
	void findCorePeopleDependencies();
	
	void findAllPeopleInterested();

}