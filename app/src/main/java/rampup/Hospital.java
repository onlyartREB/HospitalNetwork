package rampup;
	
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;


public class Hospital extends Agent {
	private int capacity = 20; // Maximum number of patients the hospital can handle
	private int activePatients = 0; // Number of currently active patients
    private List<String> patientList; // Just one list to store the names of admitted patients

	
	/*
	 * private List<List<String>> patientLists = new ArrayList<>(); // Separate
	 * patient lists for each hospital private List<String> patientList = new
	 * ArrayList<>(); private List<String> receivedPatients = new ArrayList<>(); //
	 * Temporary list to store received patients
	 */
// One list

	protected void setup() {
		System.out.println("Hospital: " + getLocalName());
		addBehaviour(new Gestion(this, 1000));
	}

	public void decrementActivePatients() {
		activePatients--;
	}
    public void addPatient(String patientName) {
        if (activePatients < capacity) {
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setContent(patientName);
            message.addReceiver(getAID());
            send(message);
            activePatients++;
            patientList.add(patientName);
        } else {
            System.out.println("Hospital is at full capacity. Cannot admit patient: " + patientName);
        }
    }



	public class Gestion extends TickerBehaviour {
		public Gestion(Agent ag, int period) {
			super(ag, period);
		}

		protected void onTick() {
			ACLMessage message = receive();
			while (message != null) {
				String patientAgentName = message.getContent();
				System.out.println("Received patient: " + patientAgentName);
				// receivedPatients.add(patientAgentName); // Store the received patient in the
				// temporary list
				if (activePatients < capacity) {
					ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
					// Reply.setContent(patientAgentName); // Set the content as the patient's name
					reply.addReceiver(message.getSender());
					send(reply);
					activePatients++;
                    patientList.add(patientAgentName);


				} else {
					ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
					// Reply.setContent(patientAgentName); // Set the content as the patient's name
					reply.addReceiver(message.getSender());
					send(reply);

				}
				message = receive();
			} 

		}
	}
}
