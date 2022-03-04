@End2End
Feature: Wheel Connecting - End-2-End

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given these wheels:
      | Name            | Mileage | Voltage Min | Voltage Max |
      | Gotway Nikola+  | 2927    | 78V         | 100.8V      |
      | KingSong 14S    | 694     | 48V         | 67.2V       |
      | KingSong S18    | 2916    | 60V         | 84V         |
      | Inmotion V8F    | 0       | 65V         | 84V         |
      | Inmotion V10F   | 1600    | 65V         | 84V         |
      | Veteran Sherman | 10000   | 75.6V       | 100.8V      |
    And the Veteran Sherman has a previous mileage of 3600
    And I start the app

  Scenario Outline: Connecting to a wheel for the first time - <wheel>
    Given I select the <wheel>
    When I connect to the <bt name>
    Then the mileage is updated to <mileage>
    And the wheel's Bluetooth name is updated
    Examples:
      | wheel           | bt name      | mileage |
#      | KingSong 14S    | KS-14SMD2107 | 765     |
      | Veteran Sherman | LK1149       | 18190   |

  Scenario Outline: Connecting to a previously connected wheel - <wheel>
    Given these wheels are connected:
      | Name            | Bt Name      | Bt Address        |
      | Inmotion V8F    | V8F-F8D10065 | 00:35:FF:1F:61:EA |
      | KingSong 14S    | KS-14SMD2107 | FC:69:47:68:79:8A |
      | KingSong S18    | KS-S18-1410  | F8:33:31:A9:5F:16 |
      | Veteran Sherman | LK1149       | 88:25:83:F1:C9:8B |
    And I select the <wheel>
    When I reconnect to the wheel
    Then the mileage is updated to <mileage>
    Examples:
      | wheel           | mileage |
#      | KingSong 14S    | 765     |
      | Veteran Sherman | 18190   |
#      | Inmotion V8F    | 0       |

  Scenario: Connection following failure to connect - long since we need to wait for the natural timeout for the first connection
    Given these wheels are connected:
      | Name            | Bt Name      | Bt Address        |
      | Gotway Nikola+  | GOTWAY       | 00:00:00:00:00:00 |
      | KingSong 14S    | KS-14SMD2107 | FC:69:47:68:79:8A |
      | KingSong S18    | KS-S18-1410  | F8:33:31:A9:5F:16 |
      | Veteran Sherman | LK1149       | 88:25:83:F1:C9:8B |
    And I select the Gotway Nikola+
    And I reconnect to the wheel
    And I cancel the scan and go back
    And I select the Veteran Sherman
    When I reconnect to the wheel
    Then the mileage is updated to 18190

#  FIXME-2 Inmotion
