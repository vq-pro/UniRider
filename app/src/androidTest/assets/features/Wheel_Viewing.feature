Feature: Wheel Viewing

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Max |
      | Sherman     | 17622   | 3200 | 75.6V       | 100.8V      |
      | Sherman Max | 2000    | 3600 | 75.6V       | 100.8V      |
      | 14S         | 694     | 840  | 48V         | 67.2V       |
      | Nikola+     | 2927    | 1800 | 78V         | 100.8V      |
      | S18         | 2850    | 1110 | 60V         | 84V         |
    And I start the app

  Scenario Outline: Viewing a wheel's details in full - [<previous mileage>]
    Given the <wheel> has a previous mileage of <previous mileage>
    When I select the <wheel>
    Then the details view shows the <wheel> with a mileage of <expected mileage>
    Examples:
      | wheel   | previous mileage | expected mileage |
      | Sherman | 0                | 17622            |
      | Sherman | 10000            | 27622            |

  Scenario Outline: Calculating percentage [<wheel> / <voltage>]
    Given I select the <wheel>
    When I enter a voltage of <voltage>
    Then it displays a percentage of <battery>
    Examples:
      | wheel   | voltage | battery |
      | Nikola+ | 96.4V   | 80.7%   |
      | Nikola+ | 89.1V   | 48.7%   |
      | 14S     | 63.5V   | 80.7%   |
      | S18     | 71.4V   | 47.5%   |
      | Sherman | 96.5V   | 82.9%   |

  Scenario Outline: Calculating estimated values based on km [<wheel> / <km> / <voltage>]
    Given I select the <wheel>
    And the distance so far is set to <km>
    When I enter a voltage of <voltage>
    Then it displays an estimated remaining range of <remaining>
    And it displays an estimated total range of <total>
    And it displays an estimated wh/km of <wh/km>
    Examples:
      | wheel       | km   | voltage | remaining | total   | wh/km      |
      | Sherman Max | 42.0 | 91.9V   | 56.2 km   | 98.2 km | 30.3 wh/km |
      | Sherman Max | 81.0 | 83.5V   | 16.4 km   | 97.4 km | 30.5 wh/km |
      | Sherman Max | 42.0 | 91.     | 47.1 km   | 89.1 km | 33.3 wh/km |

  Scenario Outline: Calculating estimated values based on km - ERROR [<wheel> / <km> / <voltage>]
    Given I select the <wheel>
    And the distance so far is set to <km>
    When I enter a voltage of <voltage>
    Then it displays blank estimated values
    Examples:
      | wheel   | km   | voltage |
      | Sherman | 20   |         |
      | Sherman |      | 91.9V   |
      | Sherman | 20.2 | 9       |
      | Sherman | aa   |         |
      | Sherman |      | bb      |
      | Sherman | aa   | bb      |

  Scenario: => Editing the wheel
    Given I select the Sherman
    When I edit the wheel
    Then it shows that every field is editable

  Scenario: => Deleting the wheel
    Given I select the Nikola+
    When I delete the wheel
    And I confirm the deletion
    Then I am back to the main screen and the wheel is gone
