Feature: Wheel Editing & Adding

  # FIXME-1 Edit Charge Rate as value

  Background:
    Given these wheels:
      | Name    | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Nikola+ | 2927    | 1800 | 78V         | 82V             | 100.8V      |
      | Sherman | 17622   | 3200 | 75.6V       | 80V             | 100.8V      |
      | S18     | 2850    | 1110 | 60V         | 68V             | 84V         |
    And I start the app

  Scenario: Adding a wheel in full
    When I add a new wheel
    And I set these new values:
      | Name             | Sherman Max |
      | Previous Mileage | 0           |
      | Mileage          | 150         |
      | Wh               | 3600        |
      | Voltage Max      | 100.8       |
      | Voltage Reserve  | 80.0        |
      | Voltage Min      | 75.6        |
    Then the wheel was added
    And it shows the updated name and a mileage of 150 on the main view
    And the wheel's Bluetooth name is undefined

  Scenario: Editing a wheel in full
    Given I select the Sherman
    When I edit the wheel
    And I set these new values:
      | Name             | Sherman Max |
      | Previous Mileage | 50          |
      | Mileage          | 150         |
      | Wh               | 3600        |
      | Voltage Max      | 100.9       |
      | Voltage Reserve  | 80.5        |
      | Voltage Min      | 74.5        |
    Then the wheel was updated
    And I go back to the main view
    And it shows the updated name and a mileage of 200 on the main view

  Scenario Outline: Wheel <can or cannot> be saved if we <do something>
    Given the Sherman has a previous mileage of 3600 km
    And I select the Sherman
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
      | can           | change the wh               |
      | cannot        | blank the name              |
      | cannot        | blank the wh                |
      | cannot        | blank the maximum voltage   |
      | cannot        | blank the minimum voltage   |
      | cannot        | change nothing              |
      | cannot        | reuse the name S18          |

  Scenario: => Deleting the wheel
    Given I select the Nikola+
    And I edit the wheel
    When I delete the wheel
    And I confirm the deletion
    Then I am back at the main screen and the wheel is gone
