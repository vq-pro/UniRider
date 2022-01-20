@Integration
Feature: Main Screen

  Scenario: On entering, we see a list of registered wheels and their distance
    When I start the app
    Then I see my wheels and their distance
