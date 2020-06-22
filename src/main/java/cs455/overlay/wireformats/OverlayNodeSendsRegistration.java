package cs455.overlay.wireformats;

import java.io.*;

import cs455.overlay.util.Util;

public class OverlayNodeSendsRegistration implements Event, Protocol{

	public String ip_address; // ip address of the client running on the messaging node
	public int port; // this is the port of the server running on the messaging node
	public int type;

	public OverlayNodeSendsRegistration() {}
	
	/**
	 * Read marshalled bytes and populate private member variables
	 * @param marshalledBytes
	 * @throws IOException
	 */
	public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		type = (int)din.readByte();

		// get the ip string
		ip_address = Util.readString(din);

		// get the port
		port = din.readInt();

		baInputStream.close();
		din.close();
	}

	public void print() {
		System.out.println(this.getClass().getName());
		System.out.println("    Type: "+type);
		System.out.println("    IP Address: "+ip_address);
		System.out.println("    Port: "+port);
	}

	@Override
	public int getType() {
		return Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
	}

	/**
	 * get the marshalled bytes
	 * @throws IOException
	 */
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() ); // write the message type
		dout.writeByte( ip_address.length() ); // write the length of the ip string
		dout.writeBytes( ip_address );	// write ip string
		dout.writeInt(port);	// write port number
		dout.flush();	// flush the stream

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	public String toString() {
		return "OVERLAY_NODE_SENDS_REGISTRATION";
	}

	public static void main(String args[]) throws IOException {
		// generate the marshalled byte array
		byte[] data = null;
		String IP = "192.168.1.7";

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( Protocol.OVERLAY_NODE_SENDS_REGISTRATION ); // write the message type
		dout.writeByte( IP.length() ); // write the length of the ip string
		dout.writeBytes( IP );	// write ip string
		dout.writeInt(5000);	// write port number
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
