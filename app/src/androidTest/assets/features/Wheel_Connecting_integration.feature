@Integration
@WIP
Feature: Wheel Connecting

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given this wheel:
      | Name      | Mileage | Wh   | Voltage Min | Voltage Max | Charge Rate | Full Charge | Charger Offset | Distance Offset | Sold |
      | Sherman-L | 20000   | 4000 | 104.4V      | 151.2V      | 21V/h       | 150.1V      | 1.8V           | 1.0667          | No   |
    And this simulated device:
      | Bt Name | Bt Address        | Km     | Mileage   | Voltage |
      | LK13447 | AB:CD:EF:GH:IJ:KL | 21.867 | 20020.518 | 141.01V |
    And I start the app

  Scenario: Connecting to a wheel to update its mileage for the first time
    Given I select the Sherman-L
    When I connect to the LK13447
    Then the mileage is updated to 20021 km
    And the wheel's Bluetooth name is updated

  Scenario: Reconnecting to a previously connected wheel to update its values
    Given the Sherman-L has a previous mileage of 2000 km
    And this wheel is connected:
      | Name      | Bt Name | Bt Address        |
      | Sherman-L | LK13447 | AB:CD:EF:GH:IJ:KL |
    And I select the Sherman-L
    When I reconnect to the wheel
    Then the mileage is updated to 22021 km
    And the voltage is updated to 141.0V and the battery 66.3%
    And the km is updated to 20.5
    And I can charge the wheel

#  FIXME-2 Scenario for adding a wheel and connecting it right away
