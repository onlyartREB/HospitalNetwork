/*
package rampup;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.apache.commons.math3.distribution.PoissonDistribution;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;

public class PatientGeneratorTick  extends Agent {

	int duree;
	private int capacity = 20; // it can be a function but I will let it like that
	private int activePatients = 0; // Number of currently active patients
	double lambda = 10; // mean
	PoissonDistribution poissonDistribution;

	protected void setup() {

		System.out.println("PatientGeneratorTick  : " + getLocalName());

		
		 * Object[] args = getArguments() ; if (args != null) { duree =
		 * Integer.parseInt(args[0].toString());
		 
		ContainerController container = getContainerController();
		poissonDistribution = new PoissonDistribution(lambda);
		// Create a new class patientGnerator that will have poisson and generate
		// patients based the lambda
		addBehaviour(new Gestion(this, 1000));

	}


	public class Gestion extends TickerBehaviour {
		public Gestion(Agent ag, int duree) {
			super(ag, duree);
		}
		
		public void onTick() {
			System.out.println("Gestion behavior to create is executing...");
			
			int randomNumber = poissonDistribution.sample();


			try {
				System.out.println("Welcome to the " + randomNumber + " Patients");
				for (int i = 1; i <= randomNumber; i++) {
						ContainerController container = getContainerController();
						String agentName = "Patient" + System.currentTimeMillis() + i;
						;
						AgentController patient = container.createNewAgent(agentName, "rampup.Patient",
								new Object[] { PatientGeneratorTick.this, agentName });
						patient.start();
						activePatients++; // Increment the counter
						System.out.println("Patient " + i);
						break;
					}
				
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}

}
*/
