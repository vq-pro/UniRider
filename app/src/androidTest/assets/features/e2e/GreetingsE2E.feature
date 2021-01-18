@E2E
Feature: Greetings

  Scenario: Complete greeting
    Given we see "Enter your name"
    When we enter the name "Roger"
    Then we see "Hello Roger!"
