package rampup;

import org.apache.commons.math3.distribution.PoissonDistribution;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Profile;
import jade.core.ProfileImpl;	
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class patientGeneratorOptFnct {
	public void generate(double lambda) {
	    PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
	    int randomNumber = poissonDistribution.sample();
	    try {
	        Runtime rt = Runtime.instance();
	        Profile profile = new ProfileImpl();
	        ContainerController container = rt.createMainContainer(profile);

	        for (int i = 1; i <= randomNumber; i++) {
	            String patientName = "Patient" + System.currentTimeMillis() + i;
	            AgentController patientController = container.createNewAgent(patientName, "rampup.Patient", null);
	            patientController.start();
	    
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}}