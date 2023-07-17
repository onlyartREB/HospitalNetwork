package rampup;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import org.apache.commons.math3.distribution.PoissonDistribution;

public class patientGeneratorOptFnct extends Agent {
//It's not an agent, loop, look for one hospital choose one randomly 
	protected void setup() {
		System.out.println("Patient Generator: " + getLocalName());
		generatePatients();
		// Length of stay
	}

//array list of controllers
	// whats the number of hospitals ?
	// Creating...
	/*
	 * int lambda = 10; PoissonDistribution poissonDistribution = new
	 * PoissonDistribution(lambda); int randomNumber = poissonDistribution.sample();
	 */
	private void generatePatients() {
		int patientNumber = 10;
		for (int i = 1; i <= patientNumber; i++) {
			String patientAgentName = "Patient" + System.currentTimeMillis() + i;
			int hospitalIndex = (int) (Math.random() * 2); // Choose a random index for the hospital
			String hospitalAgentName = "Hospital" + hospitalIndex; // Assume the hospitals are named "Hospital0" and
																	// "Hospital1"
			sendPatientToHospital(patientAgentName, hospitalAgentName);
			System.out.println("Patient " + i + " has been sent to " + hospitalAgentName);
		}
	}

// Just a way to create, it shouldn't do anything except creating
	private void sendPatientToHospital(String patientAgentName, String hospitalAgentName) {
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.setContent(patientAgentName); // Set the content as the patient's name
		message.addReceiver(new AID(hospitalAgentName, AID.ISLOCALNAME));
		send(message);
	}
}