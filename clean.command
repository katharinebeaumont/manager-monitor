echo "Cleaning agents"
#Following needs to be changed to reflect directory where code is installed
cd Documents/UCD/Semester\ 3/ms-with-agents/
rm -r mockService
echo "removed mock service file"
rm -r mockService-monitoring_agent/
echo "removed monitoring agent file"
rm monitoring_agent_logs*.txt
echo "removed monitoring agent logs"
rm application.pid
echo "removed application.pid"
cd manager-monitor/
rm managerAgentLogs.txt*
echo "removed manager logs"
Kill $(ps -e | grep mockService)
echo "killed mock service"