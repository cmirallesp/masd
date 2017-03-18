[TOC]

# Beliefs

```prolog
Beliefs:
```

Agent believes that is in position (1,1), has no gold, there are two trashes and blockworld is clean when no trash everywhere

# Goals
```prolog
Goals:
```

Agent with two goals, one in which has 5 gold and the environment is clean, and the second one in which has 10 gold.

The beliefs and goals of an agent are governed by a rationality principle. According to this principle, if an agent believes a certain fact, then the agent does not pursue that fact as a goal.

# Actions

Each action is specified by pre-post condition as `{pre} action_name {post} `

5 kind of actions: 

 1. to update beliefs
 2. to test beliefs and goals bases
 3. to manage the dynamics of goals
 4. abstract actions
 5. communication actions
 6. external actions

## To update beliefs
```prolog
BeliefUpdates:
  {not carry(gold)} PickUp() {carry(gold)}
  {trash(X,Y) and pos(X,Y)} RemoveTrash() {not trash(X,Y)}
```

## Test action
B($$\phi$$) for test belief and G($$\phi$$) for   A test action can be used in a plan to (1) instantiate variables in the subsequent actions of the plan (if the test succeeds), or (2) block the execution of the plan (if the test fails).

**For example:** if an agent believes p(a) and has the goal q(b) then: 

* the test action B(p(X)) G(q(X)) **fails** 
* while the test action B(p(X)) G(q(Y) or r(X)) **succeeds with {X/a , Y/b} as the resulting substitution**.

## Manage dynamics of goals

* adopta($$\phi$$), adoptz($$\phi$$) : Add the goal $$\phi$$ at the begining/end.
* dropgoal($$\phi$$), dropsubgoal($$\phi$$), dropsupergoal($$\phi$$): drop goal $$\phi$$, all subgoals of $$\phi$$ and all supergoals of $$\phi$$.

## Abstract action

¿¿Not clear at this moment??

## Communication action

* send(Receiver, Performative, Language, Ontology, Content). Performative: (inform, request,..) 
* receive(Receiver, Performative, Content).

It should be noted that 2APL interpreter is built on the FIPA compliant JADE platform.  For this reason, the name of the receiving agent can be a local name or a full JADE name. A full JADE name has the form localname@host:port/JADE

## External action

An external action is supposed to change the state of an external environment. Is an expression of the form @env(ActionName,Return), where env is the name of the agent’s environment (implemented as a Java class), ActionName is a method call (of the Java class) that specifies the effect of the external action in the environment, and Return is a list of values, possibly an empty list, returned by the corresponding method. 

**Example:** @blockworld(east(),L) (go one step east in the blockworld environment). The effect of this action is that the position of the agent in the blockworld environment is shifted one slot to the right. The list L is expected as the return value.


# Plans

Basic actions composed by sequence/conditional/iterator operator:

* sequence: [$$\pi_1$$;$$\pi_2$$]
* iterator: while $$\phi$$ do $$\pi_1$$
* conditional: if $$\phi$$ then $$\pi_1$$ else $$\pi_2$$

```prolog
Plans:
```

# Practical reasoning rules
Three kinds of PRR:

* Planning global rules
* Procedure call rules
* Plan repair rules

## Planning global rules (PG-Rules)

A planning goal rule is of the form: `[ ⟨goalquery⟩ ] "<-" ⟨belquery⟩ "|" ⟨plan⟩`

```prolog
PG-rules:
```

Note that this rule can be applied if (beside the satisfaction of the belief condition) the agent has a conjunctive goal hadGold(5) and clean(blockworld) since the head of the rule is entailed by this goal.

## Procedure call rules (PC-Rules)

The procedure call rule is introduced for various reasons and purposes: 

* the **reception of messages** sent by other agents
* the **reception of events** generated by the external environments
* the **execution of abstract actions**

```prolog
PC-rules:
  % Message type: if an agent A informs that there is some gold at position (X2,Y2) 
  % and the agent believes it does not carry a gold item, then the agent has to get 
  % and store the gold item in the depot
  % Event type: if the environment blockworld notifies the agent that there is some 
  % gold at position (X2,Y2) and the agent believes it does not carry a gold item, 
  % then the agent has to do the same, i.e., get and store the gold item in the depot
  % this rule indicates that the abstract action getAndStoreGold should be performed as a certain sequence 
  % of actions, i.e., go from its current position (obtained through the condition of the rule) to the gold 
  % position, pick up the gold item, go to the depot position (i.e., position (3,3)), and store the gold 
  % item in the depot. 
```

## Plan repair rules (PR-rules)
