package cs455.overlay.wireformats;

import java.io.*;

import cs455.overlay.util.Util;

public class RegistryReportsRegistrationStatus implements Event {
	public int successStatus;
	public String informationString;

	/**
	 * Empty Constructor for when there is no marshalled byte or just want to generate marshalled byte by
	 * changing member variables
	 */
	public RegistryReportsRegistrationStatus() {}
	
	public RegistryReportsRegistrationStatus(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		din.readByte();

		// get success status
		successStatus = din.readInt();

		// get the information string
		informationString = Util.readString(din);

		baInputStream.close();
		din.close();
	}

	@Override
	public int getType() {
		return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS.getValue();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes;

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
		System.out.println(NodeReportsOverlaySetupStatus.SUCCESS_STATUS +successStatus);
		System.out.println(NodeReportsOverlaySetupStatus.INFORMATION_STRING +informationString);
	}

	public String toString() {
		return "REGISTRY_REPORTS_REGISTRATION_STATUS";
	}
}
