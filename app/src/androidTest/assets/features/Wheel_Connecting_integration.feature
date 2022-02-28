@IntegrationOnly
Feature: Wheel Connecting

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given this simulated device:
      | Bt Name    | Bt Address        | Mileage | Voltage |
      | KS-14S-SIM | AB:CD:EF:GH:IJ:KL | 705.615 | 58.56V  |

  Scenario: Connecting to a wheel to update its mileage for the first time
    Given this wheel:
      | Name         | Mileage | Voltage Min | Voltage Max |
      | KingSong 14S | 694     | 48.0V       | 67.2V       |
    And I start the app
    And I select the KingSong 14S
    When I connect to the KS-14S-SIM
    Then the mileage is updated to 706
    And the wheel's Bluetooth name is updated

  Scenario: Connecting to a previously connected wheel to update its mileage
    Given these connected wheels:
      | Name         | Bt Name    | Bt Address        | Mileage | Voltage Min | Voltage Max |
      | KingSong 14S | KS-14S-SIM | AB:CD:EF:GH:IJ:KL | 694     | 48.0V       | 67.2V       |
    And I start the app
    And I select the KingSong 14S
    When I reconnect to the wheel
    Then the mileage is updated to 706
    And the voltage is set to 58.6V and battery to 55.2%

#  FIXME-2 Scenario for adding a wheel and connecting it right away
