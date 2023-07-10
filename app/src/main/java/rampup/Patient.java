package rampup;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID; 
//import java.lang.Math;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
public class Patient extends Agent {
	// Steps
	// The Patient will decide which hospital
	// One generatorpatient Class + one hospital in main
	/*
	 * inform patient patients the available hospitals the next complex step is to
	 * make another hospital at this point patient will make the decision, and the
	 * decision could be random once you have this than you can start thinking about
	 * more complex things, make the patients informed about availability,
	 * positionning hospitals and calculte the distance and choose the closest
	 */
	private int lifelos; // LOS of the patient
	private Hospital hospital; // Reference to the creating Hospital agent

	protected void setup() {
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			hospital = (Hospital) args[0]; // Set the reference to the creating Hospital agent
		}

		System.out.println("Patient: " + getLocalName());
		// Set the lifelos randomly between 1 and 10

		lifelos = (int) (Math.random() * 10) + 1;
		addBehaviour(new LifeBehaviour());
		chooseHospital();

	}
	private void chooseHospital() {
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.setContent(getLocalName()); // Set the content as the patient's name
		message.addReceiver(hospital.getAID());
		send(message);
	}
    /*private void chooseHospital() {
        //ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent(getLocalName()); // Set the content as the patient's name
        message.addReceiver(hospital.getAID());
        send(message);
    }*/

	private class LifeBehaviour extends CyclicBehaviour {
		public void action() {
			if (lifelos > 0) {
				System.out.println("Patient " + getLocalName() + " - LOS: " + lifelos);
				lifelos--;
			} else {
				System.out.println("Patient " + getLocalName() + " - LOS over. Terminating...");
				if (hospital != null) {
					hospital.decrementActivePatients();
				}

				myAgent.doDelete(); // Terminate the agent
			}
		}
	}
}