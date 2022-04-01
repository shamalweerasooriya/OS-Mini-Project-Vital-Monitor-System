# kill monitor process at port 5000
kill $(ps -ef | grep "CICU_1" | grep -v grep | awk '{print $2}')