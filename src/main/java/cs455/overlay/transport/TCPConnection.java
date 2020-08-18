package cs455.overlay.transport;

import java.io.*;
import java.net.*;

import cs455.overlay.node.Node;
import cs455.overlay.node.Peer;
import cs455.overlay.util.LOGGER;
import cs455.overlay.util.LOGGER;

public class TCPConnection {

	// for logging
	private static final LOGGER log = new LOGGER(TCPConnection.class.getSimpleName());

	// TCP sender and receiver objects
	private TCPSender tcp_sender = null;
	private TCPReceiverThread tcp_receiver = null;
	private final Socket socket;
	private final Node node;
	
	public TCPConnection(Socket socket, Node node) {
		this.node = node;
		this.socket = socket;
	}

	public String getRemoteAddressPortPair() {
		return getRemoteIP()+":"+getRemotePort();
	}

	public void kill() throws IOException {
		log.info("Initiated kill.");
		tcp_sender.kill();
		tcp_receiver.kill();
		socket.close();
	}

	/**
	 * Get the hostname of the endpoint of the TCPConnection.
	 * @return String containing the hostname of the endpoint of the TCPConnection
	 */
	public String getRemoteHostName() {
		InetSocketAddress address = (InetSocketAddress) this.socket.getRemoteSocketAddress();
		return address.getHostName();
	}

	/**
	 * Get the IP address of the endpoint of the TCPConnection
	 * @return String containing the address of the end point the socket in an instance
	 * of this class is connected to
	 */
	public String getRemoteIP() {
		InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
		return address.getAddress().getHostAddress();
	}

	/**
	 * Get the port of the endpoint of the TCPConnection
	 * @return int representing the port at the endpoint the socket in an instance of
	 * this class is connected to
	 */
	public int getRemotePort() {
		InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
		return address.getPort();
	}

	/**
	 * Get the hostname of where the TCPConnection started, i.e. get the local hostname
	 * to which the socket in an instance of the TCPConenction class is bound
	 * @return
	 */
	public String getLocalHostName() {
		return this.socket.getLocalAddress().getHostName();
	}

	/**
	 * Get the get the local address of where the TCPConnection started, i.e. the local
	 * address to which the TCPConnection is bound to
	 * @return String containing the local address to which the socket in an instance
	 * of this class is bound to
	 */
	public String getLocalIP() {
		return socket.getLocalAddress().getHostAddress();
	}

	/**
	 * Get the port of where the TCPConnection started, i.e. the local port to which an instance of
	 * the TCPConnections class is bound to
	 * @return the local port number to which a socket in an instance of this class is bound
	 */
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	/**
	 * Start the TCPSender
	 * @throws IOException
	 */
	public void startTCPSender() throws IOException {
		tcp_sender = new TCPSender(socket, node);
	}

	/**
	 * Start the TCPReceiver
	 * @throws IOException
	 */
	public void startTCPReceiver() throws IOException {
		tcp_receiver =  new TCPReceiverThread(socket, this.node, tcp_sender);
		new Thread( tcp_receiver ).start();
	}

	/**
	 *
	 * @param data
	 * @throws IOException
	 */
	public synchronized void send(byte[] data) throws IOException {
		tcp_sender.sendData(data);
	}
	
}
