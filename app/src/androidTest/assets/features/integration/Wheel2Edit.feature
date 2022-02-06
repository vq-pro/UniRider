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

  Scenario Outline: Wheel <can or cannot> be saved if we <do something>
    Given I select the Veteran Sherman
    When I edit the wheel
    And I <do something>
    Then the wheel <can or cannot> be saved
    Examples:
      | can or cannot | do something               |
      | can           | change the name            |
      | can           | change the mileage         |
      | can           | blank the mileage          |
      | can           | change the maximum voltage |
      | can           | change the minimum voltage |
      | cannot        | change nothing             |
      | cannot        | blank the name             |
      | cannot        | blank the maximum voltage  |
      | cannot        | blank the minimum voltage  |
