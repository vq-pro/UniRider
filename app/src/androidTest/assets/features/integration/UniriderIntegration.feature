@Integration
Feature: Unirider

  Scenario: Selecting a wheel
    Given I start the app
    And these devices:
      | KingSong 14D  |
      | Inmotion V10F |
    When I scan for devices
    Then I see these devices:
      | KingSong 14D  |
      | Inmotion V10F |

  Scenario: Scanning twice
    Given I start the app
    And this device: "KingSong 14D"
    When I scan for devices
    And I scan again
    Then I see these devices:
      | KingSong 14D  |
