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


public class Hospital extends Agent {
    
	int durée; 
	private int capacity = 20;
	private int activePatients = 0; // Number of currently active patients
	protected void setup() {
	
	System.out.println("Hospital  : "+getLocalName());
	
	/*Object[] args = getArguments() ;
	if (args != null) {
	durée = Integer.parseInt(args[0].toString());
	*/
    ContainerController container = getContainerController();
	addBehaviour(new Gestion(this, 1000));
	}
	   public void decrementActivePatients() {
	        activePatients--;
	    
}
	public class Gestion extends TickerBehaviour { 
	public Gestion(Agent ag, int durée){
	super(ag, durée);
	}
	public void onTick() {
	    System.out.println("Gestion behavior executing...");
	    double lambda = 10; // mean
	    PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
	    int randomNumber = poissonDistribution.sample();

	    int patientsToCreate = Math.min(randomNumber, capacity - activePatients); // Consider the remaining capacity

	    try {
	    	System.out.println("Welcome to the " + patientsToCreate + " Patients");
	        for (int i = 1; i <= patientsToCreate; i++) {
	            if (activePatients < capacity) {
	                ContainerController container = getContainerController();
	                String agentName = "Patient" + System.currentTimeMillis()+ i;;
	                AgentController patient = container.createNewAgent(agentName, "rampup.Patient", new Object[]{Hospital.this, agentName});
	                patient.start();
	                activePatients++; // Increment the counter
	                System.out.println("Creating " + i);
	            } else {
	                System.out.println("Hospital reached its capacity. Cannot create more patients.");
	                break;
	            }	
	    }} catch (StaleProxyException e) {
	        e.printStackTrace();
	    }
	}
	}

}