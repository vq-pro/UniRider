Feature: Wheel Viewing - KingSong 14S

  Background:
    Given these wheels:
      | Name         | Voltage Min | Voltage Max | Mileage |
      | KingSong 14S | 48.0V       | 67.2V       | 650     |
    And I start the app

  @Prod
  Scenario: Connecting to the 14S to update its mileage
    Given I select the KingSong 14S
    When I connect to the KS-14SMD2107
    Then the mileage is updated to 695
    And the wheel's Bluetooth name is updated to KS-14SMD2107
