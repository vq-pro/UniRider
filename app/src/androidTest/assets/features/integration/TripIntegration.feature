@Integration
Feature: Battery Percentage Calculator

  Background:
    Given I start the app

  @Ignore
  Scenario: Starting a ride
    When I choose the "Gotway Nikola+"
    Then the initial date and time is displayed
