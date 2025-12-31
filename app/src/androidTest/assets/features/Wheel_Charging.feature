Feature: Wheel Charging

  Background:
    Given this wheel:
      | Name        | Mileage | Wh   | Voltage Min | Voltage Max | Charge Rate | Full Charge | Charger Offset | Distance Offset | Sold |
      | Sherman Max | 2000    | 3600 | 75.6V       | 100.8V      | 8V/h        | 99.5V       | 1.5V           | 1.1             | No   |
    And this wheel is connected:
      | Name        | Bt Name | Bt Address        |
      | Sherman Max | LK1234  | C0:C1:C2:C3:C4:C5 |
    And the current time is 11:45
    And I start the app
    And I select the Sherman Max
    And I set the actual voltage to 86.4V
    And I set the distance to 50 km
    And it displays these estimates:
      | remaining | total range |
      | 14.5      | 64.5        |
    And I charge the wheel

#  FIXME-1 Pour charger, il faut absolument capturer le voltage initial.
#  Donc quand on clique sur le bouton Charger:
#    * Se connecter pour capturer le voltage initial, AVANT de brancher le chargeur
#    * Afficher un popup pour indiquer quand le chargeur est brancher
#    * Brancher le charger, et appuyer sur le bouton
#    * Entrer dans l'écran de charge, recapturer le voltage actuel et faire le calcul de l'offset chargeur
#    * Afficher le voltage requis en tenant compte de ce décallage
#    * Pour une charge partielle par km, ajouter le décallage au voltage requis

  Scenario: Changing the voltage
    Given I request to charge for 40 km
    And it displays an actual voltage of 87.9V
    And it displays these charging estimates:
      | required voltage | diff   | time        |
      | 95.0V            | (+7.1) | 12:38 (53m) |
    When I change the actual voltage to 90V
    Then it displays these charging estimates:
      | required voltage | diff   | time        |
      | 95.0V            | (+5.0) | 12:23 (38m) |

  Scenario Outline: Charging a wheel by distance [<distance>]
    When I request to charge for <distance>
    Then it displays an actual voltage of 87.9V
    And the full charge indicator is <fc_indicator>
    And it displays these charging estimates:
      | required voltage | diff   | time   |
      | <voltage>        | <diff> | <time> |
    Examples:
      | distance | voltage | diff    | time          | fc_indicator |
      | 0 km     | 87.9V   | Go!     |               | off          |
      | 5 km     | 84.0V   | Go!     |               | off          |
      | 10 km    | 86.1V   | Go!     |               | off          |
      | 20 km    | 89.9V   | (+2.0)  | 12:00 (15m)   | off          |
      | 40 km    | 95.0V   | (+7.1)  | 12:38 (53m)   | off          |
      | 50 km    | 96.3V   | (+8.4)  | 12:48 (1h3m)  | off          |
      | 60 km    | 97.7V   | (+9.8)  | 12:59 (1h14m) | off          |
      | 200 km   | 99.5V   | (+11.6) | 13:12 (1h27m) | off          |
      | full     | 99.5V   | (+11.6) | 13:12 (1h27m) | on           |

  Scenario Outline: Charging a wheel by voltage [<voltage>]
    When I request to charge to <voltage>
    Then it displays an actual voltage of 87.9V
    And the full charge indicator is off
    And it displays these charging estimates:
      | required voltage | diff   | time   |
      | <voltage>        | <diff> | <time> |
    Examples:
      | voltage | diff    | time          |
      | 87.9V   | Go!     |               |
      | 89.9V   | (+2.0)  | 12:00 (15m)   |
      | 95.0V   | (+7.1)  | 12:38 (53m)   |
      | 96.3V   | (+8.4)  | 12:48 (1h3m)  |
      | 97.7V   | (+9.8)  | 12:59 (1h14m) |
      | 99.5V   | (+11.6) | 13:12 (1h27m) |

  Scenario: Reconnect to update the voltage
    Given this simulated device:
      | Bt Name | Bt Address        | Km     | Mileage | Voltage  |
      | LK1234  | C0:C1:C2:C3:C4:C5 | 12.218 | 705.615 | 88.5001V |
    And I request to charge for 40 km
    When I reconnect to update the voltage
    Then it displays an actual voltage of 88.5V
    And it displays these charging estimates:
      | required voltage | diff   | time        |
      | 95.0V            | (+6.5) | 12:34 (49m) |
