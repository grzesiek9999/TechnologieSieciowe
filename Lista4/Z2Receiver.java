import java.net.*;

public class Z2Receiver {
    static final int datagramSize = 50;
    InetAddress localHost;
    int destinationPort;
    DatagramSocket socket;

    ReceiverThread receiver;

    Z2Packet[] received = new Z2Packet[Z2Sender.maxPacket];
    int printedPackets = 0;

    public Z2Receiver(int myPort, int destPort) throws Exception {
        for(int i=0; i<Z2Sender.maxPacket; i++){
            received[i] = null;
        }
        localHost = InetAddress.getByName("127.0.0.1");
        destinationPort = destPort;
        socket = new DatagramSocket(myPort);
        receiver = new ReceiverThread();
    }

    class ReceiverThread extends Thread {
        public void run() {
            try {
                while (true) {
                    byte[] data = new byte[datagramSize];
                    DatagramPacket packet =
                            new DatagramPacket(data, datagramSize);
                    socket.receive(packet);
                    Z2Packet p = new Z2Packet(packet.getData());
                    received[p.getIntAt(0)] = p;
                    printPackets();
                    // WYSLANIE POTWIERDZENIA
                    packet.setPort(destinationPort);
                    socket.send(packet);
                }
            } catch (Exception e) {
                System.out.println("Z2Receiver.ReceiverThread.run: " + e);
            }
        }
    }

    public  void printPackets(){
        for(int i=printedPackets; i<received.length; i++){
            if(received[i] == null)
                break;
            System.out.println("R:" + i + ": " + (char) received[i].data[4]);
            printedPackets = i+1;
        }
    }

    public static void main(String[] args) throws Exception {
        Z2Receiver receiver = new Z2Receiver(Integer.parseInt(args[0]),
                Integer.parseInt(args[1]));
        receiver.receiver.start();
    }
}