package cs455.overlay.transport;

import java.io.*;
import java.net.*;

import cs455.overlay.node.Node;
import cs455.overlay.node.RegistryNode;
import cs455.overlay.util.LOGGER;
import cs455.overlay.util.Util;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

public class TCPReceiverThread implements Runnable {

	// for logging
	private static final LOGGER log = new LOGGER(TCPReceiverThread.class.getSimpleName());

	private final Socket socket;
	private final DataInputStream din;
	private final Node node;
	private final TCPSender tcp_sender;
	private volatile boolean _kill_ = false;

	public TCPReceiverThread(Socket socket, Node node, TCPSender tcp_sender) throws IOException {
		this.tcp_sender = tcp_sender;
		this.node = node;
		this.socket = socket;
		din = new DataInputStream(this.socket.getInputStream());
	}

	public void kill() throws IOException {
		log.info("Initiated kill.");
		din.close();
		_kill_ = true;
	}

	/**
	 * The following function received a marshalled byte array from
	 * a client and returns
	 */
	public void receiveData() {
		while (socket != null && !_kill_) {
			try {
				byte[] marshalledBytes = new byte[(int)din.readInt()];
				din.readFully(marshalledBytes);
				Event e = EventFactory.getInstance().createEvent(marshalledBytes);
				node.onEvent(e, Util.getRemoteAddressPortPair(this.socket));
				
			} catch (Exception e) {
				TCPConnection temp = new TCPConnection(socket, node);
				log.info("Disconnected from: "+temp.getRemoteHostName()+":"+temp.getRemoteAddressPortPair());
				log.printStackTrace(e);
				node.tcpReceiverErrorHandler(temp);
				break;
			}
		}
	}

	@Override
	public void run() {
		receiveData();
	}
	
}
