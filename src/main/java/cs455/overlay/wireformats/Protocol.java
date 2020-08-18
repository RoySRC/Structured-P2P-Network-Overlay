package cs455.overlay.wireformats;

import java.io.IOException;

public enum Protocol {
	
	OVERLAY_NODE_SENDS_REGISTRATION (2) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new OverlayNodeSendsRegistration(marshalledBytes);
		}
	},
	REGISTRY_REPORTS_REGISTRATION_STATUS (3) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new RegistryReportsRegistrationStatus(marshalledBytes);
		}
	},
	OVERLAY_NODE_SENDS_DEREGISTRATION 	(4) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new OverlayNodeSendsDeregistration(marshalledBytes);
		}
	},
	REGISTRY_REPORTS_DEREGISTRATION_STATUS (5) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new RegistryReportsDeregistrationStatus(marshalledBytes);
		}
	},
	REGISTRY_SENDS_NODE_MANIFEST (6) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new RegistrySendsNodeManifest(marshalledBytes);
		}
	},
	NODE_REPORTS_OVERLAY_SETUP_STATUS (7) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new NodeReportsOverlaySetupStatus(marshalledBytes);
		}
	},
	REGISTRY_REQUESTS_TASK_INITIATE (8) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new RegistryRequestsTaskInitiate(marshalledBytes);
		}
	},
	OVERLAY_NODE_SENDS_DATA (9) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new OverlayNodeSendsData(marshalledBytes);
		}
	},
	OVERLAY_NODE_REPORTS_TASK_FINISHED (10) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new OverlayNodeReportsTaskFinished(marshalledBytes);
		}
	},
	REGISTRY_REQUESTS_TRAFFIC_SUMMARY (11) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new RegistryRequestsTrafficSummary(marshalledBytes);
		}
	},
	OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY (12) {
		@Override
		public Event getEventObject(byte[] marshalledBytes) throws IOException {
			return new OverlayNodeReportsTrafficSummary(marshalledBytes);
		}
	};

	private int id;

	Protocol(int id) {
		this.id = id;
	}

	public int getType() {
		return id;
	}

	public abstract Event getEventObject(byte[] marshalledBytes) throws IOException;

}
