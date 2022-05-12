@Integration
Feature: Wheel Connecting

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given this wheel:
      | Name | Mileage | Wh  | Voltage Min | Voltage Reserve | Voltage Max |
      | 14S  | 694     | 840 | 48V         | 55V             | 67.2V       |
    And this simulated device:
      | Bt Name    | Bt Address        | Mileage | Voltage |
      | KS-14S-SIM | C0:C1:C2:C3:C4:C5 | 705.615 | 58.56V  |
    And I start the app

  Scenario: Connecting to a wheel to update its mileage for the first time
    Given I select the 14S
    When I connect to the KS-14S-SIM
    Then the mileage is updated to 706
    And the wheel's Bluetooth name is updated

  Scenario: Connecting to a previously connected wheel to update its mileage
    Given the 14S has a previous mileage of 1000
    Given this wheel is connected:
      | Name | Bt Name    | Bt Address        |
      | 14S  | KS-14S-SIM | C0:C1:C2:C3:C4:C5 |
    And I select the 14S
    When I reconnect to the wheel
    Then the mileage is updated to 1706
    And the voltage shows 58.6V and the battery 55.2%

#  FIXME-2 Scenario for adding a wheel and connecting it right away
