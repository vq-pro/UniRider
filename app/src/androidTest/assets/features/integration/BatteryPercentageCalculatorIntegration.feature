@Integration
Feature: Battery Percentage Calculator

  Background:
    Given I start the app
    And I go into the calculator

  Scenario Outline: Calculating percentage [<Wheel> / <Voltage>]
    Given I choose the "<Wheel>"
    When I enter a voltage of <Voltage>
    Then it displays a percentage of <Battery>
    Examples:
      | Wheel           | Voltage | Battery |
      | Gotway Nikola   | 96.4    | 79.6%   |
      | Gotway Nikola   | 89.1    | 45.8%   |
      | Inmotion V10F   | 74.6    | 41.3%   |
      | KingSong 14D    | 63.5    | 80.7%   |
      | Veteran Sherman | 96.5    | 82.9%   |

  Scenario: Switching wheels
    Given I choose the "Gotway Nikola"
    And I enter a voltage of 96.4
    When I choose the "KingSong 14D"
    Then it blanks the displays


