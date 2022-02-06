@Integration
Feature: Wheel Edit

  Background:
    Given this wheel:
      | Name            | Voltage Min | Voltage Max | Mileage |
      | Veteran Sherman | 75.6V       | 100.8V      | 17622   |
    And I start the app

  Scenario: Editing a wheel in full
    Given I select the Veteran Sherman
    When I edit the wheel
    And I set these new values:
      | Name        | Veteran Sherman Max |
      | Mileage     | 150                 |
      | Voltage Min | 74.5                |
      | Voltage Max | 100.9               |
    Then the wheel was updated
    And it shows the updated name and mileage on the main view

# FIXME-0 Editing a wheel - cannot save with blank fields
  Scenario: Cannot edit a wheel with no changes
    Given I select the Veteran Sherman
    When I edit the wheel
    And I don't set any new values
    Then the wheel cannot be saved

  Scenario: Cannot save a wheel with a blank name
    Given I select the Veteran Sherman
    When I edit the wheel
    And I set a blank name
    Then the wheel cannot be saved

# FIXME-1 Editing a wheel - with cancel