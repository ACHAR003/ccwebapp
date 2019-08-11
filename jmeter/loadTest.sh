echo "Enter Path to JMETER binaries:"
read JMETER_BIN

echo "Enter Path to Test Plan file (.jmx):"
read TEST_PLAN_PATH

echo "Enter Path to place the output log file:"
read LOG_FILE_PATH

echo "Enter IP of the web application server:"
read SERVER_IP 

echo "Enter Port of the web application server:"
read SERVER_PORT 

echo "Enter Path to user data:"
read USER_DATA


$JMETER_BIN/jmeter.sh -n -t $TEST_PLAN_PATH -l $LOG_FILE_PATH -JServerIP=$SERVER_IP -JServerPort=$SERVER_PORT -JUserData=$USER_DATA
