@Integration
Feature: Unirider

  Scenario: Selecting a wheel
    Given I start the app
    And these test devices:
      | KingSong 14D  |
      | Inmotion V10F |
    When I scan for devices
    Then I see these devices:
      | KingSong 14D  |
      | Inmotion V10F |
