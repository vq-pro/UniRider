@Integration
Feature: Unirider

  Scenario: Selecting a wheel
    Given I start the app
    When I scan for devices
    Then I see a list of devices
