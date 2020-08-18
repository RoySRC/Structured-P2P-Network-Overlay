package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTrafficSummary implements Event {
	public RegistryRequestsTrafficSummary() {}
	public RegistryRequestsTrafficSummary(byte[] marshalledBytes) {}

	@Override
	public int getType() {
		return Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY.getValue();
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
}
