package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class OverlayNodeReportsTaskFinishedTest extends TestCase {
    static String IP = "192.168.1.7";

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED.getValue() );
        dout.writeByte( IP.length() );
        dout.writeBytes( IP );
        dout.writeInt( 5000 );
        dout.writeInt( 55 );
        dout.flush();	// flush the stream

        data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        OverlayNodeReportsTaskFinished e = new OverlayNodeReportsTaskFinished(getMarshalledByte());
        assertEquals(e.ip_address, IP);
        assertEquals(e.port, 5000);
        assertEquals(e.nodeID, 55);
    }

    @Test
    public void testMarshalling() throws IOException {
        OverlayNodeReportsTaskFinished e = new OverlayNodeReportsTaskFinished(getMarshalledByte());
        assertArrayEquals(e.getBytes(), getMarshalledByte());
    }
}