@Integration
Feature: Main Screen

  Background:
    Given these wheels:
      | Inmotion V10F   |
      | Veteran Sherman |
      | KingSong 14S    |
      | Gotway Nikola+  |
      | KingSong S18    |

  Scenario: On entering, we see a list of registered wheels and their distance
    When I start the app
    Then I see my wheels and their distance:
      | Gotway Nikola+  |
      | Inmotion V10F   |
      | KingSong 14S    |
      | KingSong S18    |
      | Veteran Sherman |

  Scenario: Going into calculator
    Given I start the app
    When I select the Veteran Sherman
    Then I go into the detailed view for that wheel
