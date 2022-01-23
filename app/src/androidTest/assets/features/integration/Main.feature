@Integration
Feature: Main Screen

  @WIP
  Scenario: On entering, we see a list of registered wheels and their distance
    Given these wheels:
      | A |
      | B |
      | C |
    When I start the app
    Then I see my wheels and their distance:
      | A |
      | B |
      | C |
