Feature: Wheel Viewing

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Sherman     | 17622   | 3200 | 75.6V       | 80V             | 100.8V      |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      |
      | 14S         | 694     | 840  | 48V         | 55V             | 67.2V       |
      | S18         | 2850    | 1110 | 60V         | 68V             | 84V         |
      | Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      |
    And I start the app

  Scenario Outline: Viewing a wheel's details in full - [<previous mileage>]
    Given the Sherman has a previous mileage of <previous mileage>
    When I select the Sherman
    Then the details view shows the Sherman with a mileage of <expected mileage> and a starting voltage of 100.8V
    Examples:
      | previous mileage | expected mileage |
      | 0 km             | 17622 km         |
      | 10000 km         | 27622 km         |

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

  Scenario Outline: Calculating estimated values based on km [<wheel> / <km> / <starting voltage> / <voltage>]
    Given I select the <wheel>
    And I set the starting voltage to <starting voltage>
    And I set the distance to <km>
    When I enter an actual voltage of <voltage>
    Then it displays an estimated remaining range of "<remaining>"
    And it displays an estimated total range of "<total>"
    And it displays an estimated wh/km of "<wh/km>"
    Examples:
      | wheel       | starting voltage | km   | voltage | remaining | total    | wh/km      |
      | Sherman Max | 100.4V           | 42.0 | 91.9V   | 58.8 km   | 100.8 km | 28.9 wh/km |
      | Sherman Max | 100.4V           | 81.0 | 83.5V   | 16.8 km   | 97.8 km  | 29.8 wh/km |
      | Sherman Max | 100.4V           | 42.0 | 91V     | 49.1 km   | 91.1 km  | 32.0 wh/km |
      | Sherman Max | 98.2V            | 42.0 | 91V     | 64.2 km   | 106.2 km | 24.5 wh/km |
      | S18         | 84V              | 42.0 | 67V     | 0 km      | 42.0 km  | 18.7 wh/km |

  Scenario Outline: Calculating estimated values based on km - ERROR [<wheel> / <km> / <voltage>]
    Given I select the <wheel>
    And I set the distance to <km>
    When I enter an actual voltage of <voltage>
    Then it displays blank estimated values
    Examples:
      | wheel   | km   | voltage |
      | Sherman | 20   |         |
      | Sherman |      | 91.9V   |
      | Sherman | 20.2 | 9       |
      | Sherman | aa   |         |
      | Sherman |      | bb      |
      | Sherman | aa   | bb      |

  Scenario: Saving the starting voltage
    Given I select the Sherman
    And the starting voltage is 100.8V
    When I set the starting voltage to 98.5V
    And I go back to the main view
    And I select the Sherman
    Then the starting voltage is 98.5V

  @WIP
  Scenario: => Charging the wheel
    Given I select the Sherman
    When I charge the wheel
    Then it shows that it's ready to help with charging

  Scenario: => Editing the wheel
    Given I select the Sherman
    When I edit the wheel
    Then it shows that every field is editable

  Scenario: => Editing the wheel with estimated values
    Given I select the Sherman Max
    And I set the distance to 42
    And I set the current voltage to 91.9
    And it displays an estimated remaining range of "56.2 km"
    When I edit the wheel
    And I go back to view the wheel
    Then it displays a percentage of 64.7%
    And it displays an estimated remaining range of "56.2 km"
    And it displays an estimated total range of "98.2 km"
    And it displays an estimated wh/km of "30.3 wh/km"
