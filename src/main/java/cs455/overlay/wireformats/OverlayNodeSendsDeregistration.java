package cs455.overlay.wireformats;

import java.io.*;

import cs455.overlay.util.Util;

public class OverlayNodeSendsDeregistration implements Event {
	public int type;
	public String ip_address;
	public int port;
	public int nodeID;

	/**
	 * Empty Constructor for when there is no marshalled byte or just want to generate marshalled byte by
	 * changing member variables
	 */
	public OverlayNodeSendsDeregistration() {}

	public OverlayNodeSendsDeregistration(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		type = (int)din.readByte();

		// get the ip string
		ip_address = Util.readString(din);

		// get the port
		port = din.readInt();

		// get node ID
		nodeID = din.readInt();

		baInputStream.close();
		din.close();
	}

	@Override
	public int getType() {
		return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION.getValue();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() ); // write the message type
		dout.writeByte( ip_address.length() ); // write the length of the ip string
		dout.writeBytes( ip_address );	// write ip string
		dout.writeInt(port);	// write port number
		dout.writeInt(nodeID);	// write the node id
		dout.flush();	// flush the stream

		marshalledBytes = baOutputStream.toByteArray();

		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void print() {
		System.out.println(this.getClass().getName());
		System.out.println("    Type: "+type);
		System.out.println("    IP Address: "+ip_address);
		System.out.println("    Port: "+port);
		System.out.println("    Node ID: "+nodeID);
	}

	public String toString() {
		return "OVERLAY_NODE_SENDS_DEREGISTRATION";
	}
}
