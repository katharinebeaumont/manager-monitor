
# Manager state:action pairings
## State

This comprises of

- Monitor identifier
- Location: which machine and port the monitor is located on
- Is Duplicate flag: are there multiple monitors observing duplicate microservices
- Microservice internals:
  - JVM memory
  - Network latency
  
## Action

Actions taken:
- Move location
- Do nothing
- Retire service
(Future: duplicate)

## Environmental feedback 

Is supplied in the State, but also in the form of a health rating.

0: service is up and running
-1: service is underperforming
-5: service is down

# Monitor state:action pairings

## State
- Microservice internals:
  - JVM memory (high/low)
  - Network latency (good/bad)

## Action

- Increase JVM Memory
- Ask Manager to intervene 
- Do nothing
 
