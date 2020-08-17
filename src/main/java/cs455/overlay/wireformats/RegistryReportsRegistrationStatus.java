package cs455.overlay.wireformats;

import java.io.*;

import cs455.overlay.util.Util;

public class RegistryReportsRegistrationStatus implements Event {

	public int successStatus;
	public String informationString;

	public RegistryReportsRegistrationStatus() {}
	
	public RegistryReportsRegistrationStatus(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		int type = (int)din.readByte();

		// get success status
		successStatus = (int)din.readInt();

		// get the information string
		informationString = Util.readString(din);

		baInputStream.close();
		din.close();
	}

	@Override
	public int getType() {
		return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS.getType();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( getType() );	// write the message type
		dout.writeInt(successStatus);	// write success status
		dout.writeByte(informationString.length());	// write length of information string
		dout.writeBytes(informationString);	// write information string
		dout.flush();

		marshalledBytes = baOutputStream.toByteArray();

		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void print() {
		System.out.println(this.getClass().getName());
		System.out.println("    Success Status: "+successStatus);
		System.out.println("    Information String: "+informationString);
	}

	public String toString() {
		return "REGISTRY_REPORTS_REGISTRATION_STATUS";
	}

	public static void main(String args[]) throws IOException {
		// generate the marshalled byte array
		byte[] data = null;
		String informationString = "Registration request " +
				"successful. The number of messaging nodes currently constituting the overlay is (5)";

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS.getType() ); // write the message type
		dout.writeInt( 55 ); // write the success status
		dout.writeByte( informationString.length() );	// write length of informationString
		dout.writeBytes(informationString);	// write the information string
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
