Feature: Main Screen

  Background:
    Given these wheels:
      | Name            | Mileage | Voltage Min | Voltage Max |
      | Veteran Sherman | 17622   | 75.6V       | 100.8V      |
      | Veteran Abrams  | 0       | 74.5V       | 100.8V      |
      | KingSong S20    | 0       | 90.0V       | 126.0V      |
      | KingSong 14S    | 694     | 48.0V       | 67.2V       |
      | Gotway Nikola+  | 2927    | 78.0V       | 100.8V      |
      | KingSong S18    | 2850    | 60.0V       | 84.0V       |

  Scenario: On entering, we see a list of registered wheels and their mileage
    Given the Veteran Sherman has a previous mileage of 3600
    When I start the app
    Then I see my wheels and their mileage:
      | Name            | Mileage |
      | Veteran Sherman | 21222   |
      | Gotway Nikola+  | 2927    |
      | KingSong S18    | 2850    |
      | KingSong 14S    | 694     |
      | KingSong S20    | 0       |
      | Veteran Abrams  | 0       |
      | <New>           |         |
    And I see the total mileage

  Scenario: => Viewing a wheel's details
    Given I start the app
    When I select the KingSong S20
    Then the details view shows the details for that wheel

  Scenario: => Adding a wheel
    Given I start the app
    When I add a new wheel
    Then I can enter the details for that wheel
