@Integration
Feature: Unirider

  Background:
    Given I have these devices:
      | KingSong 14D  |
      | Inmotion V10F |
    And I start the app

  Scenario: Scanning for wheels
    When I scan for devices
    Then I see my devices

  Scenario: Scanning twice
    When I scan for devices
    And I scan again
    Then I see my devices
