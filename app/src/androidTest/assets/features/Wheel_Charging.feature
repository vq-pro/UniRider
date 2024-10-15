Feature: Wheel Charging

  Background:
    Given this wheel:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Sold |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        | 99.5V       | 1.5V           | No   |
    And this wheel is connected:
      | Name        | Bt Name | Bt Address        |
      | Sherman Max | LK1234  | C0:C1:C2:C3:C4:C5 |
    And I start the app
    And I select the Sherman Max
    And I set the starting voltage to 100.6V
    And I set the actual voltage to 86.4V
    And I set the distance to 50 km
    And I charge the wheel
    And the current time is 11:45

  Scenario: Changing the rate
    Given I request to charge for 40 km
    And it displays an actual voltage of 87.9V
    And it displays these charging estimates:
      | required voltage | time  |
      | 94.9V (+7.0)     | 12:38 |
    When I change the rate to 35 wh/km
    Then it displays these charging estimates:
      | required voltage | time  |
      | 93.3V (+5.4)     | 12:26 |

  Scenario: Changing the voltage
    Given I request to charge for 40 km
    And it displays an actual voltage of 87.9V
    And I change the rate to 35 wh/km
    And it displays these charging estimates:
      | required voltage | time  |
      | 93.3V (+5.4)     | 12:26 |
    When I change the voltage to 90V
    Then it displays these charging estimates:
      | required voltage | time  |
      | 93.3V (+3.3)     | 12:10 |

  Scenario Outline: Charging a wheel [<distance>]
    When I request to charge for <distance>
    Then it displays an actual voltage of 87.9V
    And it displays these charging estimates:
      | required voltage   | time   |
      | <required voltage> | <time> |
    Examples:
      | distance | required voltage | time  |
      | 15 km    | Go!              |       |
      | 20 km    | 89.2V (+1.3)     | 11:55 |
      | 40 km    | 94.9V (+7.0)     | 12:38 |
      | 50 km    | 97.7V (+9.8)     | 12:59 |
      | 60 km    | 99.5V (+11.6)    | 13:12 |
      | 200 km   | 99.5V (+11.6)    | 13:12 |
      | full     | 99.5V (+11.6)    | 13:12 |

  Scenario: Reconnect to update the voltage
    Given this simulated device:
      | Bt Name | Bt Address        | Km     | Mileage | Voltage  |
      | LK1234  | C0:C1:C2:C3:C4:C5 | 12.218 | 705.615 | 88.5001V |
    And I request to charge for 40 km
    When I reconnect to update the voltage
    Then it displays an actual voltage of 88.5V
    And it displays these charging estimates:
      | required voltage | time  |
      | 94.9V (+6.4)     | 12:33 |
