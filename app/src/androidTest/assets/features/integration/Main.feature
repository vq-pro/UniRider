@Integration
Feature: Main Screen

  Background:
    Given these wheels:
      | Name            | Voltage Max | Voltage Min | Distance |
      | Veteran Sherman | 100.8V      | 75.6V       | 17622    |
      | Veteran Abrams  | 74.5V       | 100.8V      | 0        |
      | KingSong S20    | 126.0V      | 90.0V       | 0        |
      | KingSong 14S    | 67.2V       | 48.0V       | 694      |
      | Gotway Nikola+  | 100.8V      | 78.0V       | 2927     |
      | KingSong S18    | 84.0V       | 60.0V       | 2850     |

  Scenario: On entering, we see a list of registered wheels and their distance
    When I start the app
    Then I see my wheels and their distance:
      | Name            | Distance |
      | Veteran Sherman | 17622    |
      | Gotway Nikola+  | 2927     |
      | KingSong S18    | 2850     |
      | KingSong 14S    | 694      |
      | KingSong S20    | 0        |
      | Veteran Abrams  | 0        |
    And I see the total distance

  Scenario: Going into the wheel details
    Given I start the app
    When I select the KingSong S20
    Then the details view shows the correct name and a distance of that wheel
