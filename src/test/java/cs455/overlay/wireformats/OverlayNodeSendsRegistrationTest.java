package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class OverlayNodeSendsRegistrationTest extends TestCase {
    static String IP = "192.168.1.7";

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data = null;

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.OVERLAY_NODE_SENDS_REGISTRATION.getValue() ); // write the message type
        dout.writeByte( IP.length() ); // write the length of the ip string
        dout.writeBytes( IP );	// write ip string
        dout.writeInt(5000);	// write port number
        dout.flush();	// flush the stream

        data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        OverlayNodeSendsRegistration e = new OverlayNodeSendsRegistration(getMarshalledByte());
        assertEquals(e.ip_address, IP);
        assertEquals(e.port, 5000);
        assertEquals(e.type, Protocol.OVERLAY_NODE_SENDS_REGISTRATION.getValue());
    }

    @Test
    public void testMarshalling() throws IOException {
        OverlayNodeSendsRegistration e = new OverlayNodeSendsRegistration(getMarshalledByte());
        assertArrayEquals(e.getBytes(), getMarshalledByte());
    }
}