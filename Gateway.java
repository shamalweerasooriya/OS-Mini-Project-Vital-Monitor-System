import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

// This is the gateway server to listen for monitor requests and create a tcp connection to the monitor.
public class Gateway {
    // listen broadcast messages from vital monitors on port 6000
    public static void main(String[] args) {
        // UDP packet receiving port
        int UDP_RCV_PORT = 6000;

        // create a datagram socket to receive broadcast messages
        DatagramSocket recvSocket = createRecvSocket(UDP_RCV_PORT);

        // receive broadcast messages
        DatagramPacket recvPacket = recvPacket(recvSocket);

        // print ip address and port of the monitor
        printIPAndPort(recvPacket);

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
        
}

