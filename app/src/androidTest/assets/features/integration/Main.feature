@Integration
Feature: Main Screen

  Background:
    Given these wheels:
      | Name            | Voltage Min | Voltage Max | Mileage |
      | Veteran Sherman | 75.6V       | 100.8V      | 17622   |
      | Veteran Abrams  | 74.5V       | 100.8V      | 0       |
      | KingSong S20    | 90.0V       | 126.0V      | 0       |
      | KingSong 14S    | 48.0V       | 67.2V       | 694     |
      | Gotway Nikola+  | 78.0V       | 100.8V      | 2927    |
      | KingSong S18    | 60.0V       | 84.0V       | 2850    |

  Scenario: On entering, we see a list of registered wheels and their mileage
    When I start the app
    Then I see my wheels and their mileage:
      | Name            | Mileage |
      | Veteran Sherman | 17622   |
      | Gotway Nikola+  | 2927    |
      | KingSong S18    | 2850    |
      | KingSong 14S    | 694     |
      | KingSong S20    | 0       |
      | Veteran Abrams  | 0       |
    And I see the total mileage

  Scenario: Navigating to the wheel details
    Given I start the app
    When I select the KingSong S20
    Then the details view shows the details for that wheel
