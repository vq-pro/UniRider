#@Integration
@Ignore
Feature: Bluetooth Connection

  Background:
    Given I have these devices:
      | KingSong 14D  | KS:KS:KS:KS:KS:KS:KS |
      | Inmotion V10F | IM:IM:IM:IM:IM:IM:IM |
    And I start the app

  Scenario: Scanning for wheels
    When I scan for devices
    Then I see my devices

  Scenario: Scanning twice
    When I scan for devices
    And I scan again
    Then I see my devices

#  Scenario: Selecting a wheel
#    When I scan for devices
#    And I select the "KingSong 14D"
#    Then the scanning has stopped
#    And I see the screen for this wheel
#    And I see the type of wheel it is
