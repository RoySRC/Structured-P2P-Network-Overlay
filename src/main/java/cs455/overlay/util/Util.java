package cs455.overlay.util;

import java.io.*;
import java.net.*;
import java.util.*;

import cs455.overlay.node.RegistryNode;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;

public class Util {

	// for logging
	private static final LOGGER log = new LOGGER(Util.class.getSimpleName());

	// maximum number of nodes in the system
	public static final int MAX_NODES = 128;

	/**
	 * Generate a random integer within [min, max]
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	/**
	 * Return a random integer
	 * @return
	 */
	public static int randInt() {
		return new Random().nextInt();
	}

	/**
	 * Randomly choose and return an element from an array
	 * @param arr
	 * @return
	 */
	public static int randChoice(ArrayList<Integer> arr) {
		if (arr.size() == 0) {
			log.error("randChoice() Received empty array list");
			System.exit(-1);
		}
		int idx = randInt(0, arr.size()-1);
		if (idx < 0 || idx > arr.size()-1) {
			log.error("Index: "+idx);
			System.exit(-1);
		}
		return arr.get(idx);
	}
	
	/**
	 * Generate random IP
	 * @return Returns the generated random IP string
	 */
	public static String generateRandomIP() {
		String g1 = Integer.toString(Util.randInt(0, 256));
		String g2 = Integer.toString(Util.randInt(0, 256));
		String g3 = Integer.toString(Util.randInt(0, 256));
		String g4 = Integer.toString(Util.randInt(0, 256));
		return (g1+"."+g2+"."+g3+"."+g4);
	}
	
	/**
	 * Read a string from the DataInputStream
	 * @param din DataInputStream
	 * @return the string read from the DataInputStream
	 * @throws IOException thrown by DataInputStream
	 */
	public static String readString(DataInputStream din) throws IOException {
		int dataLength = din.readByte();	// read the length of the string field
		byte[] data = new byte[dataLength];	// allocate byte array whose size is dataLength
		din.readFully(data);	// populate the data byte array
		return new String(data);	// return a new string with the populated byte array
	}
	
	/**
	 * Read a byte from the {@link DataInputStream} and return an integer cast of it.
	 * @param din
	 * @throws IOException
	 */
	public static int readType(DataInputStream din) throws IOException {
		return (int)din.readByte();
	}
	
	/**
	 * return the node ID of the node in the routing table 
	 * that does not overshoot the destination node
	 * <p></p>
	 * @param routingTable is the routing table associated with 
	 * 			a messaging node
	 * @param dstNodeID is the destination node for the message
	 * @return the node ID of the node from the routing table
	 * 			that does not overshoot the destination node
	 */
	public static int getNextNode(RoutingTable routingTable, int dstNodeID) {

		int nodeID = -1;

		int lowestPositiveDeltaValue = Integer.MAX_VALUE;
		for (RoutingEntry e : routingTable.entry) {
			int delta = dstNodeID-e.nodeID;
			// If the current node in the routing table is closer to the
			// destination node in the clockwise direction
			if (delta >= 0 && delta < lowestPositiveDeltaValue) {
				nodeID = e.nodeID;
				lowestPositiveDeltaValue = delta;
			}
		}
		if (nodeID > -1)
			return nodeID;

		// if there is no entry in the routing table whose nodeID is smaller than
		// or equal to the node ID of the destination node, i.e. a node that is
		// closer to the destination node in the clockwise direction, then find the
		// node ID that is closest to the destination node in the counter clockwise
		// direction. This is equivalent to finding the node with the greatest ID
		// value in the routing table.
		int previousNodeID = Integer.MIN_VALUE;
		for (RoutingEntry e : routingTable.entry) {
			if (e.nodeID > previousNodeID)
				previousNodeID = e.nodeID;
		}
		return previousNodeID;

	}

	/**
	 *
	 * @param socket
	 * @return
	 */
	public static String getRemoteAddressPortPair(Socket socket) {
		InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
		return address.getAddress().getHostAddress()+":"+address.getPort();
	}

}
