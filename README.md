# Running the application

## Requirements:

Essential:
- [Java 8](https://www.oracle.com/ie/java/technologies/javase/javase-jdk8-downloads.html)
- [Maven](http://maven.apache.org/download.cgi)
- [Neo4J](https://neo4j.com/download-neo4j-now/?utm_source=google&utm_medium=cpc&utm_campaign=uk-search-branded&utm_adgroup=neo4j-desktop&gclid=EAIaIQobChMI3sCOhOm97AIVCbrtCh3zJwzSEAAYASABEgLGF_D_BwE)

Recommended:
- [Eclipse IDE](https://www.eclipse.org/ide/)

# Getting started

Ensure Neo4J is running and database is started with a bolt address that matches the one in [application.properties](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/resources/application.properties).

The following Neo4J entities are created by the manager agent when Experiment 2 is run:
- Application: applications to be deployed. Linked to a location once deployed.
- Location: locations (local or remote) available for deployments
- Monitors: agents responsible for applications. Linked to a location once deployed - typically the same one as the application but a different port. 

## Configuration requirements:

See [application.properties](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/resources/application.properties) for examples:
 - Neo4J username, password and location
 - Directory on the server where monitoring agents will be deployed where the jar file can be found (agent.directory)
 - The name of the jar file for monitoring agents (agent.monitor.jar)

## Compilation requirements:

A jar needs to be compiled for the monitor, named (`agent.monitor.jar`) and located (`agent.directory`) as per the agent.directory configuration requirements. The script [`buildjar.command`](https://github.com/katharinebeaumont/manager-monitor/blob/master/buildjar.command) can be run, but needs to be modified to reflect the directory the code is in. Recommended folder structure:

   - Parent directory
    	- manager-monitor directory
      - mock-service directory
       - buildjar.command
       - clean.command
       - monitor.jar
       - mockService.jar
 
Running `buildjar.command` will copy `monitor.jar` to the parent folder. `mockService.jar` needs to be built as per [https://github.com/katharinebeaumont/mock-service](https://github.com/katharinebeaumont/mock-service).

Otherwise within the code directory (manager-monitor) run `mvn install` to create the jar file for the manager agent. This will appear in the subfolder `target` as `manager-monitor-0.0.1-SNAPSHOT.jar`. Rename it `manager.jar` and move to the parent directory.
Copy `application.properties.monitor` and name it `application.properties`, then run `mvn install` to create the jar file for the monitoring agent. This will appear in the subfolder `target` as `manager-monitor-0.0.1-SNAPSHOT.jar`. Rename it `monitor.jar` and move to the parent directory.

## Manager Agent

To run in manager mode:
- Ensure `application.properties` contains

    agent.mode=manager
    
    agent.name=manager

This can be run from a code editor (e.g. Eclipse IDE or IntelliJ IDE) or packaged as a jar and executed on the command line by running `java -jar manager.jar` from the parent directory.

The manager agent reads in Applications from Neo4J. The starting point is [`agent.manager.ManagerAgent`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/java/agent/AgentApplication.java)

 - Experiment 2 is turned on by deafult in application.properties. See [`agent.AgentApplication`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/java/agent/AgentApplication.java). This wipes the Neo4J database, then creates and saves one application and two locations on localhost (port 3000 and 7000). It requires the mock service to be packaged as `mockService.jar`.
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

Running the code in an IDE is recommended for this experiemnt. Open Eclipse or IntelliJ IDE and navigate to src/test/java/agent/learning. The tests within [`agent.learning.QLearningSimulationManager`](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/test/java/agent/learning/QLearningSimulationManager.java) should be run. The results are output to files `Results_1`, `Results_2` and `Results_3` in the manager-monitor directory.

## Running Experiment 2

Launch the manager agent with `experiment=2` configured in [application.properties](https://github.com/katharinebeaumont/manager-monitor/blob/master/src/main/resources/application.properties). This automatically starts the experiment. To observe learnings, navigage to http://localhost:7070/agents.
