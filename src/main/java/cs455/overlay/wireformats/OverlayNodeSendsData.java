package cs455.overlay.wireformats;

import java.io.*;
import java.util.*;

public class OverlayNodeSendsData implements Event {
	public int destinationID;
	public int sourceID;
	public int payload;
	public ArrayList<Integer> packet_trace = new ArrayList<>();

	/**
	 * Empty Constructor for when there is no marshalled byte or just want to generate marshalled byte by
	 * changing member variables
	 */
	public OverlayNodeSendsData() {}

	public OverlayNodeSendsData(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		din.readByte();

		// get the destination ID
		destinationID = din.readInt();

		// get the source ID
		sourceID = din.readInt();

		// get payload
		payload = din.readInt();

		// get packet_trace length
		int packet_trace_length = din.readInt();

		// get packet trace
		for (int i=0; i < packet_trace_length; ++i) {
			packet_trace.add(din.readInt());
		}

		baInputStream.close();
		din.close();
	}

	/**
	 * Insert a node ID in the packet trace array
	 * @param nodeID The id of the node
	 */
	public void insertInPacketTrace(int nodeID) {
		this.packet_trace.add(nodeID);
	}

	@Override
	public int getType() {
		return Protocol.OVERLAY_NODE_SENDS_DATA.getValue();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() ); // write the message type
		dout.writeInt( destinationID );
		dout.writeInt( sourceID );
		dout.writeInt( payload );
		dout.writeInt( packet_trace.size() );
		for (int i : packet_trace)
			dout.writeInt( i );
		dout.flush();

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	public void printPacketTrace() {
		for (int i : packet_trace)
			System.out.printf(" %s |", i);
		System.out.println();
	}

	@Override
	public void print() {
		System.out.println(this.getClass().getName());
		System.out.println("    Destination ID: "+destinationID);
		System.out.println("    Source ID: "+sourceID);
		System.out.println("    Payload: "+payload);
		System.out.println("    Packet Trace Length: "+packet_trace.size());
		System.out.print("    PacketTrace: |");
		this.printPacketTrace();
	}

	public String toString() {
		return "OVERLAY_NODE_SENDS_DATA";
	}
}
