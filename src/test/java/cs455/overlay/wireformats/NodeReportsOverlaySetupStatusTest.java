package cs455.overlay.wireformats;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NodeReportsOverlaySetupStatusTest {

    public static byte[] getMarshalledByte() throws IOException {
        String informationString = "Successful";

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS.getValue() );
        dout.writeInt( 10 );
        dout.writeByte( informationString.length() );
        dout.writeBytes( informationString );
        dout.flush();

        byte[] data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        NodeReportsOverlaySetupStatus e = new NodeReportsOverlaySetupStatus(getMarshalledByte());
        assertEquals(e.successStatus, 10);
        assertEquals(e.informationString, "Successful");
    }

    @Test
    public void testMarshalling() throws IOException {
        NodeReportsOverlaySetupStatus e = new NodeReportsOverlaySetupStatus(getMarshalledByte());
        assertArrayEquals(e.getBytes(), getMarshalledByte());
    }

}