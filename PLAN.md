# What needs to change for deployments?

1. How the agents access the Neo4J database.
Considerations: if a monitor in a different geographic location gets promoted to manager, latency reading/writing to the database.

2. Flexible environmental feedback.
If applications and monitors are deployed on different platforms, the monitoring information might be different. Should this be standardised by the monitoring agents? It should be flexible enough that new information fields can be added. 



# What needs doing?

NB. excludes code 'TODO's.

Immediate:
- Research CDNs and agents
- Research 'real' monitoring feedback
- Design location factors
- Design agent status updates 
- Write service mock
- Start write up

Experiments:
- Design experiments 
- Perform a remote deployment (Heroku? AWS?)
- Write simulation

Nice to have:
- Unit testing
- End-to-end testing
