package rampup;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting the simulation...");

        // Get the JADE runtime instance
        Runtime runtime = Runtime.instance();

        // Create a profile with the desired settings
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");

        try {
            // Create the main container for the Hospital agents
            AgentContainer mainContainer = runtime.createMainContainer(profile);

            // Create a container for the Patient agents
            Profile patientProfile = new ProfileImpl();
            patientProfile.setParameter(Profile.MAIN_HOST, "localhost");
            patientProfile.setParameter(Profile.MAIN_PORT, "1099");
            AgentContainer patientContainer = runtime.createAgentContainer(patientProfile);

            // Create a list to store the hospital names
            List<String> hospitalNames = new ArrayList<>();

            // Create and start the Hospital agents
            AgentController hospitalController1 = mainContainer.createNewAgent("Hospital1", Hospital.class.getName(), null);
            hospitalController1.start();
            hospitalNames.add("Hospital1");

            AgentController hospitalController2 = mainContainer.createNewAgent("Hospital2", Hospital.class.getName(), null);
            hospitalController2.start();
            hospitalNames.add("Hospital2");

            System.out.println("Simulation started.");

            // Generate patients and pass the hospital names and the patient container
            double lambda = 10;

            patientGeneratorOptFnct patientGenerator = new patientGeneratorOptFnct();
            patientGenerator.generate(lambda, hospitalNames, patientContainer);

            // Wait for the simulation to finish
            Thread.sleep(10000); // Adjust the time as needed to allow the simulation to run

            // Terminate the agents and the JADE platform
            hospitalController1.kill();
            hospitalController2.kill();

            runtime.shutDown();

            System.out.println("Simulation terminated.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
