package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class RegistryReportsRegistrationStatusTest extends TestCase {
    static String informationString = "Registration request " +
            "successful. The number of messaging nodes currently constituting the overlay is (5)";

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS.getValue() ); // write the message type
        dout.writeInt( 55 ); // write the success status
        dout.writeByte( informationString.length() );	// write length of informationString
        dout.writeBytes(informationString);	// write the information string
        dout.flush();	// flush the stream

        data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        RegistryReportsRegistrationStatus e = new RegistryReportsRegistrationStatus(getMarshalledByte());
        assertEquals(e.informationString, informationString);
        assertEquals(e.successStatus, 55);
    }

    @Test
    public void testMarshalling() throws IOException {
        RegistryReportsRegistrationStatus e = new RegistryReportsRegistrationStatus(getMarshalledByte());
        assertArrayEquals(e.getBytes(), getMarshalledByte());
    }
}