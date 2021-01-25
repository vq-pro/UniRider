#@E2E
# FIXME 2 Cleanup
Feature: Greetings

  Scenario: Complete greeting
    Given we see "Enter your name"
    When we enter the name "Roger"
    Then we see "Hello Roger!"
