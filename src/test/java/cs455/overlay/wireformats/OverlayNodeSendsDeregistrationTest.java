package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class OverlayNodeSendsDeregistrationTest extends TestCase {
    static String IP = "192.168.1.7";
    static int port = 5000;
    static int nodeID = 55;

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION.getValue() ); // write the message type
        dout.writeByte( IP.length() ); // write the length of the ip string
        dout.writeBytes( IP );	// write ip string
        dout.writeInt(port);	// write port number
        dout.writeInt(nodeID);
        dout.flush();	// flush the stream

        data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        OverlayNodeSendsDeregistration e = new OverlayNodeSendsDeregistration(getMarshalledByte());
        assertEquals(e.ip_address, IP);
        assertEquals(e.port, port);
        assertEquals(e.nodeID, nodeID);
    }

    @Test
    public void testMarshalling() throws IOException {
        OverlayNodeSendsDeregistration e = new OverlayNodeSendsDeregistration(getMarshalledByte());
        assertArrayEquals(e.getBytes(), getMarshalledByte());
    }
}