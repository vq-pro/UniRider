@Integration
Feature: Battery Percentage Calculator

  Background:
    Given these wheels:
      | Name            | Voltage Max | Voltage Min | Distance |
      | Veteran Sherman | 100.8V      | 75.6V       | 123      |
      | KingSong S20    | 126.0V      | 90.0V       | 123      |
      | KingSong 14S    | 67.2V       | 48.0V       | 123      |
      | Gotway Nikola+  | 100.8V      | 78.0V       | 123      |
      | KingSong S18    | 84.0V       | 60.0V       | 123      |
    And I start the app

  Scenario Outline: Calculating percentage [<Wheel> / <Voltage>]
    Given I select the <Wheel>
    When I enter a voltage of <Voltage>
    Then it displays a percentage of <Battery>
    Examples:
      | Wheel           | Voltage | Battery |
      | Gotway Nikola+  | 96.4V   | 80.7%   |
      | Gotway Nikola+  | 89.1V   | 48.7%   |
      | KingSong 14S    | 63.5V   | 80.7%   |
      | KingSong S18    | 71.4V   | 47.5%   |
      | Veteran Sherman | 96.5V   | 82.9%   |
      | KingSong S20    | 108.0V  | 50.0%   |
