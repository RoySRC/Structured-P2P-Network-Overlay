package cs455.overlay.routing;

public class RoutingTable {

	public RoutingEntry[] entry;
	
	public RoutingTable(int tableSize) {
		entry = new RoutingEntry[tableSize];
		for (int i=0; i < tableSize; ++i)
			entry[i] = new RoutingEntry();
	}
	
	public int length() {return entry.length;}

	/**
	 * Return true if the routing table contains the specified nodeID
	 * @param nodeID is the ID of the node to search for in the routing table
	 * @return true if the nodeID was found in the routing table, false otherwise
	 */
	public boolean containsNodeID(int nodeID) {
		for (RoutingEntry e : entry) {
			if (e.nodeID == nodeID) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * print the routing table
	 */
	public void print() {
		System.out.printf("\u001B[47m \u001B[30m");
		System.out.printf("%-12s %-16s %-15s %s\u001B[0m\n", "Distance", "NodeID", "IP", "Port");
		for (RoutingEntry e : entry) {
			System.out.printf(" %7s %10s %19s %11s", e.distance, e.nodeID, e.IP, e.port);
			System.out.println();
		}
	}
	
}
