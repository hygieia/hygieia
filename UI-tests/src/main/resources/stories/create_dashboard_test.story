Meta:
@dashboard
@smoke

Narrative:
	As a software project stakeholder
	I want to ensure that I am able to create a new dashboard for my project
	In order to view project metrics.



Scenario: User creates a new dashboard
Given I am an authorized project stakeholder
And I am on the Hygieia home screen
When I define a new team dashboard named 'DummyDashboard'
And I create the dashboard
Then the current dashboard header should read 'DummyDashboard'