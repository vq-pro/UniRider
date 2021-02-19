@Integration
Feature: Battery Percentage Calculator

  Scenario: Startup
    When I start the app
    Then I can choose from these wheels:
      | <Select Model> |
      | Gotway Nikola  |
      | KingSong 14D   |

  @WIP
  Scenario Outline: Calculating percentage [<Wheel> / <Voltage>]
    Given I start the app
    And I choose the "<Wheel>"
    When I enter a voltage of <Voltage>
    Then it displays a percentage of <Battery>
    Examples:
      | Wheel         | Voltage | Battery |
      | Gotway Nikola | 96.4    | 79.6%   |
      | Gotway Nikola | 89.1    | 45.8%   |
