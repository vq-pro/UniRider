Feature: Wheel Charging - ERROR

  Background:
    Given this wheel:
      | Name      | Mileage | Wh   | Voltage Min | Voltage Max | Charge Rate | Full Charge | Charger Offset | Distance Offset | Sold |
      | Sherman L | 4000    | 4000 | 104.4V      | 151.2V      | 21V/h       | 150.1V      | 1.8V           | 1.0667          | No   |
    And this wheel is connected:
      | Name      | Bt Name | Bt Address        |
      | Sherman L | LK13447 | AB:CD:EF:GH:IJ:KL |
    And this simulated device:
      | Bt Name | Bt Address        | Km     | Mileage   | Voltages     |
      | LK13447 | AB:CD:EF:GH:IJ:KL | 21.867 | 20020.518 | 136.5V, 138V |

    And I start the app

    And I select the Sherman L
    And I set the actual voltage to 137.0V
    And I set the distance to 50 km
    And I charge the wheel
    And I reconnect to update the voltage

  Scenario Outline: Changing the voltage - ERROR [<voltage>]
    When I change the actual voltage to <voltage>
    Then it displays empty charging estimates
    Examples:
      | voltage |
      |         |
      | 1V      |
      | aa      |

  Scenario Outline: Charging a wheel - ERROR [<distance>]
    When I request to charge for <distance>
    Then it displays an actual voltage of 138.0V
    And it displays empty charging estimates
    Examples:
      | distance |
      |          |
      | aa       |
