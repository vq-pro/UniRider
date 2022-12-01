Feature: Wheel Charging - ERROR

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        |
    And I start the app
    And I select the Sherman Max
    And I set the starting voltage to 100.6V
    And I set the actual voltage to 86.4V
    And I set the distance to 50 km
    And I charge the wheel
    And it displays an estimated rate of 40.6 wh/km

  Scenario: Update the voltage - ERROR - Never connected
    When I request to charge for 40 km
    Then I cannot connect to the wheel on the charge screen
