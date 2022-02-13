Feature: Wheel Viewing

  Background:
    Given these wheels:
      | Name            | Voltage Min | Voltage Max | Mileage |
      | Veteran Sherman | 75.6V       | 100.8V      | 17622   |
      | Veteran Abrams  | 74.5V       | 100.8V      | 0       |
      | KingSong S20    | 90.0V       | 126.0V      | 0       |
      | KingSong 14S    | 48.0V       | 67.2V       | 694     |
      | Gotway Nikola+  | 78.0V       | 100.8V      | 2927    |
      | KingSong S18    | 60.0V       | 84.0V       | 2850    |
    And I start the app

  Scenario: Viewing a wheel's details in full
    When I select the KingSong S20
    Then the details view shows the correct name and a mileage of that wheel

  @Integration
  Scenario: Connecting to a wheel to update its mileage
    Given I select the KingSong 14S
    When I connect to the KS-14Sxx9999
    Then the mileage is updated to 695
    And the wheel's Bluetooth name is updated to KS-14Sxx9999

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
