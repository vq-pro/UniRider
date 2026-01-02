Feature: Wheel Charging

  Background:
    Given this wheel:
      | Name      | Mileage | Wh   | Voltage Min | Voltage Max | Charge Rate | Full Charge | Charger Offset | Distance Offset | Sold |
      | Sherman L | 4000    | 4000 | 104.4V      | 151.2V      | 21V/h       | 150.1V      | 1.8V           | 1.0667          | No   |
    And this wheel is connected:
      | Name      | Bt Name | Bt Address        |
      | Sherman L | LK13447 | AB:CD:EF:GH:IJ:KL |
    And the current time is 11:45
    And I start the app
    And I select the Sherman L
    And I set the actual voltage to 136.5V
    And I set the distance to 30 km
    And it displays these estimates:
      | remaining | total range |
      | 28.1      | 58.1        |
    And I charge the wheel
#    FIXME-1 Force a popup when entering this screen, and when OK do another connect to get the voltage actual on charger. Calculate the charger offset live.

#  FIXME-2 Pour charger, il faut absolument capturer le voltage initial.
#  Donc quand on clique sur le bouton Charger:
#    * Se connecter pour capturer le voltage initial, AVANT de brancher le chargeur
#    * Afficher un popup pour indiquer quand le chargeur est brancher
#    * Brancher le charger, et appuyer sur le bouton
#    * Entrer dans l'écran de charge, recapturer le voltage actuel et faire le calcul de l'offset chargeur
#    * Afficher le voltage requis en tenant compte de ce décallage
#    * Pour une charge partielle par km, ajouter le décallage au voltage requis

  Scenario: Changing the actual voltage
    Given I request to charge for 40 km
    And it displays an actual voltage of 138.3V
    And it displays these charging estimates:
      | required      | target | time        |
      | 143.1V (+4.8) | 141.3V | 11:59 (14m) |
    When I change the actual voltage to 140.0V
    Then it displays these charging estimates:
      | required      | target | time       |
      | 143.1V (+3.1) | 141.3V | 11:54 (9m) |

  Scenario Outline: Charging a wheel by distance [<distance>]
    When I request to charge for <distance>
    Then it displays an actual voltage of 138.3V
    And the full charge indicator is <fc_indicator>
    And it displays these charging estimates:
      | required   | target   | time   |
      | <required> | <target> | <time> |
    Examples:
      | distance | fc_indicator | required       | target | time        |
      | 0 km     | off          | 138.3V         | 136.5V | Go!         |
      | 10 km    | off          | 129.3V         | 127.5V | Go!         |
      | 20 km    | off          | 135.1V         | 133.3V | Go!         |
      | 40 km    | off          | 143.1V (+4.8)  | 141.3V | 11:59 (14m) |
      | 50 km    | off          | 145.0V (+6.7)  | 143.2V | 12:04 (19m) |
      | 60 km    | off          | 150.1V (+11.8) | 148.3V | 12:19 (34m) |
      | full     | on           | 150.1V (+11.8) | 148.3V | 12:19 (34m) |

  Scenario Outline: Charging a wheel by voltage [<required>]
    When I request to charge to <required>
    Then it displays an actual voltage of 138.3V
    And the full charge indicator is off
    And it displays these charging estimates:
      | required   | target   | time   |
      | <required> | <target> | <time> |
    Examples:
      | required       | target | time        |
      | 135.6V         | 133.8V | Go!         |
      | 140.0V (+1.7)  | 138.2V | 11:50 (5m)  |
      | 142.7V (+4.4)  | 140.9V | 11:58 (13m) |
      | 144.5V (+6.2)  | 142.7V | 12:03 (18m) |
      | 147.9V (+9.6)  | 146.1V | 12:12 (27m) |
      | 150.1V (+11.8) | 148.3V | 12:19 (34m) |

  Scenario: Reconnect to update the voltage
    Given this simulated device:
      | Bt Name | Bt Address        | Km     | Mileage   | Voltage |
      | LK13447 | AB:CD:EF:GH:IJ:KL | 21.867 | 20020.518 | 141.01V |
    And I request to charge for 40 km
    When I reconnect to update the voltage
    Then it displays an actual voltage of 141.0V
    And it displays these charging estimates:
      | required      | target | time       |
      | 143.1V (+2.1) | 141.3V | 11:51 (6m) |
