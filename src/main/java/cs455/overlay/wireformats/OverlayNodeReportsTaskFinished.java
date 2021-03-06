package cs455.overlay.wireformats;

import cs455.overlay.util.Util;

import java.io.*;

public class OverlayNodeReportsTaskFinished implements Event {
	public String ip_address;
	public int port;
	public int nodeID;

	/**
	 * Empty Constructor for when there is no marshalled byte or just want to generate marshalled byte by
	 * changing member variables
	 */
	public OverlayNodeReportsTaskFinished() {}

	public OverlayNodeReportsTaskFinished(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		din.readByte();

		// get the IP address of the node
		ip_address = Util.readString(din);

		// get the port
		port = din.readInt();

		// get the node ID
		nodeID = din.readInt();

		baInputStream.close();
		din.close();
	}

	@Override
	public int getType() {
		return Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED.getValue();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() ); // write the message type
		dout.writeByte( ip_address.length() );	// write the length of the ip string
		dout.writeBytes( ip_address );	// write ip string
		dout.writeInt( port ); // write the port number
		dout.writeInt( nodeID );	// write node ID
		dout.flush();	// flush the stream

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void print() {
		System.out.println(this.getClass().getName());
		System.out.println(OverlayNodeSendsDeregistration.IP_ADDRESS +ip_address);
		System.out.println("    Port: "+port);
		System.out.println("    Node ID: "+nodeID);
	}

	public String toString() {
		return "OVERLAY_NODE_REPORTS_TASK_FINISHED";
	}
}
