@End2End
Feature: Wheel Connecting - End-2-End

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max |
      | Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      |
      | 14S         | 950     | 840  | 48V         | 55V             | 67.2V       |
      | S18         | 3143    | 1110 | 60V         | 68V             | 84V         |
      | V10F        | 1600    | 960  | 65V         | 70V             | 84V         |
      | Sherman     | 10000   | 3200 | 75.6V       | 80V             | 100.8V      |
      | Sherman Max | 2434    | 3600 | 75.6V       | 80V             | 100.8V      |
      | S18-SE      | 269     | 900  | 60V         | 66V             | 84V         |
      | Sherman-S   | 0       | 3600 | 75.6V       | 80V             | 100.8V      |
    And the Sherman has a previous mileage of 3600 km
    And I start the app

  Scenario Outline: Connecting to a wheel for the first time - <wheel>
    Given I select the <wheel>
    When I connect to the <bt name>
    Then the mileage is updated to <mileage> km
    And the wheel's Bluetooth name is updated
    Examples:
      | wheel   | bt name      | mileage |
      | 14S     | KS-14SMD2107 | 1301    |
#      | S18-SE  | KSS18-9135   | 718     |
      | Sherman | LK1149       | 22696   |

  Scenario: Connecting to a wheel for the first time - ERROR - Wheel should be detectable repeatedly
    Given I select the Sherman
    And I do a scan and see the LK1149 but go back without connecting
    When I connect to the LK1149
    Then the wheel's Bluetooth name is updated

  Scenario Outline: Connecting to a previously connected wheel [<wheel>]
    Given these wheels are connected:
      | Name        | Bt Name      | Bt Address        |
      | 14S         | KS-14SMD2107 | FC:69:47:68:79:8A |
      | S18-SE      | KSS18-9135   | 48:70:1E:4D:E7:3F |
      | Sherman     | LK1149       | 88:25:83:F1:C9:8B |
      | Sherman Max | LK4142       | 88:25:83:F3:61:20 |
    And I select the <wheel>
    When I reconnect to the wheel
    Then the mileage is updated to <mileage> km
    Examples:
      | wheel   | mileage |
      | 14S     | 1301    |
#      | S18-SE  | 718     |
      | Sherman | 22696   |

  Scenario: Connecting to a previously connected wheel - ERROR - Connection following failure to connect
    Given these wheels are connected:
      | Name        | Bt Name      | Bt Address        |
      | Nikola+     | GOTWAY       | 00:00:00:00:00:00 |
      | 14S         | KS-14SMD2107 | FC:69:47:68:79:8A |
      | S18-SE      | KSS18-9135   | 48:70:1E:4D:E7:3F |
      | Sherman     | LK1149       | 88:25:83:F1:C9:8B |
      | Sherman Max | LK4142       | 88:25:83:F3:61:20 |
    And I select the Nikola+
    And I reconnect to the wheel
    And I cancel the scan and go back
    And I select the Sherman
    When I reconnect to the wheel
    Then the mileage is updated to 22696 km
