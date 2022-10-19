Feature: Wheel Charging

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      |
    And I start the app

  @WIP
  Scenario Outline: Charging a wheel [<distance>]
    Given I select the Sherman Max
    And I set the starting voltage to 100.4V
    And I set the actual voltage to 91.9V
    And I set the distance to 42 km
    And I charge the wheel
    And it displays an estimated rate of 25+ wh/km
    When I request to charge for <distance>
    Then it displays an actual voltage of 91.9V
    And it displays a required voltage of <voltage>
    Examples:
      | distance | voltage |
#      | 20 km    | Ready!  |
      | 50 km    | 94.0V   |
#      | 200 km   | Full!   |
      |          |         |
      | aa       |         |
