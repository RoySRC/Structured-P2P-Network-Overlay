package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTrafficSummary implements Event {
	public int nodeID;							// ID of the messaging node
	public int num_sent_packets;				// total number of packets sent by the messaging node
	public int num_relayed_packets;			// total number of packets relayed
	public long sent_packet_payload_sum;		// sum of the payload in the packets that were sent
	public int num_received_packets;			// total number of packets received by the messaging node
	public long received_packet_payload_sum;	// sum of payloads in the packets that were received

	/**
	 * Empty Constructor for when there is no marshalled byte or just want to generate marshalled byte by
	 * changing member variables
	 */
	public OverlayNodeReportsTrafficSummary() {}

	public OverlayNodeReportsTrafficSummary(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		din.readByte();

		// get the node ID
		nodeID = din.readInt();

		// get the total number of packets sent. These are the packets
		// that were started/initiated by the node
		num_sent_packets = din.readInt();

		// get total number of relayed packets. These are the packets
		// received from a different node and forwarded
		num_relayed_packets = din.readInt();

		// get sum of sent packet payload. These are only the
		// ones that were started by the node.
		sent_packet_payload_sum = din.readLong();

		// get number of received packets. These are packets that
		// have the current messaging node ID as the final destination
		num_received_packets = din.readInt();

		// get the sum of received packet payload. These are packets
		// that have the current messaging node ID as the final destination
		received_packet_payload_sum = din.readLong();

		baInputStream.close();
		din.close();
	}

	@Override
	public int getType() {
		return Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY.getValue();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() ); // write the message type
		dout.writeInt( nodeID );
		dout.writeInt( num_sent_packets );
		dout.writeInt( num_relayed_packets );
		dout.writeLong( sent_packet_payload_sum );
		dout.writeInt( num_received_packets );
		dout.writeLong( received_packet_payload_sum );
		dout.flush();	// flush the stream

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void print() {
		System.out.println(this.getClass().getName());
		System.out.println("    Node ID: "+nodeID);
		System.out.println("    Number of sent packets: "+num_sent_packets);
		System.out.println("    Number of relayed packets: "+num_relayed_packets);
		System.out.println("    Sum of sent packet payload: "+sent_packet_payload_sum);
		System.out.println("    Number of received packets: "+num_received_packets);
		System.out.println("    Sum of received packet payload: "+received_packet_payload_sum);
	}

	public String toString() {
		return "OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY";
	}
}
