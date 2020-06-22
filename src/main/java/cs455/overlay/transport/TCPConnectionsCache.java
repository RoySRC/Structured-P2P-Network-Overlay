package cs455.overlay.transport;

import cs455.overlay.util.LOGGER;
import cs455.overlay.util.SortedArrayList;

import java.util.*;

public class TCPConnectionsCache {

	// for logging
	private static final LOGGER log = new LOGGER(TCPConnectionsCache.class.getSimpleName());

	/**
	 * String is of the format: IP:Port
	 * Map a IP:Port String to its TCP Connection
	 */
	private HashMap<String, TCPConnection> StringConnectionMap = null;
	
	/**
	 * Maps a node ID to its TCP Connection
	 */
	private HashMap<Integer, TCPConnection> NodeIDConnectionMap = null;

	/**
	 * Map an IP:Port String to node ID
	 */
	private HashMap<String, Integer> StringNodeIDMap = null;

	/**
	 * Store the node ID when a connection is assigned a node ID
	 */
	private SortedArrayList<Integer> nodeIDs = null;

	/**
	 * Constructor does nothing
	 */
	public TCPConnectionsCache() {
		StringConnectionMap = new HashMap<>();
		NodeIDConnectionMap = new HashMap<>();
		StringNodeIDMap = new HashMap<>();
		nodeIDs = new SortedArrayList<>();
	}

	/**
	 * Assign an ID to an existing connection
	 * @param id
	 * @param connection
	 */
	public synchronized void assign(int id, TCPConnection connection) {
		log.info("Assigning node "+id+" to "+connection.toString());
		StringConnectionMap.remove(connection.getRemoteAddressPortPair());
		StringNodeIDMap.put(connection.getRemoteAddressPortPair(), id);
		NodeIDConnectionMap.put(id, connection);
		nodeIDs.insert(id);
	}

  /**
   * The following function removes the IP port pair with its nodeID, and the nodeID
   * with the {@link TCPConnection}
   * @param connection
   */
	public synchronized void unassign(TCPConnection connection) {
	  int nodeID = -1;
	  if (this.contains(connection.getRemoteAddressPortPair())) {
	    nodeID = this.getNodeID(connection.getRemoteAddressPortPair());
			this.StringNodeIDMap.remove(connection.getRemoteAddressPortPair());
			this.NodeIDConnectionMap.remove(nodeID);
      this.nodeIDs.remove((Integer)nodeID);
    }
  }


	/**
	 * Add Section
	 */

	public synchronized void add(String AddressPortPair, TCPConnection conn) {
		StringConnectionMap.put(AddressPortPair, conn);
	}


	/**
	 * Getters Section
	 */

	public int getNodeID(int index) {
		return nodeIDs.get(index);
	}

	public int getNodeID(String AddressPortPair) {
		return StringNodeIDMap.get(AddressPortPair);
	}

	public TCPConnection getTCPConnection(String AddressPortPair) {
		if (StringNodeIDMap.containsKey(AddressPortPair))
	  	return NodeIDConnectionMap.get(StringNodeIDMap.get(AddressPortPair));
		else
			return StringConnectionMap.get(AddressPortPair);
  }

  public TCPConnection getTCPConnection(int nodeID) {
	  return NodeIDConnectionMap.get(nodeID);
  }

	public ArrayList<Integer> getNodeIDs() {
		return this.nodeIDs;
	}


	/**
	 * Remove Section
	 */

	/**
	 * Remove a <AddressPortPair, TCPConnection> pair from the String connection map
	 * @param IpNodePair
	 */
	public synchronized void remove(String IpNodePair) {
		StringConnectionMap.remove(IpNodePair);
  }


	/**
	 * Contains Section
	 */

  /**
   * Check if the given node ID is present in the cache
   * @param nodeID
   * @return
   */
	public boolean contains(int nodeID) {
	  return NodeIDConnectionMap.containsKey(nodeID);
  }

	/**
	 * Check if a node ID exists in the cache with the given port.
	 * @param AddressPort
	 * @return
	 */
  public boolean contains(String AddressPort) {
		return StringNodeIDMap.containsKey(AddressPort);
	}


	/**
	 * Get size section
	 */

	/**
	 * Get the number of elements in StringConnectionMap
	 * @return
	 */
	public int StringConnectionMapSize() { return StringConnectionMap.size(); }

	/**
	 * Get the number of elements in NodeIDConnectionMap
	 * @return
	 */
	public int NodeIDConnectionMapSize() { return NodeIDConnectionMap.size(); }

	/**
	 * get the number of nodes in the connection cache
	 * @return
	 */
	public int size() { return this.nodeIDs.size(); }


}
