# Running the application

Requirements:
- Java 8
- Maven
- Neo4J

Configuration requirements:
- See `application.properties`:
 - Neo4J username, password and location
 - Directory on the server where monitoring agents will be deployed where the jar file can be found
 (eventually this will be transferred across to the home directory of the server)
 - The name of the jar file for monitoring agents

Start the application in `agent.mode=manager` (see `application.properties`).

# Manager

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

Starts the application locally on the instructed port.
Monitors the application based on available metrics. These depend on the application.
