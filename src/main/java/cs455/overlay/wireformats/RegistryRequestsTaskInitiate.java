package cs455.overlay.wireformats;

import java.io.*;

import cs455.overlay.util.Util;

public class RegistryRequestsTaskInitiate implements Event {

	public int num_packets;

	public RegistryRequestsTaskInitiate() {}

	public RegistryRequestsTaskInitiate(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		int type = Util.readType(din);

		// get the number of packets to send
		num_packets = (int)din.readInt();

		baInputStream.close();
		din.close();
	}

	@Override
	public int getType() {
		return Protocol.REGISTRY_REQUESTS_TASK_INITIATE.getType();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() );	// write the message type
		dout.writeInt( num_packets );	// write the number of packets to send
		dout.flush();	// flush the stream

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void print() {
		System.out.println(this.getClass().getName());
		System.out.println("    Number of packets to send: "+num_packets);
	}

	public String toString() {
		return "REGISTRY_REQUESTS_TASK_INITIATE";
	}

	public static void main(String[] args) throws IOException {
		// generate the marshalled byte array
		byte[] data = null;
		int num_packets_to_send = 5;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( Protocol.REGISTRY_REQUESTS_TASK_INITIATE.getType() );
		dout.writeInt( num_packets_to_send );
		dout.flush();

		data = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		// use the event factory for testing
		EventFactory factory = EventFactory.getInstance();
		Event e = factory.createEvent(data);
		e.print();
	}

}
