/*
package rampup;
import org.apache.commons.math3.distribution.PoissonDistribution;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;

public class PoissonGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

            
			double lambda = 10; // mean
			PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
			int randomNumber = poissonDistribution.sample();
			System.out.println("Get ready many patients are coming");
		    // Get the JADE runtime instance
		    Runtime runtime = Runtime.instance();
		    // Create a profile with the desired settings
		    Profile profile = new ProfileImpl();
		    profile.setParameter(Profile.MAIN_HOST, "localhost");
		    profile.setParameter(Profile.MAIN_PORT, "1099");
		    try {
		        // Create the main container
		        AgentContainer mainContainer = runtime.createMainContainer(profile);
		    	for(int i = 1;i<=randomNumber;i++)
				{
					String agentName = "Patient" + System.currentTimeMillis() + i;
					AgentController patient = mainContainer.createNewAgent(agentName, "rampup.Patient",
							null);
					patient.start();
				}
		        
		        System.out.println("Finished execution");
		        // Wait for user input to terminate the program
		        System.out.println("Press enter to terminate...");
		        System.in.read();
		        // Terminate the agents and the JADE platform
		        runtime.shutDown();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			// int patientsToCreate = Math.min(randomNumber, capacity - activePatients); //
			// Consider the remaining capacity
		
		 }
	}

*/
