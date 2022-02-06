@Integration
Feature: Wheel Edit

  Background:
    Given this wheel:
      | Name            | Voltage Min | Voltage Max | Mileage |
      | Veteran Sherman | 75.6V       | 100.8V      | 17622   |

# FIXME-1 Editing a wheel and cancelling
  Scenario: Editing a wheel in full
    Given I start the app
    And I select the Veteran Sherman
    When I edit the wheel
    And I set these new values:
      | Name        | Veteran Sherman Max |
      | Mileage     | 150                 |
      | Voltage Min | 74.5                |
      | Voltage Max | 100.9               |
    Then the wheel was updated
    And it shows the updated name and mileage on the main view
