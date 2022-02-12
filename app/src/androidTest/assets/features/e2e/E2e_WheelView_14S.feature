@E2E
Feature: Wheel Viewing - KingSong 14S

  Background:
    Given these wheels:
      | Name         | Voltage Min | Voltage Max | Mileage |
      | KingSong 14S | 48.0V       | 67.2V       | 650     |
    And I start the app

  Scenario: Connecting to the wheel to update its mileage
    Given I select the KingSong 14S
    When I connect to the KS-14SMD2107
    Then the mileage is updated to 655
#  FIXME-0 Implement this
#    And the wheel's Bluetooth name is updated to KS-14SMD2107
