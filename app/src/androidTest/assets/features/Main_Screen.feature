Feature: Main Screen

  Background:
    Given these wheels:
      | Name          | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Sold |
      | Sherman       | 17622   | 3200 | 75.6V       | 80V             | 100.8V      | 6V/h        | No   |
      | x Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        | Yes  |
      | 14S           | 694     | 840  | 48V         | 55V             | 67.2V       | 4V/h        | No   |
      | x Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      | 6V/h        | Yes  |
      | x S18         | 2850    | 1110 | 60V         | 68V             | 84V         | 4V/h        | Yes  |

  Scenario: On entering, we see a list of registered wheels and their mileage
    Given the Sherman has a previous mileage of 3600 km
    When I start the app
    Then I see my wheels and their mileage:
      | Name    | Mileage |
      | Sherman | 21222   |
      | 14S     | 694     |
      | <Sold>  | 7777    |
      | <New>   |         |
    And I see the total mileage

  Scenario: => Viewing a wheel's details
    Given I start the app
    When I select the Sherman
    Then the details view shows the details for that wheel

  @Ignore
  Scenario: Viewing deleted wheels
    Given I start the app
    When I select the deleted wheels
    Then I see my wheels and their mileage:
      | Name          | Mileage |
      | Sherman       | 21222   |
      | x Nikola+     | 2927    |
      | x S18         | 2850    |
      | x Sherman Max | 2000    |
      | 14S           | 694     |
      | <New>         |         |

  Scenario: => Adding a wheel
    Given I start the app
    When I add a new wheel
    Then I can enter the details for that wheel
