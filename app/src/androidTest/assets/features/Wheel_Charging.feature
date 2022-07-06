Feature: Wheel Charging

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      |
    And I start the app

  Scenario Outline: Charging a wheel [<distance>]
    Given I select the Sherman Max
    And I set the starting voltage to 100.4V
    And I set the actual voltage to 91.9V
    And I set the distance to 42 km
    And I charge the wheel
    And it displays an estimated wh/km of 28.9 wh/km
    When I request to charge for <distance>
    Then it displays a required voltage of <voltage>
    When I request to charge for 20 km
    Then it displays a required voltage of 85.5V
    Examples:
      | distance | voltage |
      | 20 km    | 85.5V   |
      | 200 km   | 99.3V   |
      |          |         |
      | aa       |         |
