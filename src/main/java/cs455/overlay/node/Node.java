package cs455.overlay.node;

import java.io.*;
import java.net.Socket;
import java.util.*;
import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;

public interface Node {

	public void onEvent(Event e, String IpPort) throws Exception;
	public void startServerSocket(int port_num)  throws IOException;
	public void parseArguments() throws Exception;

	// Error handlers for TCP Connections
	public void tcpReceiverErrorHandler(TCPConnection connection);
	public void tcpSenderErrorHandler(TCPConnection connection);

	/**
	 *
	 * @param connection
	 * @throws IOException
	 */
	public void addTCPConnection(TCPConnection connection) throws IOException;
	
}
