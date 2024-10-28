Feature: Main Screen

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Distance Offset | Sold |
      | Sherman     | 17622   | 3200 | 75.6V       | 80V             | 100.8V      | 6V/h        | 99.5V       | 1.5V           | 1               | No   |
      | Sherman Max | 2000    | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        | 99.5V       | 1.5V           | 1               | Yes  |
      | 14S         | 694     | 840  | 48V         | 55V             | 67.2V       | 4V/h        | 65.5V       | 1.5V           | 1               | No   |
      | Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      | 6V/h        | 99.5V       | 1.5V           | 1               | Yes  |
      | S18         | 2850    | 1110 | 60V         | 68V             | 84V         | 4V/h        | 81.5V       | 1.5V           | 1               | Yes  |
    And the Sherman has a previous mileage of 3600 km

  Scenario: On entering, we see a list of registered wheels and their mileage
    When I start the app
    Then I see my wheels and their mileage:
      | Name    | Mileage  |
      | Sherman | 21222 km |
      | 14S     | 694 km   |
      | <Sold>  | 7777 km  |
      | <New>   |          |
    And I see the total mileage

  Scenario: => Viewing a wheel's details
    Given I start the app
    When I select the Sherman
    Then the details view shows the details for that wheel

  Scenario: Viewing sold wheels
    Given I start the app
    When I open up the sold wheels
    Then I see my wheels and their mileage:
      | Name          | Mileage  |
      | Sherman       | 21222 km |
      | 14S           | 694 km   |
      | <Sold>        |          |
      | - Nikola+     | 2927 km  |
      | - S18         | 2850 km  |
      | - Sherman Max | 2000 km  |
      | <New>         |          |

  Scenario: Hiding sold wheels
    Given I start the app
    And I open up the sold wheels
    When I collapse the sold wheels
    Then I see my wheels and their mileage:
      | Name    | Mileage  |
      | Sherman | 21222 km |
      | 14S     | 694 km   |
      | <Sold>  | 7777 km  |
      | <New>   |          |

  Scenario: => Adding a wheel
    Given I start the app
    When I add a new wheel
    Then I can enter the details for that wheel
