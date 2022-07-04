Feature: Wheel Charging

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      |
    And I start the app

  Scenario: Charging a wheel
    Given I select the Sherman Max
    And I set the starting voltage to 100.4V
    And I set the actual voltage to 91.9V
    And I set the distance to 42 km
    And I charge the wheel
    And it displays an estimated wh/km of 28.9 wh/km
    When I request to charge for 20 km
    Then it displays a required voltage of 85.5V

    # FIXME-1 Verify a maximum voltage of 99.3V (100.8V - 1.5V) or blank
    # FIXME-1 Verify a blank voltage for invalid distances
