Feature: Policy influence in simulation

  Scenario: Agents are socially distancing
    Given field of 5, 5
    And 'social distancing' policy is in effect
    And there is an agent at 1, 1
    And there is an agent at 1, 2
    And there is an agent at 2, 1
    And there is an agent at 2, 2
    And all agents are compliant
    When agents move
    Then each agent has no neighbours

  Scenario: Agents are quarantining if Infected
    Given field of 2, 2
    And 'quarantining' policy is in effect
    And there is an agent at 0, 0
    And there is an agent at 1, 1
    And all agents are compliant
    And all agents are infected
    When agents move
    Then agent is at 0, 0
    And agent is at 1, 1
