@End2End
Feature: Wheel Connecting - End-2-End

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Max | Full Charge | Charge Amperage | Charge Rate | Distance Offset | Sold |
      | Sherman L   | 20000   | 4000 | 104.4V      | 151.2V      | 150.1V      | 20A             | 21V/h       | 1.0667          | No   |
      | Sherman     | 20106   | 3200 | 75.6V       | 100.8V      | 99.5V       | 10A             | 7.5V/h      | 1               | No   |
      | Lynx        | 2000    | 2700 | 104.4V      | 151.2V      | 150.1V      | 15A             | 18.5V/h     | 1.0667          | No   |
      | Aero        | 0       | 1100 | 87V         | 126V        | 125.1V      | 3A              | 8V/h        | 1.0167          | No   |
      | Sherman-S   | 23427   | 3600 | 69.6V       | 100.8V      | 99.5V       | 10A             | 8V/h        | 1               | Yes  |
      | Sherman Max | 14530   | 3600 | 75.6V       | 100.8V      | 99.5V       | 10A             | 8V/h        | 1               | Yes  |
      | Abrams      | 3675    | 2700 | 74.5V       | 100.8V      | 99.5V       | 10A             | 14V/h       | 1               | Yes  |
      | S18         | 3143    | 1110 | 60V         | 84V         | 81.5V       | 2.5A            | 4V/h        | 1               | Yes  |
      | Nikola+     | 2927    | 1800 | 78V         | 100.8V      | 99.5V       | 3A              | 6V/h        | 1               | Yes  |
      | S18-SE      | 2836    | 900  | 60V         | 84V         | 81.5V       | 2.5A            | 4V/h        | 1               | Yes  |
      | 14S         | 2421    | 840  | 48V         | 67.2V       | 65.5V       | 2.5A            | 4V/h        | 1               | Yes  |
      | V10F        | 1600    | 960  | 65V         | 84V         | 82V         | 2A              | 2V/h        | 1               | Yes  |
      | V9          | 794     | 750  | 60V         | 84V         | 83.1V       | 4A              | 12V/h       | 1               | Yes  |
    And the Sherman has a previous mileage of 3600 km
    And the updated mileage for some of these wheels should be:
      | Name | Updated mileage |
      | Aero | 347             |
    And I start the app

  Scenario: Connecting to a wheel for the first time
    Given I select the Aero
    When I connect to the NF3079
    Then the mileage is updated to its up-to-date value
    And the wheel's Bluetooth name is updated

  Scenario: Connecting to a wheel for the first time - ERROR - Wheel should be detectable repeatedly
    Given I select the Aero
    And I do a scan and see the NF3079 (88:25:83:F5:DB:28) but go back without connecting
    When I connect to the NF3079
    Then the wheel's Bluetooth name is updated

  Scenario: Reconnecting to a previously connected wheel
    Given these wheels are connected:
      | Name      | Bt Name      | Bt Address        |
      | 14S       | KS-14SMD2107 | FC:69:47:68:79:8A |
      | S18-SE    | KSS18-9135   | 48:70:1E:4D:E7:3F |
      | Sherman   | LK1149       | 88:25:83:F1:C9:8B |
      | Sherman-S | LK6474       | 88:25:83:F4:E4:89 |
      | Lynx      | LK9622       | 88:25:83:F5:36:17 |
      | Sherman L | LK13447      | 88:25:83:F5:75:80 |
      | Aero      | NF3079       | 88:25:83:F5:DB:28 |
    And I select the Aero
    When I reconnect to the wheel
    Then the mileage is updated to its up-to-date value

  Scenario: Reconnecting to a previously connected wheel - ERROR - Connection following failure to connect
    Given these wheels are connected:
      | Name      | Bt Name      | Bt Address        |
      | 14S       | KS-14SMD2107 | FC:69:47:68:79:8A |
      | S18-SE    | KSS18-9135   | 48:70:1E:4D:E7:3F |
      | Sherman   | LK1149       | 88:25:83:F1:C9:8B |
      | Sherman-S | LK6474       | 88:25:83:F4:E4:89 |
      | Lynx      | LK9622       | 88:25:83:F5:36:17 |
      | Sherman L | LK13447      | 88:25:83:F5:75:80 |
      | Aero      | NF3079       | 88:25:83:F5:DB:28 |
    And I select the Lynx
    And I reconnect to the wheel
    And I cancel the scan and go back
    And I select the Aero
    When I reconnect to the wheel
    Then the mileage is updated to its up-to-date value
