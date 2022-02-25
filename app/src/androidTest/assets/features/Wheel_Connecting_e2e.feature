@End2End
Feature: Wheel Connecting - End-2-End

  Scenario: Connecting to the 14S to update its mileage for the first time
    Given this disconnected wheel:
      | Name         | Mileage |
      | KingSong 14S | 694     |
    And I start the app
    And I select the KingSong 14S
    When I connect to the KS-14SMD2107
    Then the mileage is updated to 759
    And the wheel's Bluetooth name is updated

  Scenario: Connecting to the previously connected 14S to update its mileage
    Given this connected wheel:
      | Name         | Bt Name      | Bt Address        | Mileage |
      | KingSong 14S | KS-14SMD2107 | FC:69:47:68:79:8A | 694     |
    And I start the app
    And I select the KingSong 14S
    When I reconnect to the wheel
    Then the mileage is updated to 759
