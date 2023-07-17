package rampup;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class Hospital extends Agent {
	private int capacity = 20; // Maximum number of patients the hospital can handle
	private int activePatients = 0; // Number of currently active patients
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
		// patientLists.add(new ArrayList<>()); // List for Hospital0
		// Each one there own list, they don't share their list
		// Make it more simple
		// Every time ou get a new tick, you add patient to this list

		// patientLists.add(new ArrayList<>()); // List for Hospital1
	}

	public void decrementActivePatients() {
		activePatients--;
	}

	/*
	 * public List<String> getPatientList() { return patientList; } public void
	 * printPatients() { int hospitalIndex =
	 * Integer.parseInt(getLocalName().replaceAll("\\D+", "")); // Extract the
	 * hospital index from the agent name List<String> patients =
	 * patientLists.get(hospitalIndex); System.out.println("Patients in " +
	 * getLocalName() + ":"); for (String patient : patients) {
	 * System.out.println(patient); } }
	 */

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
				// All the decision here
				// Is there capacity ?
				// Accept or reject
				if (activePatients < capacity) {
					ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
					// Reply.setContent(patientAgentName); // Set the content as the patient's name
					reply.addReceiver(message.getSender());
					send(reply);
					activePatients++;

				} else {
					ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
					// Reply.setContent(patientAgentName); // Set the content as the patient's name
					reply.addReceiver(message.getSender());
					send(reply);

				}
				message = receive();
			} /*
				 * if (activePatients < capacity && !patientList.isEmpty()) {
				 * 
				 * String patientAgentName = patientList.remove(0); printPatients(); int
				 * hospitalIndex = Integer.parseInt(getLocalName().replaceAll("\\D+", "")); for
				 * (String receivedPatient : receivedPatients) {
				 * patientLists.get(hospitalIndex).add(receivedPatient); }
				 * receivedPatients.clear(); // Clear the temporary list
				 * 
				 * // Print the patients accueillis par chaque hôpital printPatients();
				 * 
				 * }
				 */

		}
	}
}
