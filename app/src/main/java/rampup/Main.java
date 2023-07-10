package rampup;

import jade.core.Profile;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Main {
	public static void main(String[] args) {
		System.out.println("we are starting");
		// Get the JADE runtime instance
		Runtime runtime = Runtime.instance();
		// Create a profile with the desired settings
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.MAIN_PORT, "1099");
		AgentContainer mainContainer = runtime.createMainContainer(profile);

		try {

			AgentController tickerAgentController = mainContainer.createNewAgent("Hospital 1",
					rampup.Hospital.class.getName(), null);
			AgentController tickerAgentController2 = mainContainer.createNewAgent("Hospital 2",
					rampup.Hospital.class.getName(), null);

			// Hospitals are ready
			tickerAgentController.start();
			tickerAgentController2.start();

			// Creation patients
			// How much do you expect the mean would be ?

			// ew patientGeneratorOptFnct.Generate(lambda)
			System.out.println("Finished execution");
			// Wait for user input to terminate the program
			System.out.println("Press enter to terminate...");
			System.in.read();

			// Terminate the agents and the JADE platform
			tickerAgentController.kill();
			tickerAgentController2.kill();

			runtime.shutDown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}