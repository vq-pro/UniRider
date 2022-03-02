@End2End
Feature: Wheel Connecting - End-2-End

  We can connect to a wheel to get its live mileage and voltage.

  Background:
    Given these wheels:
      | Name            | Mileage | Voltage Min | Voltage Max |
      | Gotway Nikola+  | 2927    | 78.0V       | 100.8V      |
      | KingSong 14S    | 694     | 48.0V       | 67.2V       |
      | KingSong S18    | 2850    | 60.0V       | 84.0V       |
      | Veteran Sherman | 10000   | 75.6V       | 100.8V      |
    And I start the app

  Scenario Outline: Connecting to a wheel for the first time - <wheel>
    And I select the <wheel>
    When I connect to the <bt name>
    Then the mileage is updated to <mileage>
    And the wheel's Bluetooth name is updated
    Examples:
      | wheel           | bt name      | mileage |
      | KingSong 14S    | KS-14SMD2107 | 759     |
#      | KingSong S18    | KS-S18-1410  | 2892    |
      | Veteran Sherman | LK1149       | 14590   |

  Scenario Outline: Connecting to a previously connected wheel - <wheel>
    Given these wheels are connected:
      | Name            | Bt Name      | Bt Address        |
      | KingSong 14S    | KS-14SMD2107 | FC:69:47:68:79:8A |
      | KingSong S18    | KS-S18-1410  | F8:33:31:A9:5F:16 |
      | Veteran Sherman | LK1149       | 88:25:83:F1:C9:8B |
    And the Veteran Sherman has a previous mileage of 3600
    And I select the <wheel>
    When I reconnect to the wheel
    Then the mileage is updated to <mileage>
    Examples:
      | wheel           | mileage |
      | KingSong 14S    | 759     |
#      | KingSong S18    | 2892    |
      | Veteran Sherman | 18190   |

#  FIXME-2 Inmotion
