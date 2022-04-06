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

import javax.xml.transform.OutputKeys;

// This is the gateway server to listen for monitor requests and create a tcp connection to the monitor.
public class Gateway {
    // listen broadcast messages from vital monitors on port 6000
    public static void main(String[] args) {
        // UDP packet receiving port
        int UDP_RCV_PORT = 6000;

        // create a datagram socket to receive broadcast messages
        DatagramSocket recvSocket = createRecvSocket(UDP_RCV_PORT);

        // receive broadcast messages
        // DatagramPacket recvPacket = recvPacket(recvSocket);

        // print ip address and port of the monitor
        // printIPAndPort(recvPacket);

        // keep receiving broadcast messages and create a tcp connection to the monitor
        while (true) {
            // receive broadcast messages
            DatagramPacket recvPacket = recvPacket(recvSocket);

            // print ip address and port of the monitor
            // printIPAndPort(recvPacket);

            // create tcp thread to connect to the monitor
            Thread tcpConnection = new Thread(new Runnable() {
                @Override
                public void run() {
                    // create a tcp connection to the monitor
                    createTcpConnection(recvPacket);
                }
            });

            // start the tcp connection thread
            tcpConnection.start();

        }

        // try {
        //     recvSocket = new DatagramSocket(UDP_RCV_PORT);

        //     // receive broadcast messages
        //     byte[] recvBuf = new byte[1024];
        //     DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
        //     recvSocket.receive(recvPacket);

        //     // byte array input stream
        //     ByteArrayInputStream bis = new ByteArrayInputStream(recvPacket.getData());
        //     ObjectInputStream ois = new ObjectInputStream(bis);

        //     // get monitor object from the received packet
        //     Monitor monitor = (Monitor) ois.readObject();

        //     // print ip address and port number
        //     System.out.println("IP: " + monitor.getIp() + ", Port: " + monitor.getPort());
        // }
        // catch (Exception e) {
        //     e.printStackTrace();
        // }

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
    private static void createTcpConnection(DatagramPacket recvPacket) {
        try {
            // byte array input stream
            ByteArrayInputStream bis = new ByteArrayInputStream(recvPacket.getData());
            ObjectInputStream ois = new ObjectInputStream(bis);

            // get monitor object from the received packet
            Monitor monitor = (Monitor) ois.readObject();

            // save ip address, port number and id of the monitor
            InetAddress ip = monitor.getIp();
            int port = monitor.getPort();
            String id = monitor.getMonitorID();

            // create a client socket to connect to the monitor
            Socket clientSocket = new Socket(ip, port);

            // print monitor info
            // System.out.println(monitor.monitor_str());

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

            // read data from the monitor
            int data = isr.read();
            while (data != '\n') {
                buffer.append((char) data);
                data = isr.read();
            }

            System.out.println(buffer.toString());

            // close the socket
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
}

