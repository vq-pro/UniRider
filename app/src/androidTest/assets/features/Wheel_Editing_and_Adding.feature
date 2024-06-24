Feature: Wheel Editing & Adding

  Background:
    Given these wheels:
      | Name    | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Sold |
      | Nikola+ | 2927    | 1800 | 78V         | 82V             | 100.8V      | 6V/h        | 99.4V       | Yes  |
      | Sherman | 17622   | 3200 | 75.6V       | 80V             | 100.8V      | 8V/h        | 99.4V       | No   |
      | S18     | 2850    | 1110 | 60V         | 68V             | 84V         | 4V/h        | 81.4V       | No   |
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
      | Charge Rate      | 8           |
      | Full Charge      | 99.5        |
      | Sold             | No          |
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
      | Charge Rate      | 2           |
      | Full Charge      | 99.5        |
      | Sold             | No          |
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
      | can or cannot | do something                                    |
      | can           | change the charge rate                          |
      | can           | change the name                                 |
      | can           | change the mileage                              |
      | can           | blank the mileage                               |
      | can           | change the full voltage                         |
      | can           | change the maximum voltage                      |
      | can           | change the minimum voltage                      |
      | can           | change the previous mileage                     |
      | can           | change the reserve voltage                      |
      | can           | blank the full voltage                          |
      | can           | blank the previous mileage                      |
      | can           | blank the reserve voltage                       |
      | can           | change the wh                                   |
      | can           | mark the wheel as sold                          |
      | cannot        | blank the charge rate                           |
      | cannot        | blank the name                                  |
      | cannot        | blank the wh                                    |
      | cannot        | blank the maximum voltage                       |
      | cannot        | blank the minimum voltage                       |
      | cannot        | change nothing                                  |
      | cannot        | reuse the name S18                              |
      | cannot        | set the full voltage lower than the minimum     |
      | cannot        | set the full voltage higher than the maximum    |
      | cannot        | set the maximum voltage lower than the minimum  |
      | cannot        | set the reserve voltage higher than the maximum |
      | cannot        | set the reserve voltage lower than the minimum  |

  Scenario: => Deleting the wheel
    Given I select the S18
    And I edit the wheel
    When I delete the wheel
    And I confirm the deletion
    Then I am back at the main screen
    And the wheel is gone

  Scenario: => Selling a wheel
    Given I select the S18
    And I edit the wheel
    When I mark the wheel as sold
    And I save and view the wheel
    Then the wheel appears as sold

  Scenario: => Unselling a wheel
    Given I select the Nikola+
    And I edit the wheel
    When I mark the wheel as unsold
    And I save and go back to the main view
    Then the wheel is shown as unsold
