package rampup;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import org.apache.commons.math3.distribution.PoissonDistribution;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import java.util.ArrayList;
import java.util.List;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class Hospital extends Agent {

	int duree;
	private int capacity = 20; // it can be a function but I will let it like that
	private int activePatients = 0; // Number of currently active patients
	double lambda = 10; // mean
	private List<String> patientList = new ArrayList<>();


	protected void setup() {

		System.out.println("Hospital  : " + getLocalName());

		/*
		 * Object[] args = getArguments() ; if (args != null) { duree =
		 * Integer.parseInt(args[0].toString());
		 */
		ContainerController container = getContainerController();
		// Create a new class patientGnerator that will have poisson and generate
		// patients based the lambda
		addBehaviour(new Gestion(this, 1000));

	}
//decrement Actual number of patients 
	public void decrementActivePatients() {
		activePatients--;

	}

	public class Gestion extends TickerBehaviour {
		public Gestion(Agent ag, int duree) {
			super(ag, duree);
		}	

		protected void onTick() {
			ACLMessage message = receive(); // Recevoir le prochain message
			while (message != null) {
				// Msg processing
				String patientAgentName = message.getContent(); // Récupérer le nom de l'agent patient du contenu du
																// message
				System.out.println("Received patient: " + patientAgentName);
                
				message = receive(); // Recevoir le prochain message
			}
		}
	}
	// Retrieve patients from the Patient generator
	// For example, you can use a shared data structure or store the patients in a
	// list

	// Send patients to the appropriate functionality in the Hospital agent
	// You can call a method or behavior to handle the patients

	// ...

	/*
	 * for (int i = 1; i <= patientsToCreate; i++) { if (activePatients < capacity)
	 * { ContainerController container = getContainerController(); String agentName
	 * = "Patient" + System.currentTimeMillis() + i; ; AgentController patient =
	 * container.createNewAgent(agentName, "rampup.Patient", new Object[] {
	 * Hospital.this, agentName }); patient.start(); activePatients++; // Increment
	 * the counter System.out.println("Bed " + i); } else { System.out.
	 * println("Hospital reached its capacity. Cannot create more patients.");
	 * break; } }
	 */
}
