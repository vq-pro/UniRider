@Integration
Feature: Battery Percentage Calculator

  Scenario: Startup
    When I start the app
    Then I can choose from these wheels:
      | <Select Model> |
      | Gotway Nikola  |
      | KingSong 14D   |

  @WIP
  Scenario: Calculating percentage
    Given I start the app
    And I choose the "Gotway Nikola"
    When I enter a voltage of 96.4
    Then it displays a percentage of 79.6
