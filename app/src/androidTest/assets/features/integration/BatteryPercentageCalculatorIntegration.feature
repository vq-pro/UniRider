@Integration
Feature: Battery Percentage Calculator

  Background:
    Given I start the app

  Scenario Outline: Calculating percentage [<Wheel> / <Voltage>]
    Given I choose the <Wheel>
    And I go into the calculator
    When I enter a voltage of <Voltage>
    Then it displays a percentage of <Battery>
    Examples:
      | Wheel           | Voltage | Battery |
      | Gotway Nikola+  | 96.4    | 80.7%   |
      | Gotway Nikola+  | 89.1    | 48.7%   |
      | Inmotion V10F   | 74.6    | 41.3%   |
      | KingSong 14S    | 63.5    | 80.7%   |
      | KingSong S18    | 71.4    | 39.1%   |
      | Veteran Sherman | 96.5    | 82.9%   |

  Scenario: Going into calculator
    When I choose the Gotway Nikola+
    And I go into the calculator
    Then I can see the name of the wheel

  Scenario: Going into calculator without selecting a wheel
    When I don't choose a wheel
    Then I cannot go into the calculator

  Scenario: Selectable wheels
    Then the selected entry is <Select Model>
    And I can choose from these wheels:
      | <Select Model>  |
      | Gotway Nikola+  |
      | Inmotion V10F   |
      | KingSong 14S    |
      | KingSong S18    |
      | Veteran Sherman |
