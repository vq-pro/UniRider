Feature: Wheel Viewing

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate |
      | Sherman     | 17622   | 3200 | 75.6V       | 80V             | 100.8V      | 7.5V/h      |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        |
      | 14S         | 694     | 840  | 48V         | 55V             | 67.2V       | 4V/h        |
      | S18         | 2850    | 1110 | 60V         | 68V             | 84V         | 4V/h        |
      | Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      | 6V/h        |
    And I start the app

  Scenario Outline: Calculating estimated values based on km [<wheel> / <km> / <starting voltage> / <voltage>]
    Given I select the <wheel>
    And I set the starting voltage to <starting voltage>V
    And I set the distance to <km> km
    When I enter an actual voltage of <voltage>V
    Then it displays these estimates:
      | remaining   | total range | wh/km   |
      | <remaining> | <total>     | <wh/km> |
    Examples:
      | wheel       | starting voltage | km   | voltage | remaining | total | wh/km |
      | Sherman Max | 100.6            | 38.0 | 91.9    | 43.3      | 81.3  | 32.7  |
      | Sherman Max | 100.4            | 81.0 | 83.5    | 7.2       | 88.2  | 29.8  |
      | Sherman Max | 100.4            | 42.0 | 91      | 40.2      | 82.2  | 32.0  |
      | Sherman Max | 98.2             | 42.0 | 91      | 52.5      | 94.5  | 24.5  |
      | S18         | 84               | 21.0 | 72      | 3.5       | 24.5  | 26.4  |
      | S18         | 84               | 42.0 | 67      | 0         | 42.0  | 18.7  |

  Scenario Outline: Calculating estimated values based on km - ERROR [<wheel> / <km> / <voltage>]
    Given I select the <wheel>
    And I set the distance to <km>
    When I enter an actual voltage of <voltage>
    Then it displays blank estimated values
    Examples:
      | wheel   | km      | voltage |
      | Sherman | 20 km   |         |
      | Sherman |         | 91.9V   |
      | Sherman | 20.2 km | 9       |
      | Sherman | aa      |         |
      | Sherman |         | bb      |
      | Sherman | aa      | bb      |

  Scenario: Calculating estimated values when changing rate
    Given I select the Sherman Max
    And I set the starting voltage to 100.6V
    And I set the distance to 20 km
    And I enter an actual voltage of 94.1V
    And it displays these estimates:
      | remaining | total range | wh/km |
      | 37.3      | 57.3        | 46.4  |
    When I change the rate to 35 wh/km
    Then it displays these estimates:
      | remaining | total range | wh/km |
      | 49.4      | 69.4        | 35    |

  Scenario Outline: Calculating percentage [<wheel> / <voltage>]
    Given I select the <wheel>
    When I enter an actual voltage of <voltage>
    Then it displays a percentage of <battery>
    Examples:
      | wheel       | voltage | battery |
      | Nikola+     | 96.4V   | 80.7%   |
      | Nikola+     | 89.1V   | 48.7%   |
      | 14S         | 63.5V   | 80.7%   |
      | S18         | 71.4V   | 47.5%   |
      | Sherman     | 96.5V   | 82.9%   |
      | Sherman Max | 91.9V   | 64.7%   |

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
      | 42.2      | 80.2        | 33.5  |
    When I edit the wheel
    And I go back to view the wheel
    Then it displays a percentage of 64.7%
    And it displays these estimates:
      | remaining | total range | wh/km |
      | 42.2      | 80.2        | 33.5  |

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
    Then the details view shows the Sherman with a mileage of <expected mileage> and a starting voltage of 100.8V
    Examples:
      | previous mileage | expected mileage |
      | 0 km             | 17622 km         |
      | 10000 km         | 27622 km         |
