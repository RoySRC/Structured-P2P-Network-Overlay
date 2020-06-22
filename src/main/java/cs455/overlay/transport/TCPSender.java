package cs455.overlay.transport;

import java.io.*;
import java.net.*;

import cs455.overlay.node.Node;
import cs455.overlay.util.LOGGER;
import cs455.overlay.wireformats.Event;

public class TCPSender {

	// for logging
	private static final LOGGER log = new LOGGER(TCPSender.class.getSimpleName());

	private Socket socket;
	private DataOutputStream dout;
	private Node node = null;

	public TCPSender(Socket socket, Node node) throws IOException {
		this.node = node;
		this.socket = socket;
		dout = new DataOutputStream(this.socket.getOutputStream());
	}

	public void kill() throws IOException {
		log.info("Initiated kill.");
		dout.close();
	}

	/**
	 * The following function sends a marshalled byte array
	 * @param dataToSend Marshalled byte array to send
	 * @throws Exception 
	 */
	public void sendData(byte[] dataToSend) throws IOException {
		dout.writeInt(dataToSend.length);
		dout.write(dataToSend);
		dout.flush();
	}
	
}
