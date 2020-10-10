# Running the application

Requirements:
- Java 8
- Maven
- Neo4J

Configuration requirements:
- See [`application.properties`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/resources/application.properties):
 - Neo4J username, password and location
 - Directory on the server where monitoring agents will be deployed where the jar file can be found
 (eventually this will be transferred across to the home directory of the server)
 - The name of the jar file for monitoring agents

Compilation requirements:
- A jar needs to be compiled for the monitor, named (`agent.monitor.jar`) and located (`agent.directory`) as per the configuration requirements.

Start the application in `agent.mode=manager` (see [`application.properties`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/resources/application.properties)).
See [PLAN.md](https://github.com/katharinebeaumont/manager-monitor/blob/master/PLAN.md) for details of what needs to be done.

# Manager

To run in manager mode:
- Ensure `application.properties` contains

    agent.mode=manager
    agent.name=manager

Neo4J entities:
- Application: applications to be deployed. Linked to a location once deployed.
- Location: locations (local or remote) available for deployments
- Monitors: agents responsible for applications. Linked to a location once deployed - typically the same one as the application but a different port. 

Reads in Applications from Neo4J:
 - At the moment this wipes the Neo4J database, then creates and saves two applications (twitterClient and twitterService) and two locations on localhost (port 8000 and 9000).
 - The ManagerAgent is started. This:
    - Reads in applications and for each, checks if there is a monitoring agent
    - If not, generate an agent. For now, this is just a packaged jar file. In the future, it could be instructions to download from GitHub, compile, etc, or deploy and start a docker container…
    - Starts a heartbeat with the monitoring agent which feeds status information about the application it is responsible for
    - Makes decisions based on the status of the applications and instructs the monitoring agent to:
    	- Restart
    	- Relocate
    	- No action

# Monitor

To run in monitor mode:
- Ensure `application.properties` contains

    agent.mode=monitor
    
  and that the `agent.name` matches a Monitor in the database, that has an Application, and both are linked to a Location.
  
- Alternatively run this command, substituting in the agent name for one that matches a Monitor in the database as for above, and ensuring the agent service jar matches an existing jar for the mock service.

`java -jar -Dlogging.file=monitoring_agent.txt -Dagent.mode=monitor -Dagent.name=test-monitoring_agent -Dagent.service.jar=monitor.jar monitor.jar`

Starts the application locally on the instructed port.
Monitors the application based on available metrics. These depend on the application.

# Getting started

Ensure Neo4J is running and database is started

