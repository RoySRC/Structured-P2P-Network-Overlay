package cs455.overlay.wireformats;

import java.io.*;
import cs455.overlay.util.Util;

public class NodeReportsOverlaySetupStatus implements Event {

	public int successStatus;
	public String informationString = null;
	
	public NodeReportsOverlaySetupStatus() {}
	
	public NodeReportsOverlaySetupStatus(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		// get the message type
		int type = Util.readType(din);
		
		// get the success status
		successStatus = din.readInt();
		
		// read the information string
		informationString = Util.readString(din);
		
		baInputStream.close();
		din.close();
	}
	
	@Override
	public int getType() {
		return Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS.getType();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte( getType() ); // write the message type
		dout.writeInt( successStatus );	// write the success status
		dout.writeByte( informationString.length() );	// write the length of information string
		dout.writeBytes( informationString ); // write the information string
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
		return "NODE_REPORTS_OVERLAY_SETUP_STATUS";
	}
	
	public static void main(String[] args) throws IOException {
		// generate the marshalled byte array
		byte[] data = null;
		
		String informationString = "Successful";
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeByte( Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS.getType() );
		dout.writeInt( 10 );
		dout.writeByte( informationString.length() );
		dout.writeBytes( informationString );
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
