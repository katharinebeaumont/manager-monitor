# Running the application

## Requirements:
- Java 8
- Maven
- Neo4J

## Getting started

Ensure Neo4J is running and database is started.

Neo4J entities:
- Application: applications to be deployed. Linked to a location once deployed.
- Location: locations (local or remote) available for deployments
- Monitors: agents responsible for applications. Linked to a location once deployed - typically the same one as the application but a different port. 

## Configuration requirements:

See [application.properties](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/resources/application.properties) for examples:
 - Neo4J username, password and location
 - Directory on the server where monitoring agents will be deployed where the jar file can be found (agent.directory)
 - The name of the jar file for monitoring agents (agent.monitor.jar)

## Compilation requirements:

A jar needs to be compiled for the monitor, named (`agent.monitor.jar`) and located (`agent.directory`) as per the configuration requirements. The script [`buildjar.command`](https://github.com/katharinebeaumont/manager-monitor/blob/master/buildjar.command) can be run, but needs to be modified to reflect the directory the code is in. Recommended folder structure:

   - Parent folder
    	- manager-monitor directory
      - mock-service directory
       - buildjar.command
       - clean.command
       - monitor.jar
       - mockService.jar
 
Running `buildjar.command` will copy `monitor.jar` to the parent folder. `mockService.jar` needs to be built as per [https://github.com/katharinebeaumont/mock-service](https://github.com/katharinebeaumont/mock-service).

## Manager Agent

To run in manager mode:
- Ensure `application.properties` contains

    agent.mode=manager
    
    agent.name=manager

This can be run from a code editor (e.g. Eclipse or IntelliJ) or packaged as a jar and executed on the command line.

The manager agent reads in Applications from Neo4J. The starting point is [`agent.manager.ManagerAgent`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/java/agent/AgentApplication.java)

 - Experiment 1 (see [`agent.AgentApplication`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/java/agent/AgentApplication.java)) wipes the Neo4J database, then creates and saves one application and two locations on localhost (port 3000 and 7000). It requires the mock service to be packaged as `mockService.jar`.
 - The application will have either port 3000 or port 7000 assigned to it as a location, and the microservice will be deployed to the same host (so, localhost) at a different port (3010 or 7010 depending).
 - The monitoring agent launches the mock service at the assigned location. The manager agent logs are output as per the logging.file configuration in [`application.properties`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/resources/application.properties)
 - By default the manager agent runs on http://localhost:7070/, and details of the monitoring agents can be seen at http://localhost:7070/agents.

## Running the Monitoring Agent

To run in monitor mode:
- Ensure `application.properties` contains

    agent.mode=monitor
    
  and that the `agent.name` matches a Monitor in the database, that has an Application (the RESPONSIBLE_FOR relationship), and both are linked to a Location.
  
- Alternatively run this command, substituting in the agent name for one that matches a Monitor in the database as for above, and ensuring the agent service jar matches an existing jar for the mock service.

`java -jar -Dlogging.file=monitoring_agent.txt -Dagent.mode=monitor -Dagent.name=test-monitoring_agent -Dagent.service.jar=monitor.jar monitor.jar`

 The starting point is [`agent.monitor.MonitoringAgent`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/java/agent/monitor/MonitoringAgent.java)

## Running Experiment 1


