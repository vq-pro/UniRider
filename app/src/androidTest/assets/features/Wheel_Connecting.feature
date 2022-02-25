Feature: Wheel Connecting

  Background:
    Given these wheels:
      | Name         | Voltage Min | Voltage Max | Mileage |
      | KingSong 14S | 48.0V       | 67.2V       | 694     |
    And I start the app
    And I select the KingSong 14S

  @IntegrationOnly
  Scenario: Connecting to a wheel to update its mileage
    Given I simulate a mileage of 705.615
    When I connect to the KS-14Sxx9999
    Then the mileage is updated to 706
    And the wheel's Bluetooth name is updated

  @End2End
  Scenario: Connecting to the 14S to update its mileage
    When I connect to the KS-14SMD2107
    Then the mileage is updated to 759
    And the wheel's Bluetooth name is updated
