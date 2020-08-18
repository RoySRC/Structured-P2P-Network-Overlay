package cs455.overlay.wireformats;

import java.io.*;

import cs455.overlay.util.Util;

public class RegistryRequestsTaskInitiate implements Event {
	public int num_packets;

	/**
	 * Empty Constructor for when there is no marshalled byte or just want to generate marshalled byte by
	 * changing member variables
	 */
	public RegistryRequestsTaskInitiate() {}

	public RegistryRequestsTaskInitiate(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		Util.readType(din);

		// get the number of packets to send
		num_packets = din.readInt();

		baInputStream.close();
		din.close();
	}

	@Override
	public int getType() {
		return Protocol.REGISTRY_REQUESTS_TASK_INITIATE.getValue();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes;

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
}
