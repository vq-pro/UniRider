Feature: Wheel Viewing - KingSong 14S

  Background:
    Given this wheel:
      | Name         | Voltage Min | Voltage Max | Mileage |
      | KingSong 14S | 48.0V       | 67.2V       | 650     |
    And I start the app

  @End2End
  Scenario: Connecting to the 14S to update its mileage
    Given I select the KingSong 14S
    When I connect to the KS-14SMD2107
    Then the mileage is updated to 758.7
    And the wheel's Bluetooth name is updated
