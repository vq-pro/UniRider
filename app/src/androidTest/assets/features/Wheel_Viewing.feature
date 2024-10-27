Feature: Wheel Viewing

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Sold |
      | Sherman     | 17622   | 3200 | 75.6V       | 80V             | 100.8V      | 7.5V/h      | 99.5V       | 1.5V           | No   |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        | 99.5V       | 1.5V           | No   |
      | Sherman L   | 4000    | 4000 | 104.4V      | 120V            | 151.2V      | 21V/h       | 149.3V      | 1.8V           | No   |
      | 14S         | 694     | 840  | 48V         | 55V             | 67.2V       | 4V/h        | 65.5V       | 1.5V           | No   |
      | S18         | 2850    | 1110 | 60V         | 68V             | 84V         | 4V/h        | 81.5V       | 1.5V           | No   |
      | Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      | 6V/h        | 99.5V       | 1.5V           | Yes  |
      | Abrams      | 95      | 2700 | 74.5V       | 80V             | 100.8V      | 14V/h       | 99.5V       | 1.5V           | Yes  |
    And I start the app

#  FIXME-1 Eliminate starting voltage
  Scenario Outline: Calculating estimated values based on km [<wheel> / <km> / <voltage>]
    Given I select the <wheel>
#    And I set the starting voltage to <starting voltage>V
    And I set the distance to <distance>
    When I set the actual voltage to <voltage>
    Then it displays these estimates:
      | remaining   | total range | wh/km |
      | <remaining> | <total>     | 0.0   |
    Examples:
      | wheel       | distance | voltage | remaining | total |
      | Sherman L   | 20 km    | 141.1V  | 40.5      | 60.5  |
      | Sherman Max | 81 km    | 83.5V   | 9.5       | 90.5  |
      | Sherman Max | 42 km    | 91V     | 39.3      | 81.3  |
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

#    FIXME-1 Remove rate
  @Ignore
  Scenario: Calculating estimated values when changing rate
    Given I select the Sherman Max
    And I set the starting voltage to 100.6V
    And I set the distance to 20 km
    And I set the actual voltage to 94.1V
    And it displays these estimates:
      | remaining | total range | wh/km |
      | 37.3      | 57.3        | 46.4  |
    When I change the rate to 35 wh/km
    Then it displays these estimates:
      | remaining | total range | wh/km |
      | 49.4      | 69.4        | 35    |

  Scenario Outline: Calculating percentage [<wheel> / <voltage>]
    Given I select the <wheel>
    And I set the starting voltage to <voltage>
    When I set the actual voltage to <voltage>
    Then it displays a percentage of <battery>
    Examples:
      | wheel       | voltage | battery |
      | 14S         | 63.5V   | 83.2%   |
      | S18         | 71.4V   | 19.5%   |
      | Sherman     | 96.5V   | 95.3%   |
      | Sherman Max | 91.9V   | 53.6%   |

#    FIXME-1 Enable charging at all times
  Scenario Outline: => Charging the wheel [when <available>]
    Given I select the Sherman
    When the wh/km is <available>
    Then I <can> charge the wheel
    Examples:
      | available     | can    |
      | available     | can    |
      | not available | cannot |

  Scenario: => Editing the wheel
    Given I select the Sherman
    When I edit the wheel
    Then it shows that every field is editable

  Scenario: => Editing the wheel with estimated values
    Given I select the Sherman Max
    And I set the distance to 38 km
    And I set the actual voltage to 91.9V
    And it displays these estimates:
      | remaining | total range | wh/km |
      | 44.0      | 82.0        | 0.0   |
    When I edit the wheel
    And I go back to view the wheel
    Then it displays a percentage of 53.6%
    And it displays these estimates:
      | remaining | total range | wh/km |
      | 44.0      | 82.0        | 0.0   |

  Scenario: Saving the starting voltage
    Given I select the Sherman
    And the starting voltage is 100.8V
    When I set the starting voltage to 98.5V
    And I go back to the main view
    And I select the Sherman
    Then the starting voltage is 98.5V

  Scenario Outline: Viewing a wheel's details in full - [<previous mileage>]
    Given the Sherman has a previous mileage of <previous mileage>
    When I select the Sherman
    Then the details view shows the Sherman with a mileage of <actual mileage> and a starting voltage of 100.8V
    Examples:
      | previous mileage | actual mileage |
      | 0 km             | 17622 km       |
      | 10000 km         | 27622 km       |
