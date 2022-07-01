Feature: Wheel Charging

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Sherman     | 17622   | 3200 | 75.6V       | 80V             | 100.8V      |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      |
      | 14S         | 694     | 840  | 48V         | 55V             | 67.2V       |
      | S18         | 2850    | 1110 | 60V         | 68V             | 84V         |
      | Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      |
    And I start the app

  @WIP
  Scenario: Charging a wheel
    Given I select the Sherman Max
    And I set the starting voltage to 100.4V
    And I set the actual voltage to 91.9V
    And I set the distance to 42 km
    When I charge the wheel
    Then it displays an estimated wh/km of 28.9 wh/km
    # FIXME-1 Enter a distance, get a voltage to charge to
