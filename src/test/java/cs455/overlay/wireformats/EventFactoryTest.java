package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class EventFactoryTest extends TestCase {

    @Test
    public void testNodeReportsOverlaySetupStatus() throws IOException {
        byte[] data = NodeReportsOverlaySetupStatusTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof NodeReportsOverlaySetupStatus;
    }

    @Test
    public void testOverlayNodeReportsTaskFinished() throws IOException {
        byte[] data = OverlayNodeReportsTaskFinishedTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof OverlayNodeReportsTaskFinished;
    }

    @Test
    public void testOverlayNodeReportsTrafficSummary() throws IOException {
        byte[] data = OverlayNodeReportsTrafficSummaryTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof OverlayNodeReportsTrafficSummary;
    }

    @Test
    public void testOverlayNodeSendsData() throws IOException {
        byte[] data = OverlayNodeSendsDataTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof OverlayNodeSendsData;
    }

    @Test
    public void testOverlayNodeSendsDeregistration() throws IOException {
        byte[] data = OverlayNodeSendsDeregistrationTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof OverlayNodeSendsDeregistration;
    }

    @Test
    public void testOverlayNodeSendsRegistration() throws IOException {
        byte[] data = OverlayNodeSendsRegistrationTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof OverlayNodeSendsRegistration;
    }

    @Test
    public void testRegistryReportsDeregistrationStatus() throws IOException {
        byte[] data = RegistryReportsDeregistrationStatusTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof RegistryReportsDeregistrationStatus;
    }

    @Test
    public void testRegistryReportsRegistrationStatus() throws IOException {
        byte[] data = RegistryReportsRegistrationStatusTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof RegistryReportsRegistrationStatus;
    }

    @Test
    public void testRegistryRequestsTaskInitiate() throws IOException {
        byte[] data = RegistryRequestsTaskInitiateTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof RegistryRequestsTaskInitiate;
    }

    @Test
    public void testRegistryRequestsTrafficSummary() throws IOException {
        byte[] data = RegistryRequestsTrafficSummaryTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof RegistryRequestsTrafficSummary;
    }

    @Test
    public void testRegistrySendsNodeManifest() throws IOException {
        byte[] data = RegistrySendsNodeManifestTest.getMarshalledByte();
        Event e = EventFactory.getInstance().createEvent(data);
        assert e instanceof RegistrySendsNodeManifest;
    }
}