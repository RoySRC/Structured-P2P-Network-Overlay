package cs455.overlay.transport;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import cs455.overlay.node.Node;
import cs455.overlay.util.LOGGER;
import cs455.overlay.util.Util;
import cs455.overlay.wireformats.Event;

/**
 * This is the TCPServer class that runs on a separate thread listening
 * for connections. When a client connects, this class returns an instance
 * of the TCPConnection object to be handled by the class that created this
 * {@link TCPServerThread}
 * 
 * @author sajeeb
 */

public class TCPServerThread implements Runnable {

	// for logging
	private static final LOGGER log = new LOGGER(TCPServerThread.class.getSimpleName());

	private final ServerSocket server_socket;
	private final Node node;
	
	/**
	 * Constructor for TCPServer class. If port is zero, 
	 * the server socket picks a suitable port to bind to.
	 * @param port
	 * @throws IOException
	 */
	public TCPServerThread(int port, Node node) throws IOException {		
		this.server_socket = new ServerSocket( port );
		this.node = node;
	}

	/**
	 * Get the local port the server socket is bound to
	 * @return
	 */
	public int getLocalPort() {
		return server_socket.getLocalPort();
	}
	
	@Override
	public void run() {
		while (server_socket != null) {
			try {
				Socket incoming_connection = server_socket.accept();

				TCPConnection tcp_connection = new TCPConnection(incoming_connection, node);
				log.info("Received connection from: "+tcp_connection.getRemoteAddressPortPair());
				node.addTCPConnection(tcp_connection);
						
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
