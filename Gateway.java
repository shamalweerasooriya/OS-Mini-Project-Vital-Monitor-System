import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.OutputKeys;

// This is the gateway server to listen for monitor requests and create a tcp connection to the monitor.
public class Gateway {
    // create a list to store ip address and port of monitors
    static private List<String> monitors = new ArrayList<String>();

    // listen broadcast messages from vital monitors on port 6000
    public static void main(String[] args) {
        // UDP packet receiving port
        int UDP_RCV_PORT = 6000;

        // create a datagram socket to receive broadcast messages
        DatagramSocket recvSocket = createRecvSocket(UDP_RCV_PORT);

        // keep receiving broadcast messages and create a tcp connection to the monitor
        while (true) {
            // receive broadcast messages
            DatagramPacket recvPacket = recvPacket(recvSocket);

            // get the monitor object from the received packet
            Monitor monitor = getMonitor(recvPacket);

            // get the ip address and port of the monitor
            InetAddress ipAddress = monitor.getIp();
            int port = monitor.getPort();


            // check if the monitor is already in the list
            if (!monitors.contains(ipAddress + ":" + port)) {
                // add the monitor to the list
                monitors.add(ipAddress + ":" + port);

                // create tcp thread to connect to the monitor
                Thread tcpConnection = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // create a tcp connection to the monitor
                        synchronized (this){
                            createTcpConnection(ipAddress, port, monitor.getMonitorID());
                        }
                    }
                });

                // start the tcp connection thread
                tcpConnection.start();
            }
        }

    }
    
    private static DatagramSocket createRecvSocket(int UDP_RCV_PORT) {
        DatagramSocket recvSocket = null;
        try {
            recvSocket = new DatagramSocket(UDP_RCV_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return recvSocket;
    }

    private static DatagramPacket recvPacket(DatagramSocket recvSocket) {
        DatagramPacket recvPacket = null;
        try {
            byte[] recvBuf = new byte[1024];
            recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
            recvSocket.receive(recvPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recvPacket;
    }
    
    private static Monitor getMonitor(DatagramPacket recvPacket) {
        Monitor monitor = null;
        try {
            // get the monitor object from the received packet
            InputStream in = new ByteArrayInputStream(recvPacket.getData());
            ObjectInputStream ois = new ObjectInputStream(in);
            monitor = (Monitor) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monitor;
    }

    // print ip address and port number
    private static void printIPAndPort(DatagramPacket recvPacket) {
        try {
            // byte array input stream
            ByteArrayInputStream bis = new ByteArrayInputStream(recvPacket.getData());
            ObjectInputStream ois = new ObjectInputStream(bis);

            // get monitor object from the received packet
            Monitor monitor = (Monitor) ois.readObject();

            // print ip address and port number
            System.out.println("IP: " + monitor.getIp() + ", Port: " + monitor.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // create a tcp connection to the monitor
    private static void createTcpConnection(InetAddress ip, int port, String id) {
        try {
            // create a client socket to connect to the monitor
            Socket clientSocket = new Socket(ip, port);

            // decalre streams to send and receive data
            OutputStream os = clientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write("Requesting data from " + id + "\n");
            osw.flush();    // request sent

            // declaring input stream
            InputStream is = clientSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            // buffer to store data received
            StringBuffer buffer = new StringBuffer();

            

            // keep reading data from the monitor
            while (true) {
                // read data from the monitor
                int data = isr.read();
                while (data != '\n') {
                    buffer.append((char) data);
                    data = isr.read();
                }

                // break if the monitor sends "end"
                if (buffer.toString().equals("end")) {
                    break;
                }

                // print the data received
                printInfo(ip, port, id, buffer);
                
            }

            // close the socket
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // display the data received
    private synchronized static void printInfo(InetAddress ip, int port, String id, StringBuffer buffer) {
        System.out.println("+-----------------------------------------------------+");
        System.out.println("| Monitor ID    | " + id + "                              |");
        System.out.println("| Monitor IP    | " + ip + "                        |");
        System.out.println("| Monitor Port  | " + port + "                                |");
        System.out.println("| Data Received | " + buffer.toString() + "    |");
        System.out.println("+-----------------------------------------------------+");
        buffer.delete(0, buffer.length());
    }
        
}

