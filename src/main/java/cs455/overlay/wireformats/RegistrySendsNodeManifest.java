package cs455.overlay.wireformats;

import java.io.*;
import java.util.ArrayList;

import cs455.overlay.util.Util;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;

public class RegistrySendsNodeManifest implements Event {

	public RoutingTable routing_table = null;	// routing table associated with a node
	public ArrayList<Integer> node_IDs = new ArrayList<>();	// node IDs of all the nodes in the system

	public RegistrySendsNodeManifest() {}
	
	public RegistrySendsNodeManifest(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		int type = (int)din.readByte();

		// get the routing table size
		int routing_table_size = (int)din.readByte();

		// create the routing table
		routing_table = new RoutingTable(routing_table_size);

		// create routing table entries
		for (int i=0; i < routing_table.length(); ++i) {
			int nodeID = din.readInt();

			// get the IP address
			String IP = Util.readString(din);

			// get the port number of node
			int port = din.readInt();

			// get the distance
			int distance = (1 << i);

			// add a new routing table entry
			routing_table.entry[i].fill(distance, nodeID, IP, port);
		}

		// get the ID of all the nodes in the system
		int num_nodes = din.readInt();
		for (int i=0; i < num_nodes; ++i) {
			node_IDs.add(din.readInt());
		}

		baInputStream.close();
		din.close();
	}

	@Override
	public int getType() {
		return Protocol.REGISTRY_SENDS_NODE_MANIFEST.getType();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() );
		dout.writeByte( routing_table.length() );
		for (RoutingEntry entry : routing_table.entry) {	// write each entry of the routing table to dout
			dout.writeInt(entry.nodeID);	// node ID
			dout.writeByte(entry.IP.length());	// length of ip address of node
			dout.writeBytes(entry.IP);	// IP address of node
			dout.writeInt(entry.port);	// port number of node
		}
		dout.writeInt(node_IDs.size());	// write the number of node IDs in the system
		for (int id : node_IDs)	// write ID of each node to dout stream
			dout.writeInt(id);
		dout.flush();	// flush the stream


		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void print() {
		System.out.println(this.getClass().getName());
		System.out.println("Routing Table:");
		routing_table.print();
		System.out.println("Number of nodes in the system: "+node_IDs.size());
		System.out.printf("Node IDs: ");
		for (int id : node_IDs) System.out.printf("%s ", id);
		System.out.println();
	}

	public String toString() {
		return "REGISTRY_SENDS_NODE_MANIFEST";
	}

	public static void main (String[] args) throws IOException {
		// generate the marshalled byte array
		byte[] data = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		int routing_size = 12;
		dout.writeByte( Protocol.REGISTRY_SENDS_NODE_MANIFEST.getType() );
		dout.writeByte( routing_size );

		for (int i=0; i < routing_size; ++i) {
			dout.writeInt( Util.randInt(0, Util.MAX_NODES-1) );	// node ID
			String IP = Util.generateRandomIP();
			dout.writeByte( IP.length() );	// length of the IP address
			dout.writeBytes( IP );	// IP address
			dout.writeInt( Util.randInt(1025, 5000) );	// port number
		}
		int num_nodes = 10;	// number of nodes in the system
		dout.writeInt(num_nodes);
		for (int i=0; i<num_nodes; ++i)
			dout.writeInt( Util.randInt(0, Util.MAX_NODES-1) );
		dout.flush();	// flush the stream

		data = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		// use the event factory for testing
		EventFactory factory = EventFactory.getInstance();
		Event e = factory.createEvent(data);
		e.print();
	}


}
