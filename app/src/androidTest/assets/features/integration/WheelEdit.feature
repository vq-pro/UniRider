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

  Scenario Outline: Can change just the <field>
    Given I select the Veteran Sherman
    When I edit the wheel
    And I change the <field>
    Then the wheel can be saved
    Examples:
      | field           |
      | name            |
      | mileage         |
      | minimum voltage |
      | maximum voltage |

  Scenario Outline: Cannot edit a wheel with <an invalid change>
    Given I select the Veteran Sherman
    When I edit the wheel
    And I set <an invalid change>
    Then the wheel cannot be saved
    Examples:
      | an invalid change       |
      | no changed values       |
      | a blank name            |
      | a blank mileage         |
      | a blank minimum voltage |
      | a blank maximum voltage |

# FIXME-1 Editing a wheel - with cancel
