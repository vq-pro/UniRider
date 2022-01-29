@Integration
Feature: Main Screen

  Background:
    Given these wheels:
      | Name            | Voltage Max | Voltage Min |
      | Veteran Sherman | 100.8V      | 75.6V       |
      | KingSong S20    | 126.0V      | 90.0V       |
      | KingSong 14S    | 67.2V       | 48.0V       |
      | Gotway Nikola+  | 100.8V      | 78.0V       |
      | KingSong S18    | 84.0V       | 60.0V       |

  Scenario: On entering, we see a list of registered wheels and their distance
    When I start the app
    # FIXME 1 We need some distances here
    Then I see my wheels and their distance:
      | Gotway Nikola+  |
      | KingSong 14S    |
      | KingSong S18    |
      | KingSong S20    |
      | Veteran Sherman |

  Scenario: Going into calculator
    Given I start the app
    When I select the KingSong S20
    Then I go into the detailed view for that wheel
