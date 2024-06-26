@End2End
Feature: Wheel Connecting - End-2-End

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given these wheels:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Reserve | Voltage Max | Charge Rate | Full Charge | Charger Offset | Sold |
      | Nikola+     | 2927    | 1800 | 78V         | 82V             | 100.8V      | 6V/h        | 99.5V       | 1.5V           | Yes  |
      | 14S         | 1635    | 840  | 48V         | 55V             | 67.2V       | 4V/h        | 65.5V       | 1.5V           | No   |
      | S18         | 3143    | 1110 | 60V         | 68V             | 84V         | 4V/h        | 81.5V       | 1.5V           | Yes  |
      | V10F        | 1600    | 960  | 65V         | 70V             | 84V         | 2V/h        | 82V         | 1.5V           | Yes  |
      | Sherman     | 19000   | 3200 | 75.6V       | 80V             | 100.8V      | 7.5V/h      | 99.5V       | 1.5V           | No   |
      | Sherman Max | 14530   | 3600 | 75.6V       | 80V             | 100.8V      | 8V/h        | 99.5V       | 1.5V           | Yes  |
      | S18-SE      | 1023    | 900  | 60V         | 66V             | 84V         | 4V/h        | 81.5V       | 1.5V           | No   |
      | Sherman-S   | 3269    | 3600 | 69.6V       | 75V             | 100.8V      | 9.5V/h      | 99.5V       | 1.5V           | No   |
      | Abrams      | 3675    | 2700 | 74.5V       | 80V             | 100.8V      | 14V/h       | 99.5V       | 1.5V           | Yes  |
    And the Sherman has a previous mileage of 3600 km
    And the updated mileage for some of these wheels should be:
      | Name      | Updated mileage |
      | Sherman-S | 21039           |
      | Sherman   | 19937           |
    And I start the app

  Scenario Outline: Connecting to a wheel for the first time - <wheel>
    Given I select the <wheel>
    When I connect to the <bt name>
    Then the mileage is updated to its up-to-date value
    And the wheel's Bluetooth name is updated
    Examples:
      | wheel     | bt name |
      | Sherman-S | LK6474  |
      | Sherman   | LK1149  |

  Scenario: Connecting to a wheel for the first time - ERROR - Wheel should be detectable repeatedly
    Given I select the Sherman
    And I do a scan and see the LK1149 (88:25:83:F1:C9:8B) but go back without connecting
    When I connect to the LK1149
    Then the wheel's Bluetooth name is updated

  Scenario Outline: Connecting to a previously connected wheel [<wheel>]
    Given these wheels are connected:
      | Name      | Bt Name      | Bt Address        |
      | 14S       | KS-14SMD2107 | FC:69:47:68:79:8A |
      | S18-SE    | KSS18-9135   | 48:70:1E:4D:E7:3F |
      | Sherman   | LK1149       | 88:25:83:F1:C9:8B |
      | Sherman-S | LK6474       | 88:25:83:F4:E4:89 |
    And I select the <wheel>
    When I reconnect to the wheel
    Then the mileage is updated to its up-to-date value
    Examples:
      | wheel     |
      | Sherman-S |
      | Sherman   |

  Scenario: Connecting to a previously connected wheel - ERROR - Connection following failure to connect
    Given these wheels are connected:
      | Name      | Bt Name      | Bt Address        |
      | 14S       | KS-14SMD2107 | FC:69:47:68:79:8A |
      | S18-SE    | KSS18-9135   | 48:70:1E:4D:E7:3F |
      | Sherman   | LK1149       | 88:25:83:F1:C9:8B |
      | Sherman-S | LK6474       | 88:25:83:F4:E4:89 |
    And I select the 14S
    And I reconnect to the wheel
    And I cancel the scan and go back
    And I select the Sherman-S
    When I reconnect to the wheel
    Then the mileage is updated to its up-to-date value
