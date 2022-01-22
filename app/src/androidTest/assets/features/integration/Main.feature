@Integration
Feature: Main Screen

  @WIP
  Scenario: On entering, we see a list of registered wheels and their distance
    # FIXME 0 Inject these entries
    When I start the app
    Then I see my wheels and their distance:
      | A |
      | B |
      | C |
