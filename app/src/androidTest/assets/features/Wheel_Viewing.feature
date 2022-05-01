Feature: Wheel Viewing

  Background:
    Given these wheels:
      | Name            | Mileage | Voltage Min | Voltage Max |
      | Veteran Sherman | 17622   | 75.6V       | 100.8V      |
      | Veteran Abrams  | 0       | 74.5V       | 100.8V      |
      | KingSong S20    | 0       | 90V         | 126V        |
      | KingSong 14S    | 694     | 48V         | 67.2V       |
      | Gotway Nikola+  | 2927    | 78V         | 100.8V      |
      | KingSong S18    | 2850    | 60V         | 84V         |
    And I start the app

  Scenario Outline: Viewing a wheel's details in full - [<previous mileage>]
    Given the <wheel> has a previous mileage of <previous mileage>
    When I select the <wheel>
    Then the details view shows the <wheel> with a mileage of <expected mileage>
    Examples:
      | wheel           | previous mileage | expected mileage |
      | Veteran Sherman | 0                | 17622            |
      | Veteran Sherman | 10000            | 27622            |

  Scenario Outline: Calculating percentage [<wheel> / <voltage>]
    Given I select the <wheel>
    When I enter a voltage of <voltage>
    Then it displays a percentage of <battery>
    Examples:
      | wheel           | voltage | battery |
      | Gotway Nikola+  | 96.4V   | 80.7%   |
      | Gotway Nikola+  | 89.1V   | 48.7%   |
      | KingSong 14S    | 63.5V   | 80.7%   |
      | KingSong S18    | 71.4V   | 47.5%   |
      | Veteran Sherman | 96.5V   | 82.9%   |
      | KingSong S20    | 108V    | 50.0%   |

  # FIXME-1 Put error values under a "blank estimated values" error scenario
  @WIP
  Scenario Outline: Calculating estimated values based on km [<wheel> / <km> / <voltage>]
    Given I select the <wheel>
    And the distance so far is set to <km>
    When I enter a voltage of <voltage>
    Then it displays an estimated remaining range of <remaining>
    And it displays an estimated total range of <total>
    And it displays an estimated wh/km of <wh/km>
    Examples:
      | wheel           | km   | voltage | remaining | total   | wh/km      |
      | Veteran Sherman | 42.0 | 91.9V   | 56.2 km   | 98.2 km | 30.3 wh/km |
      | Veteran Sherman | 81.0 | 83.5V   | 16.4 km   | 97.4 km | 30.5 wh/km |
#      | Veteran Sherman | 20 |         |         |
      | Veteran Sherman |      | 91.9V   |           |         |            |
#      | Veteran Sherman | aa |         |         |
#      | Veteran Sherman |    | bb      |         |
#      | Veteran Sherman | aa | bb      |         |

  Scenario: => Editing the wheel
    Given I select the Veteran Sherman
    When I edit the wheel
    Then it shows that every field is editable

  Scenario: => Deleting the wheel
    Given I select the Veteran Abrams
    When I delete the wheel
    And I confirm the deletion
    Then I am back to the main screen and the wheel is gone
