Feature: Wheel Charging

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      |
    And I start the app

  Scenario Outline: Charging a wheel [<distance>]
    Given I select the Sherman Max
    And I set the starting voltage to 100.6V
    And I set the actual voltage to 92.5V
    And I set the distance to 38 km
    And I charge the wheel
    And it displays an estimated rate of 30+ wh/km
    When I request to charge for <distance>
    Then it displays an actual voltage of 92.5V
    And it displays a required voltage of <voltage>
    Examples:
      | distance | voltage |
      | 20 km    | Go!     |
      | 40 km    | Go!     |
      | 50 km    | 94.2V   |
      | 200 km   | 99.3V   |
      |          |         |
      | aa       |         |
