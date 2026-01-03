Feature: Wheel Charging

  Background:
    Given this wheel:
      | Name      | Mileage | Wh   | Voltage Min | Voltage Max | Charge Rate | Full Charge | Charger Offset | Distance Offset | Sold |
      | Sherman L | 4000    | 4000 | 104.4V      | 151.2V      | 21V/h       | 150.1V      | 1.8V           | 1.0667          | No   |
    And this simulated device:
      | Bt Name | Bt Address        | Km     | Mileage   | Voltage |
      | LK13447 | AB:CD:EF:GH:IJ:KL | 21.867 | 20020.518 | 136.5V  |
    And this wheel is connected:
      | Name      | Bt Name | Bt Address        |
      | Sherman L | LK13447 | AB:CD:EF:GH:IJ:KL |

    And the current time is 11:45
    And I start the app

    And I select the Sherman L
    And I set the actual voltage to 137.0V
    And I set the distance to 30 km
    And it displays these estimates:
      | remaining | total range |
      | 31.2      | 61.2        |
    And I charge the wheel
#    FIXME-1 Start with everything disabled, tell the user to connect to start the charging process

  Scenario: Changing the actual voltage
    Given I request to charge for 30 km
    And it displays an actual voltage of 138.3V
    And it displays these charging estimates:
      | required      | target | time        |
      | 143.8V (+5.5) | 142.0V | 12:01 (16m) |
    When I change the actual voltage to 140.0V
    Then it displays these charging estimates:
      | required      | target | time        |
      | 143.8V (+3.8) | 142.0V | 11:56 (11m) |

  Scenario Outline: Charging a wheel by distance [<distance>]
    When I request to charge for <distance>
    Then it displays an actual voltage of 138.3V
    And the full charge indicator is <fc_indicator>
    And it displays these charging estimates:
      | required   | target   | time   |
      | <required> | <target> | <time> |
    Examples:
      | distance | fc_indicator | required       | target | time        |
      | 0 km     | off          | 138.3V         | 136.5V | Go!         |
      | 10 km    | off          | 132.3V         | 130.5V | Go!         |
      | 20 km    | off          | 138.7V (+0.4)  | 136.9V | 11:46 (1m)  |
      | 40 km    | off          | 150.1V (+11.8) | 148.3V | 12:19 (34m) |
      | 50 km    | off          | 150.1V (+11.8) | 148.3V | 12:19 (34m) |
      | full     | on           | 150.1V (+11.8) | 148.3V | 12:19 (34m) |

  Scenario Outline: Charging a wheel by voltage [<required>]
    When I request to charge to <required>
    Then it displays an actual voltage of 138.3V
    And the full charge indicator is off
    And it displays these charging estimates:
      | required   | target   | time   |
      | <required> | <target> | <time> |
    Examples:
      | required       | target | time        |
      | 135.6V         | 133.8V | Go!         |
      | 140.0V (+1.7)  | 138.2V | 11:50 (5m)  |
      | 142.7V (+4.4)  | 140.9V | 11:58 (13m) |
      | 144.5V (+6.2)  | 142.7V | 12:03 (18m) |
      | 147.9V (+9.6)  | 146.1V | 12:12 (27m) |
      | 150.1V (+11.8) | 148.3V | 12:19 (34m) |

  Scenario: Reconnect to update the voltage
    Given I request to charge for 20 km
    And I change the actual voltage to 140.0V
    When I reconnect to update the voltage
    Then it displays an actual voltage of 136.5V
    And it displays these charging estimates:
      | required      | target | time       |
      | 138.7V (+2.2) | 136.9V | 11:51 (6m) |
