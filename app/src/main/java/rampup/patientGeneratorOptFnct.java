package rampup;

import org.apache.commons.math3.distribution.PoissonDistribution;

import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Profile;
import jade.core.ProfileImpl;

import java.util.List;
import java.util.ArrayList;

public class patientGeneratorOptFnct {
    public void generate(double lambda, List<String> hospitalNames) {
        PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
        int randomNumber = poissonDistribution.sample();
        try {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            AgentContainer container = rt.createMainContainer(profile);

            for (int i = 1; i <= randomNumber; i++) {
                String patientName = "Patient" + System.currentTimeMillis() + i;
                AgentController patientController = container.createNewAgent(patientName, "rampup.Patient",
                        new Object[] { hospitalNames }); // Pass the hospital names to the patient agent
                patientController.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}