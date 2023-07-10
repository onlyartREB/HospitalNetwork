package rampup;

import java.util.List;

import org.apache.commons.math3.distribution.PoissonDistribution;

import jade.core.Agent;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Profile;
import jade.core.ProfileImpl;



import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
public class patientGeneratorOptFnct extends Agent{
	// ContainerController container = getContainerController();
		
	    //I can use this function by passing a parametre for each lambda 
	public List<String> Generate(double lambda) {	
		PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
		int randomNumber = poissonDistribution.sample(); // let it like that we will find a way to ditribute everyday a random number from this distribution
		try {
			// Instantiate the JADE runtime
			Runtime rt = Runtime.instance();
			// Create the main container for the agents
			Profile profile = new ProfileImpl();
			ContainerController container = rt.createMainContainer(profile);
            System.out.println(randomNumber);
            System.out.println("Begin to create ");
			for (int i = 1; i <= randomNumber; i++) {
				String agentName = "Patient" + System.currentTimeMillis() + i;
				AgentController patient = container.createNewAgent(agentName, "rampup.Patient", null);
				patient.start();
				
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.addReceiver(new AID("Hospital", AID.ISLOCALNAME));
				message.setContent(agentName); // Envoyez le nom de l'agent patient dans le contenu du message
				send(message);
				
				
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
 