package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTrafficSummary implements Event, Protocol {

	public RegistryRequestsTrafficSummary() {}
	public RegistryRequestsTrafficSummary(byte[] marshalledBytes) {}

	@Override
	public int getType() {
		return Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() ); // write the message type
		dout.flush();	// flush the stream

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void print() {
		System.out.println(this.getClass().getName());
	}

	public String toString() {
		return "REGISTRY_REQUESTS_TRAFFIC_SUMMARY";
	}

	public static void main(String[] args) throws IOException {
		// generate the marshalled byte array
		byte[] data = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY ); // write the message type
		dout.writeInt( 55 );
		dout.writeInt( 1200 );
		dout.writeInt( 1300 );
		dout.writeLong( 1400 );
		dout.writeInt( 1500 );
		dout.writeLong( 1600 );
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
