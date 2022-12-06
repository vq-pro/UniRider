Feature: Wheel Charging

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        |
    And these wheels are connected:
      | Name        | Bt Name | Bt Address        |
      | Sherman Max | LK1234  | C0:C1:C2:C3:C4:C5 |
    And I start the app
    And I select the Sherman Max
    And I set the starting voltage to 100.6V
    And I set the actual voltage to 86.4V
    And I set the distance to 50 km
    And I charge the wheel
    And it displays an estimated rate of 40.6 wh/km

  Scenario Outline: Charging a wheel [<distance>]
    When I request to charge for <distance>
    Then it displays an actual voltage of 87.9V
    And it displays a required voltage of <required voltage>
    And it displays a remaining time of <time>
    Examples:
      | distance | required voltage | time |
      | 15 km    | Go!              |      |
      | 20 km    | 89.2V (+1.3)     | 10m  |
      | 40 km    | 94.9V (+7.0)     | 53m  |
      | 50 km    | 97.7V (+9.8)     | 1h14 |
      | 60 km    | 99.3V (+11.4)    | 1h25 |
      | 200 km   | 99.3V (+11.4)    | 1h25 |
      |          |                  |      |
      | aa       |                  |      |

#    FIXME-1 Change rate scenario

  @Ignore
  Scenario: Update the voltage
    Given this simulated device:
      | Bt Name | Bt Address        | Km     | Mileage | Voltage |
      | LK1234  | C0:C1:C2:C3:C4:C5 | 12.218 | 705.615 | 88.5V   |
    And I request to charge for 40 km
    When I reconnect to update the voltage
    Then it displays an actual voltage of 88.5V
    And it displays a required voltage of 94.9V (+6.4)
    And it displays a remaining time of 48m
