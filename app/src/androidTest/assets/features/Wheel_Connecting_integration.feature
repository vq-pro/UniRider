@Integration
Feature: Wheel Connecting

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given this wheel:
      | Name | Mileage | Wh  | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Sold |
      | 14S  | 694     | 840 | 48V         | 55V             | 67.2V       | 4V/h        | 65.5V       | 1.5V           | No   |
    And this simulated device:
      | Bt Name    | Bt Address        | Km     | Mileage | Voltage |
      | KS-14S-SIM | C0:C1:C2:C3:C4:C5 | 12.218 | 705.615 | 58.56V  |
    And I start the app

  Scenario: Connecting to a wheel to update its mileage for the first time
    Given I select the 14S
    When I connect to the KS-14S-SIM
    Then the mileage is updated to 706 km
    And the wheel's Bluetooth name is updated

  Scenario: Connecting to a previously connected wheel to update its values
    Given the 14S has a previous mileage of 1000 km
    And this wheel is connected:
      | Name | Bt Name    | Bt Address        |
      | 14S  | KS-14S-SIM | C0:C1:C2:C3:C4:C5 |
    And I select the 14S
    When I reconnect to the wheel
    Then the mileage is updated to 1706 km
    And the voltage is updated to 58.6V and the battery 55.2%
    And the km is updated to 12.2

  Scenario Outline: When reconnecting, set the starting voltage depending on km [<km>]
    Given this simulated device:
      | Bt Name    | Bt Address        | Km   | Mileage | Voltage   |
      | KS-14S-SIM | C0:C1:C2:C3:C4:C5 | <km> | 705.615 | <voltage> |
    And this wheel is connected:
      | Name | Bt Name    | Bt Address        |
      | 14S  | KS-14S-SIM | C0:C1:C2:C3:C4:C5 |
    And I select the 14S
    When I reconnect to the wheel
    Then the starting voltage is <starting voltage>
    Examples:
      | km  | voltage | starting voltage |
      | 0.0 | 66.1V   | 66.1V            |
      | 0.1 | 66.5V   | 67.2V            |

#  FIXME-2 Scenario for adding a wheel and connecting it right away
