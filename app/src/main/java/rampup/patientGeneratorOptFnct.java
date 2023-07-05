package rampup;

import org.apache.commons.math3.distribution.PoissonDistribution;

import jade.core.Agent;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Profile;
import jade.core.ProfileImpl;
	
import java.util.ArrayList;
import java.util.List;

public class patientGeneratorOptFnct {
    public List<String> generate(double lambda) {
        List<String> patientNames = new ArrayList<>();
        PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
        int randomNumber = poissonDistribution.sample();
        try {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            ContainerController container = rt.createMainContainer(profile);

            for (int i = 1; i <= randomNumber; i++) {
                String agentName = "Patient" + System.currentTimeMillis() + i;
                patientNames.add(agentName);
                AgentController patient = container.createNewAgent(agentName, "rampup.Patient", null);
                patient.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return patientNames;
    }
}
