package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;

public class OverlayNodeSendsDataTest extends TestCase {
    static ArrayList<Integer> packet_trace;

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data = null;

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.OVERLAY_NODE_SENDS_DATA.getValue() );
        dout.writeInt( 10 );
        dout.writeInt( 0 );
        dout.writeInt( 3724920 );
        dout.writeInt( 512 );

        packet_trace = new ArrayList<>();
        for (int i=0; i < 512; ++i) {
            dout.writeInt( i );
            packet_trace.add(i);
        }
        dout.flush();	// flush the stream

        data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        OverlayNodeSendsData e = new OverlayNodeSendsData(getMarshalledByte());
        assertEquals(e.destinationID, 10);
        assertEquals(e.sourceID, 0);
        assertEquals(e.payload, 3724920);
        assertEquals(e.packet_trace, packet_trace);
    }

    @Test
    public void testMarshalling() throws IOException {
        OverlayNodeSendsData e = new OverlayNodeSendsData(getMarshalledByte());
        assertArrayEquals(e.getBytes(), getMarshalledByte());
    }
}