Meta:
@dashboard
@smoke

Narrative:
	As a software project stakeholder
	I want to ensure that I am able delete a dashboard for my project
	In order to clean up unwanted dashboards.


Scenario: User deletes a dashboard
Given I am an authorized project stakeholder
And I am on the Hygieia home screen
When I delete a team dashboard
Then the dashboard 'DummyDashboard' should be deleted