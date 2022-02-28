Feature: Wheel Editing & Adding

  Background:
    Given these wheels:
      | Name            | Mileage | Voltage Min | Voltage Max |
      | Veteran Sherman | 17622   | 75.6V       | 100.8V      |
      | KingSong S18    | 2850    | 60.0V       | 84.0V       |
    And I start the app

  Scenario: Adding a wheel in full
    When I add a new wheel
    And I set these new values:
      | Name        | Veteran Sherman Max |
      | Mileage     | 150                 |
      | Voltage Min | 74.5                |
      | Voltage Max | 100.9               |
    Then the wheel was added
    And it shows the updated name and mileage on the main view
    And the wheel's Bluetooth name is undefined

  Scenario: Editing a wheel in full
    Given I select the Veteran Sherman
    When I edit the wheel
    And I set these new values:
      | Name        | Veteran Sherman Max |
      | Mileage     | 150                 |
      | Voltage Min | 74.5                |
      | Voltage Max | 100.9               |
    Then the wheel was updated
    And we go back to the main view
    And it shows the updated name and mileage on the main view

  Scenario Outline: Wheel <can or cannot> be saved if we <do something>
    Given I select the Veteran Sherman
    When I edit the wheel
    And I <do something>
    Then the wheel <can or cannot> be saved
    Examples:
      | can or cannot | do something                |
      | can           | change the name             |
      | can           | change the mileage          |
      | can           | blank the mileage           |
      | can           | change the maximum voltage  |
      | can           | change the minimum voltage  |
      | cannot        | blank the name              |
      | cannot        | blank the maximum voltage   |
      | cannot        | blank the minimum voltage   |
      | cannot        | change nothing              |
      | cannot        | reuse the name KingSong S18 |

    #  FIXME-1 Previous mileage, before firmware update
