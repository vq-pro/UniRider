@IntegrationOnly
Feature: Wheel Connecting

  Background:
    Given this simulated device:
      | Bt Name      | Bt Address        | Mileage | Voltage |
      | KS-14Sxx9999 | AB:CD:EF:GH:IJ:KL | 705.615 | 58.56V  |

  Scenario: Connecting to a wheel to update its mileage for the first time
    Given this disconnected wheel:
      | Name         | Mileage | Voltage Min | Voltage Max |
      | KingSong 14S | 694     | 48.0V       | 67.2V       |
    And I start the app
    And I select the KingSong 14S
    When I connect to the KS-14Sxx9999
    Then the mileage is updated to 706
    And the wheel's Bluetooth name is updated

  Scenario: Connecting to a previously connected wheel to update its mileage
    Given this connected wheel:
      | Name         | Bt Name      | Bt Address        | Mileage | Voltage Min | Voltage Max |
      | KingSong 14S | KS-14Sxx9999 | AB:CD:EF:GH:IJ:KL | 694     | 48.0V       | 67.2V       |
    And I start the app
    And I select the KingSong 14S
    When I reconnect to the wheel
    Then the mileage is updated to 706
    And the voltage is set to 58.6V and battery to 55.2%
