@Integration
Feature: Wheel Details

  Background:
    Given these wheels:
      | Name            | Voltage Max | Voltage Min | Mileage |
      | Veteran Sherman | 100.8V      | 75.6V       | 17622    |
      | Veteran Abrams  | 74.5V       | 100.8V      | 0        |
      | KingSong S20    | 126.0V      | 90.0V       | 0        |
      | KingSong 14S    | 67.2V       | 48.0V       | 694      |
      | Gotway Nikola+  | 100.8V      | 78.0V       | 2927     |
      | KingSong S18    | 84.0V       | 60.0V       | 2850     |

  Scenario Outline: Calculating percentage [<Wheel> / <Voltage>]
    Given I start the app
    And I select the <Wheel>
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

  Scenario: Editing wheel mileage
    Given I start the app
    And I select the Veteran Sherman
    When I change the mileage to 18000
    Then it shows the new mileage on the details view
    And it shows the updated mileage on the main view