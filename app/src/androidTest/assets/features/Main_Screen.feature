Feature: Main Screen

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Max |
      | Sherman     | 17622   | 3200 | 75.6V       | 100.8V      |
      | Sherman Max | 2000    | 3600 | 75.6V       | 100.8V      |
      | 14S         | 694     | 840  | 48V         | 67.2V       |
      | Nikola+     | 2927    | 1800 | 78V         | 100.8V      |
      | S18         | 2850    | 1110 | 60V         | 84V         |

  Scenario: On entering, we see a list of registered wheels and their mileage
    Given the Sherman has a previous mileage of 3600
    When I start the app
    Then I see my wheels and their mileage:
      | Name        | Mileage |
      | Sherman     | 21222   |
      | Nikola+     | 2927    |
      | S18         | 2850    |
      | Sherman Max | 2000    |
      | 14S         | 694     |
      | <New>       |         |
    And I see the total mileage

  Scenario: => Viewing a wheel's details
    Given I start the app
    When I select the S18
    Then the details view shows the details for that wheel

  Scenario: => Adding a wheel
    Given I start the app
    When I add a new wheel
    Then I can enter the details for that wheel
