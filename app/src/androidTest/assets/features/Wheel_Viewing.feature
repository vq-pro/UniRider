Feature: Wheel Viewing

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Distance Offset | Sold |
      | Sherman     | 17622   | 3200 | 75.6V       | 80V             | 100.8V      | 7.5V/h      | 99.5V       | 1.5V           | 1               | No   |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        | 99.5V       | 1.5V           | 1               | No   |
      | Sherman L   | 4000    | 4000 | 104.4V      | 120V            | 151.2V      | 21V/h       | 149.3V      | 1.8V           | 1.0181          | No   |
      | 14S         | 694     | 840  | 48V         | 55V             | 67.2V       | 4V/h        | 65.5V       | 1.5V           | 1               | No   |
      | S18         | 2850    | 1110 | 60V         | 68V             | 84V         | 4V/h        | 81.5V       | 1.5V           | 1               | No   |
      | Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      | 6V/h        | 99.5V       | 1.5V           | 1               | Yes  |
      | Abrams      | 95      | 2700 | 74.5V       | 80V             | 100.8V      | 14V/h       | 99.5V       | 1.5V           | 1               | Yes  |
    And I start the app

  Scenario Outline: Calculating estimated values based on km [<wheel> / <km> / <voltage>]
    Given I select the <wheel>
    And I set the distance to <distance>
    When I set the actual voltage to <voltage>
    Then it displays these estimates:
      | remaining   | total range |
      | <remaining> | <total>     |
    Examples:
      | wheel       | distance | voltage | remaining | total |
      | Sherman L   | 10.5 km  | 142.8V  | 50.1      | 60.6  |
      | Sherman L   | 20 km    | 141.1V  | 40.5      | 60.5  |
      | Sherman L   | 30 km    | 136.9V  | 30.5      | 60.5  |
      | Sherman Max | 42 km    | 91V     | 39.3      | 81.3  |
      | Sherman Max | 81 km    | 83.5V   | 9.5       | 90.5  |
      | Sherman Max | 60 km    | 83.5V   | 7.0       | 67.0  |
      | Sherman Max | 40 km    | 83.5V   | 4.7       | 44.7  |
      | S18         | 21 km    | 72V     | 6.1       | 27.1  |
      | S18         | 42 km    | 67V     | 0         | 42.0  |

  Scenario Outline: Calculating estimated values based on km - ERROR [<wheel> / <km> / <voltage>]
    Given I select the <wheel>
    And I set the distance to <km>
    When I set the actual voltage to <voltage>
    Then it displays blank estimated values
    Examples:
      | wheel   | km      | voltage |
      | Sherman | 20 km   |         |
      | Sherman |         | 91.9V   |
      | Sherman | 20.2 km | 9       |
      | Sherman | aa      |         |
      | Sherman |         | bb      |
      | Sherman | aa      | bb      |

  Scenario Outline: Calculating percentage [<wheel> / <voltage>]
    Given I select the <wheel>
    When I set the actual voltage to <voltage>
    Then it displays a percentage of <battery>
    Examples:
      | wheel       | voltage | battery |
      | 14S         | 63.5V   | 83.2%   |
      | S18         | 71.4V   | 19.5%   |
      | Sherman     | 96.5V   | 95.3%   |
      | Sherman Max | 91.9V   | 53.6%   |

  Scenario: => Charging the wheel
    Given I select the Sherman
    And I cannot charge the wheel
    When I set the actual voltage to 91.9V
    Then I cannot charge the wheel
    When I set the distance to 20 km
    Then I can charge the wheel

  Scenario: => Editing the wheel
    Given I select the Sherman
    When I edit the wheel
    Then it shows that every field is editable

  Scenario: => Editing the wheel with estimated values
    Given I select the Sherman Max
    And I set the distance to 38 km
    And I set the actual voltage to 91.9V
    And it displays these estimates:
      | remaining | total range |
      | 44.0      | 82.0        |
    When I edit the wheel
    And I go back to view the wheel
    Then it displays a percentage of 53.6%
    And it displays these estimates:
      | remaining | total range |
      | 44.0      | 82.0        |

  Scenario Outline: Viewing a wheel's details in full - [<previous mileage>]
    Given the Sherman has a previous mileage of <previous mileage>
    When I select the Sherman
    Then the details view shows the Sherman with a mileage of <actual mileage>
    Examples:
      | previous mileage | actual mileage |
      | 0 km             | 17622 km       |
      | 10000 km         | 27622 km       |
