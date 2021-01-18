@Integration
Feature: Greetings

  Scenario: New greeting
    Given we see "Enter your name"
    And we've never greeted "Roger" before
    When we enter the name "Roger"
    Then we see "Hello Roger!"

  Scenario: Repeated greeting
    Given we've already greeted "Linda" 3 times
    When we enter the name "Linda"
    Then we see "Hello Linda! (4)"
