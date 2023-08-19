package rampup;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

import java.util.Random;

public class Patient extends Agent {
	private Zone patientZone; // The zone of the patient

	protected void setup() {
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			patientZone = (Zone) args[0]; // Get the patient's zone from the arguments
			chooseHospital(); // Send a request message to find a hospital
		}

		addBehaviour(new LifeBehaviour());
	}

	private void chooseHospital() {
		HospitalData chosenHospital = null;
		boolean accepted = false;

		for (HospitalData hospital : patientZone.getHospitals()) {
			chosenHospital = hospital;
			System.out.println("Patient " + getLocalName() + " in zone " + patientZone.getZoneIndex()
					+ " chose Hospital " + chosenHospital.getName());

			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setContent(getLocalName()); // Send the patient's name as content
			message.addReceiver(new AID(chosenHospital.getName(), AID.ISLOCALNAME));
			send(message);

			ACLMessage reply;
			do {
				reply = blockingReceive(0); // Check for incoming messages with zero timeout
			} while (reply == null);

			if (reply.getPerformative() == ACLMessage.AGREE) {
				accepted = true;
				break; // Exit the loop since the patient is accepted by a hospital
			}
		}

		if (!accepted && chosenHospital != null) {
			// The patient was not accepted by any hospital, try the special hospital
			String specialHospitalName = "SpecialHospital"; // Change this to the actual name of the special hospital.
			System.out.println("Patient " + getLocalName() + " in zone " + patientZone.getZoneIndex()
					+ " chose the Special Hospital.");
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setContent(getLocalName()); // Send the patient's name as content
			message.addReceiver(new AID(specialHospitalName, AID.ISLOCALNAME));
			send(message);
		}
	}

	private class LifeBehaviour extends CyclicBehaviour {
		public void action() {
			// Implement patient's daily activities here
		}
	}
}