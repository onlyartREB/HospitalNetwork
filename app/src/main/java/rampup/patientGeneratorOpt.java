package rampup;

import org.apache.commons.math3.distribution.PoissonDistribution;

import jade.core.Agent;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Profile;
import jade.core.ProfileImpl;

public class patientGeneratorOpt {
	// ContainerController container = getContainerController();
	public static void main(String[] args) {
		double lambda = 10; // mean
		PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
		int randomNumber = poissonDistribution.sample();
		try {
			// Instantiate the JADE runtime
			Runtime rt = Runtime.instance();
			// Create the main container for the agents
			Profile profile = new ProfileImpl();
			ContainerController container = rt.createMainContainer(profile);

			for (int i = 1; i <= randomNumber; i++) {
				String agentName = "Patient" + System.currentTimeMillis() + i;
				AgentController patient = container.createNewAgent(agentName, "rampup.Patient", null);
				patient.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
