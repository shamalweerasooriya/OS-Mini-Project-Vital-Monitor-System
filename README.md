# OS-Mini-Project-Vital-Monitor-System
Implementing a gateway that discovers vital monitors in a hospital. In a hospital, there will be a number of patients whose vitals (things like heart rate, blood pressure, etc.) should be monitored. Each patient would be connected to a vital monitor and these monitors will transmit the vital information over a network to a central location. That way nursing staff will be able to monitor many patients and do it remotely – which is useful when the patients are contagious.

---------------------------------------

Run runMultipleMonitors.sh 
```console
foo@bar:~$ chmod +x runMultipleMonitors.sh
foo@bar:~$ ./runMultipleMonitors.sh
```
Compile and run Gateway.java
```console
foo@bar:~$ javac Gateway.java
foo@bar:~$ java Gateway
```
### Results

![Screenshot 2022-04-24 at 7.14.37 PM.png](https://www.dropbox.com/s/796urw8kjdcqzdj/Screenshot%202022-04-24%20at%207.14.37%20PM.png?dl=0&raw=1)

After terminating the Gateway killMultipleMonitors.sh can be used to kill the monitors created by runMultipleMonitors.sh.

