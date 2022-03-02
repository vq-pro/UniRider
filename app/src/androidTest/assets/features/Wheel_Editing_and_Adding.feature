Feature: Wheel Editing & Adding

  Background:
    Given these wheels:
      | Name            | Mileage | Voltage Min | Voltage Max |
      | Veteran Sherman | 17622   | 75.6V       | 100.8V      |
      | KingSong S18    | 2850    | 60V         | 84V         |
    And I start the app

  Scenario: Adding a wheel in full
    When I add a new wheel
    And I set these new values:
      | Name             | Veteran Sherman Max |
      | Previous Mileage | 0                   |
      | Mileage          | 150                 |
      | Voltage Min      | 74.5                |
      | Voltage Max      | 100.9               |
    Then the wheel was added
    And it shows the updated name and a mileage of 150 on the main view
    And the wheel's Bluetooth name is undefined

  Scenario: Editing a wheel in full
    Given I select the Veteran Sherman
    When I edit the wheel
    And I set these new values:
      | Name             | Veteran Sherman Max |
      | Previous Mileage | 50                  |
      | Mileage          | 150                 |
      | Voltage Min      | 74.5                |
      | Voltage Max      | 100.9               |
    Then the wheel was updated
    And I go back to the main view
    And it shows the updated name and a mileage of 200 on the main view

  Scenario Outline: Wheel <can or cannot> be saved if we <do something>
    Given the Veteran Sherman has a previous mileage of 3600
    And I select the Veteran Sherman
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
      | can           | change the previous mileage |
      | can           | blank the previous mileage  |
      | cannot        | blank the name              |
      | cannot        | blank the maximum voltage   |
      | cannot        | blank the minimum voltage   |
      | cannot        | change nothing              |
      | cannot        | reuse the name KingSong S18 |
