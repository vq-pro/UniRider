Feature: Wheel Charging

  Background:
    Given this wheel:
      | Name      | Mileage | Wh   | Voltage Min | Voltage Max | Full Charge | Charge Amperage | Charge Rate | Distance Offset | Sold |
      | Sherman L | 4000    | 4000 | 104.4V      | 151.2V      | 150.1V      | 20A             | 21V/h       | 1.0667          | No   |
      | Sherman   | 17622   | 3200 | 75.6V       | 100.8V      | 99.5V       | 9.5A            | 7.5V/h      | 1               | No   |
    And this wheel is connected:
      | Name      | Bt Name | Bt Address        |
      | Sherman L | LK13447 | AB:CD:EF:GH:IJ:KL |
    And this simulated device:
      | Bt Name | Bt Address        | Km     | Mileage   | Voltages     |
      | LK13447 | AB:CD:EF:GH:IJ:KL | 21.867 | 20020.518 | 136.5V, 138V |

    And the current time is 11:45
    And I start the app

    And I select the Sherman L
    And I set the actual voltage to 137.0V
    And I set the distance to 30 km
    And it displays these estimates:
      | remaining | total range |
      | 31.2      | 61.2        |
    And I charge the wheel

  Scenario: Changing the actual voltage
    Given I reconnect to update the voltage
    And I request to charge for 30 km
    And it displays an actual voltage of 138.0V
    And it displays these charging estimates:
      | target        | required      | time        |
      | 143.5V (+5.5) | 142.0V (-1.5) | 12:01 (16m) |
    When I change the actual voltage to 140.0V
    Then it displays these charging estimates:
      | target        | required      | time        |
      | 143.5V (+3.5) | 142.0V (-1.5) | 11:55 (10m) |

  Scenario: Changing the amperage
    Given I reconnect to update the voltage
    And I request to charge for 30 km
    And it displays an actual voltage of 138.0V
    And it displays an amperage of 20A
    And it displays these charging estimates:
      | target        | required      | time        |
      | 143.5V (+5.5) | 142.0V (-1.5) | 12:01 (16m) |
    When I change the amperage to 9.5A
    Then it displays these charging estimates:
      | target        | required      | time        |
      | 143.5V (+5.5) | 142.0V (-1.5) | 12:18 (33m) |

  Scenario Outline: Charging a wheel by distance [<distance>]
    Given I reconnect to update the voltage
    When I request to charge for <distance>
    Then it displays an actual voltage of 138.0V
    And the full charge indicator is <fc_indicator>
    And it displays these charging estimates:
      | target   | required   | time   |
      | <target> | <required> | <time> |
    Examples:
      | distance | fc_indicator | target         | required      | time        |
      | 0 km     | off          | 138.0V         | 136.5V (-1.5) | Go!         |
      | 10 km    | off          | 132.0V         | 130.5V (-1.5) | Go!         |
      | 20 km    | off          | 138.4V (+0.4)  | 136.9V (-1.5) | 11:46 (1m)  |
      | 30 km    | off          | 143.5V (+5.5)  | 142.0V (-1.5) | 12:01 (16m) |
      | 40 km    | off          | 150.1V (+12.1) | 148.6V (-1.5) | 12:20 (35m) |
      | 50 km    | off          | 150.1V (+12.1) | 148.6V (-1.5) | 12:20 (35m) |
      | full     | on           | 150.1V (+12.1) | 148.6V (-1.5) | 12:20 (35m) |

  Scenario Outline: Charging a wheel by voltage [<required>]
    Given I reconnect to update the voltage
    When I request to charge to <required>
    Then it displays an actual voltage of 138.0V
    And the full charge indicator is off
    And it displays these charging estimates:
      | target   | required   | time   |
      | <target> | <required> | <time> |
    Examples:
      | target         | required      | time        |
      | 135.6V         | 134.1V (-1.5) | Go!         |
      | 140.0V (+2.0)  | 138.5V (-1.5) | 11:51 (6m)  |
      | 142.7V (+4.7)  | 141.2V (-1.5) | 11:58 (13m) |
      | 144.5V (+6.5)  | 143.0V (-1.5) | 12:04 (19m) |
      | 147.9V (+9.9)  | 146.4V (-1.5) | 12:13 (28m) |
      | 150.1V (+12.1) | 148.6V (-1.5) | 12:20 (35m) |

  Scenario: Charging a wheel that is not connected
    Given I go back to view the wheel
    And I go back to the main view
    And I select the Sherman
    And I set the actual voltage to 89.0V
    And I set the distance to 30 km
    And I charge the wheel
    When I change the actual voltage to 91.0V
    And I request to charge for 40 km
    Then it displays an actual voltage of 91.0V
    And it displays these charging estimates:
      | target       | required     | time        |
      | 97.5V (+6.5) | 95.5V (-2.0) | 12:37 (52m) |

  Scenario: Start charging
    Given I see the charge warning
    And it displays no actual voltage
    And it displays empty charging estimates
    When I reconnect to update the voltage
    Then I don't see the charge warning
    And it displays an actual voltage of 138.0V
    And it displays these charging estimates:
      | target         | required      | time        |
      | 150.1V (+12.1) | 148.6V (-1.5) | 12:20 (35m) |

  Scenario: Reconnect to update the voltage
    Given I reconnect to update the voltage
    And I request to charge for 20 km
    And I change the actual voltage to 140.0V
    When I reconnect to update the voltage
    Then it displays an actual voltage of 136.5V
    And it displays these charging estimates:
      | target        | required      | time       |
      | 138.4V (+1.9) | 136.9V (-1.5) | 11:50 (5m) |
