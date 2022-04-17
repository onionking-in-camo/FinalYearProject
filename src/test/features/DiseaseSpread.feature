Feature: Disease spread in SIR model

  Scenario: No contacts occur between agents when all agents are Infected
    Given field is 2, 2
    And field is filled with Infected
    When check for contacts
    Then contacts size should be 0

  Scenario: All Susceptible agents have contact when they are neighbours to an Infected agent
    Given field is 3, 3
    And field is filled with Susceptible
    And agent at 1, 1 is Infected
    When check for contacts
    Then contacts size should be 8

  Scenario: Highly infectious disease always transmits to Susceptible agent
    Given field is 3, 3
    And field is filled with Infected
    And agent at 1, 1 is Susceptible
    And chance of infection is 1.0
    When check for newly infected
    Then newly infected should equal 1
  
  Scenario: Disease is non-transmissive and does not transmit to Susceptible agent
    Given field is 3, 3
    And field is filled with Infected
    And agent at 1, 1 is Susceptible
    And chance of infection is 0.0
    When check for newly infected
    Then newly infected should equal 0