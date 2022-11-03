Feature: Wheel Charging

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      |
    And I start the app

  # FIXME-0 Activate time display
  @WIP
  Scenario Outline: Charging a wheel [<distance>]
    Given I select the Sherman Max
    And I set the starting voltage to 100.6V
    And I set the actual voltage to 86.4V
    And I set the distance to 50 km
    And I charge the wheel
    And it displays an estimated rate of 40+ wh/km
    When I request to charge for <distance>
    Then it displays a required voltage of <voltage>
    And it displays a remaining time of <time>
    Examples:
      | distance | voltage | time |
      | 15 km    | Go!     |      |
#      | 20 km    | 89.2V (+1.3) | 10m   |
#      | 40 km    | 94.9V (+7.0) | 53m   |
#      | 50 km    | 97.7V (+9.8) | 1h14m |
#      | 60 km    | Fill up!     | 1h37m |
      |          |         |      |
      | aa       |         |      |

    # FIXME-1 Add a connect button to the charge screen to refresh the voltage