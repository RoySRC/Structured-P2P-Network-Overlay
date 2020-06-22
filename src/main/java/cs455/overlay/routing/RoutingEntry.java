package cs455.overlay.routing;

public class RoutingEntry {

	public int distance;
	public int nodeID;
	public String IP;
	public int port;
	
	public RoutingEntry() {}
	
	public RoutingEntry(int distance, int nodeID, String IP, int port) {
		this.distance = distance;
		this.nodeID = nodeID;
		this.IP = IP;
		this.port = port;
	}
	
	public void fill(int distance, int nodeID, String IP, int port) {
		this.distance = distance;
		this.nodeID = nodeID;
		this.IP = IP;
		this.port = port;
	}
	
}
