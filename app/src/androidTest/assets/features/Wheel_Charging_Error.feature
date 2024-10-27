Feature: Wheel Charging - ERROR

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Sold |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        | 99.5V       | 1.5V           | No   |
    And I start the app
    And I select the Sherman Max
    And I set the actual voltage to 86.4V
    And I set the distance to 50 km
    And I charge the wheel

  Scenario Outline: Changing the voltage - ERROR [<voltage>]
    When I change the voltage to <voltage>
    Then it displays these charging estimates:
      | required voltage | time |
      |                  |      |
    Examples:
      | voltage |
      |         |
      | 1V      |
      | aa      |

  Scenario Outline: Charging a wheel - ERROR [<distance>]
    When I request to charge for <distance>
    Then it displays an actual voltage of 87.9V
    And it displays these charging estimates:
      | required voltage   | time   |
      | <required voltage> | <time> |
    Examples:
      | distance | required voltage | time |
      |          |                  |      |
      | aa       |                  |      |

  Scenario: Update the voltage - ERROR - Never connected
    When I request to charge for 40 km
    Then I cannot connect to the wheel on the charge screen
