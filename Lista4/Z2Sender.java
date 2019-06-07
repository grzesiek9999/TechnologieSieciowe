import java.net.*;

class Z2Sender {
	static final int datagramSize = 50;
	static final int sleepTime = 500;
	static final int maxPacket = 50;
	InetAddress localHost;
	int destinationPort;
	DatagramSocket socket;
	SenderThread sender;
	ReceiverThread receiver;

	private DatagramPacket[] sent = new DatagramPacket[maxPacket];
	private Z2Packet[] received = new Z2Packet[maxPacket];
	private int numberOfReceived = 0;
	private int numberOfSent = 0;

	public Z2Sender(int myPort, int destPort) throws Exception {
		for(int i=0; i<maxPacket; i++)
			received[i] = null;
		localHost = InetAddress.getByName("127.0.0.1");
		destinationPort = destPort;
		socket = new DatagramSocket(myPort);
		sender = new SenderThread();
		receiver = new ReceiverThread();
	}

	class SenderThread extends Thread {
		public void run() {
			int i, x;
			try {
				for (i = 0; (x = System.in.read()) >= 0; i++) {
					Z2Packet p = new Z2Packet(4 + 1);
					p.setIntAt(i, 0);
					p.data[4] = (byte) x;
					DatagramPacket packet =
							new DatagramPacket(p.data, p.data.length,
									localHost, destinationPort);
					numberOfSent++;
					sent[i] = packet;
					socket.send(packet);
					sleep(sleepTime);
				}
				sleep(Z2Forwarder.maxDelay + 1000);
				while(numberOfReceived<numberOfSent) {
					System.out.println("resending missing packets...");
					for (int k = 0; k < maxPacket; k++) {
						if (received[k] == null && sent[k] != null) {
							socket.send(sent[k]);
						}
					}
					sleep(Z2Forwarder.maxDelay + 1500);
				}
				//System.out.println(numberOfReceived + " " + numberOfSent);
				/*for(i=0; i<numberOfSent; i++){
					System.out.println("sent: " + sent[i] + ", received: " + received[i]);
				}
				/*for (int k = 0; k < maxPacket; k++) {
					if (received[k] == null && sent[k] != null) {
						socket.send(sent[k]);
					}
				}*/
				System.out.println("resending done");
			} catch (Exception e) {
				System.out.println("Z2Sender.SenderThread.run: " + e);
				e.printStackTrace();
			}
		}
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
					if(received[p.getIntAt(0)] == null) {
						received[p.getIntAt(0)] = p;
						sent[p.getIntAt(0)] = null;
						numberOfReceived++;
					}
				}
			} catch (Exception e) {
				System.out.println("Z2Sender.ReceiverThread.run: " + e);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Z2Sender sender = new Z2Sender(Integer.parseInt(args[0]),
				Integer.parseInt(args[1]));
		sender.sender.start();
		sender.receiver.start();
	}
}