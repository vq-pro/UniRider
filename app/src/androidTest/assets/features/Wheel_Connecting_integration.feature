@Integration
Feature: Wheel Connecting

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given this wheel:
      | Name | Mileage | Wh  | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Distance Offset | Sold |
      | 14S  | 694     | 840 | 48V         | 55V             | 67.2V       | 4V/h        | 65.5V       | 1.5V           | 1.2             | No   |
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
    And the voltage is updated to 58.6V and the battery 29.5%
    And the km is updated to 10.2

#  FIXME-2 Scenario for adding a wheel and connecting it right away
