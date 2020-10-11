
echo "Copying new jars"
#Change following directory according to where code is installed:
cd Documents/UCD/Semester\ 3/ms-with-agents/manager-monitor/src/main/resources
cp application.properties.monitor application.properties
cd ../../../
mvn install
cp target/manager-monitor-0.0.1-SNAPSHOT.jar ../monitor.jar
cd src/main/resources
cp application.properties.manager application.properties
cd ../../../
mvn install
echo "Copied monitor"


