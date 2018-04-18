Feature: peerReview

  Background:
    * header apiUser = 'apiadmin'
    * header Authorization = call read('classpath:basic-auth.js')
    * url baseUrl
    * configure readTimeout = 60000

  Scenario: Verify pull request information for a repo
    Given path 'peerReview'
    And params read('peerReview-params.json')
    When method get
    Then status 200
    And match response..auditStatuses == [["REPO_NOT_CONFIGURED"]]