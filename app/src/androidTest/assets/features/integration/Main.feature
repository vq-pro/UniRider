@Integration
Feature: Main Screen

  Background:
    Given these wheels:
      | Name            | Voltage Max | Voltage Min | Distance |
      | Veteran Sherman | 100.8V      | 75.6V       | 123      |
      | KingSong S20    | 126.0V      | 90.0V       | 123      |
      | KingSong 14S    | 67.2V       | 48.0V       | 123      |
      | Gotway Nikola+  | 100.8V      | 78.0V       | 123      |
      | KingSong S18    | 84.0V       | 60.0V       | 123      |

  Scenario: On entering, we see a list of registered wheels and their distance
    When I start the app
    Then I see my wheels and their distance:
      | Name            | Distance |
      | Gotway Nikola+  | 123      |
      | KingSong 14S    | 123      |
      | KingSong S18    | 123      |
      | KingSong S20    | 123      |
      | Veteran Sherman | 123      |

  Scenario: Going into calculator
    Given I start the app
    When I select the KingSong S20
    Then I go into the detailed view for that wheel
