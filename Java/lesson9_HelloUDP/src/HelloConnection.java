import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class HelloConnection {
	DatagramSocket socket = null;

	public HelloConnection() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("Can't create socket");
		}
	}

	public HelloConnection(String host, int port) {
		try {
			socket = new DatagramSocket(port, InetAddress.getByName(host));
		} catch (SocketException e) {
			System.err.println("Can't create socket");
		} catch (UnknownHostException e) {
			System.err.println("Unknown host name");
		}
	}

	public HelloConnection(int port) {
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.err.println("Can't create socket");
		}
	}
	public void close() {
		if (socket != null) {
			socket.disconnect();
			socket.close();
		}
	}

	public boolean sendMessage(int port, InetAddress host, String message) {
		if (socket == null) return false;
		try {
			DatagramPacket packet = new DatagramPacket(message.getBytes("UTF-8"), message.getBytes("UTF-8").length, host, port);
			socket.send(packet);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public DatagramPacket receivePacket() {
		if (socket == null) return null;
		try {
			socket.setSoTimeout(500);
			int length = 1024;
			byte[] message = new byte[length];
			DatagramPacket packet = new DatagramPacket(message, length);			
			socket.receive(packet);
			return packet;
		} catch (SocketTimeoutException e) {
			return null;
		} catch (SocketException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}