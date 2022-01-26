@Integration
Feature: Battery Percentage Calculator

  Background:
    Given these wheels:
      | Inmotion V10F   |
      | Veteran Sherman |
      | KingSong 14S    |
      | Gotway Nikola+  |
      | KingSong S18    |
    And I start the app

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
