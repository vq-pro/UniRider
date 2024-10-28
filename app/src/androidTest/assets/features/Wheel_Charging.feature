Feature: Wheel Charging

  Background:
    Given this wheel:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Sold |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        | 99.5V       | 1.5V           | No   |
    And this wheel is connected:
      | Name        | Bt Name | Bt Address        |
      | Sherman Max | LK1234  | C0:C1:C2:C3:C4:C5 |
    And the current time is 11:45
    And I start the app
    And I select the Sherman Max
    And I set the actual voltage to 86.4V
    And I set the distance to 50 km
    And it displays these estimates:
      | remaining | total range |
      | 14.5      | 64.5        |
    And I charge the wheel

  Scenario: Changing the voltage
    Given I request to charge for 40 km
    And it displays an actual voltage of 87.9V
    And it displays these charging estimates:
      | required voltage | time  |
      | 95.0V (+7.1)     | 12:38 |
    When I change the voltage to 90V
    Then it displays these charging estimates:
      | required voltage | time  |
      | 95.0V (+5.0)     | 12:23 |

  Scenario Outline: Charging a wheel [<distance>]
    When I request to charge for <distance>
    Then it displays an actual voltage of 87.9V
    And it displays these charging estimates:
      | required voltage   | time   |
      | <required voltage> | <time> |
    Examples:
      | distance | required voltage | time  |
      | 10 km    | Go!              |       |
      | 20 km    | 89.9V (+2.0)     | 12:00 |
      | 40 km    | 95.0V (+7.1)     | 12:38 |
      | 50 km    | 96.3V (+8.4)     | 12:48 |
      | 60 km    | 97.7V (+9.8)     | 12:59 |
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
      | 95.0V (+6.5)     | 12:34 |
