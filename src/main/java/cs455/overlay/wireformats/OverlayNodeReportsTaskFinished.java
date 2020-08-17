package cs455.overlay.wireformats;

import cs455.overlay.util.Util;

import java.io.*;

public class OverlayNodeReportsTaskFinished implements Event {

	public String ip_address;
	public int port;
	public int nodeID;

	public OverlayNodeReportsTaskFinished() {}

	public OverlayNodeReportsTaskFinished(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		int type = (int)din.readByte();

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
		return Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED.getType();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;

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
		System.out.println("    IP Address: "+ip_address);
		System.out.println("    Port: "+port);
		System.out.println("    Node ID: "+nodeID);
	}

	public String toString() {
		return "OVERLAY_NODE_REPORTS_TASK_FINISHED";
	}

	public static void main(String[] args) throws IOException {
		// generate the marshalled byte array
		byte[] data = null;

		String IP = "192.168.1.7";

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED.getType() );
		dout.writeByte( IP.length() );
		dout.writeBytes( IP );
		dout.writeInt( 5000 );
		dout.writeInt( 55 );
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
