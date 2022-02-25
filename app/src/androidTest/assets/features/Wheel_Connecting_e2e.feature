@End2End
Feature: Wheel Connecting - End-2-End

  Background:
    And I start the app

  Scenario: Connecting to the 14S to update its mileage
    Given this wheel:
      | Name         | Voltage Min | Voltage Max | Mileage |
      | KingSong 14S | 48.0V       | 67.2V       | 694     |
    And I select the KingSong 14S
    When I connect to the KS-14SMD2107
    Then the mileage is updated to 759
    And the wheel's Bluetooth name is updated
