Feature: Wheel Viewing

  Background:
    Given these wheels:
      | Name            | Mileage | Voltage Min | Voltage Max |
      | Veteran Sherman | 17622   | 75.6V       | 100.8V      |
      | Veteran Abrams  | 0       | 74.5V       | 100.8V      |
      | KingSong S20    | 0       | 90.0V       | 126.0V      |
      | KingSong 14S    | 694     | 48.0V       | 67.2V       |
      | Gotway Nikola+  | 2927    | 78.0V       | 100.8V      |
      | KingSong S18    | 2850    | 60.0V       | 84.0V       |
    And I start the app

  Scenario: Viewing a wheel's details in full
    When I select the KingSong S20
    Then the details view shows the correct name and a mileage of that wheel

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

  Scenario: => Editing the wheel
    Given I select the Veteran Sherman
    When I edit the wheel
    Then it shows that every field is editable

  Scenario: => Deleting the wheel
    Given I select the Veteran Abrams
    When I delete the wheel
    And I confirm the deletion
    Then I am back to the main screen and the wheel is gone
