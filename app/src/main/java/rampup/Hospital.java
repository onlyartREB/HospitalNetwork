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
	                AgentController patient = container.createNewAgent(agentName, "rampup.Patient", new Object[]{Hospital.this});
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
	   public void decrementActivePatients() {
	        activePatients--;
	    }
	/*int durée; 
	protected void setup() {
	System.out.println("Je suis l'hopital : "+getLocalName());
	Object[] args = getArguments() ;
	if (args != null) {
	durée = Integer.parseInt(args[0].toString());
	addBehaviour(new Addition(this, durée));
	}
	}
	public class Addition extends TickerBehaviour { //comportement Addition
	public Addition(Agent ag, int durée){
	super(ag, durée);
	}
	public void onTick(){
		for(int i=1;i<=5;i++) {
			PoissonDistribution D1 = new PoissonDistribution(15);
			int value = D1.sample();
			System.out.println("Hopital"+getLocalName()+":j’ai généré :"+value+" patient");
	}
	}
	}
	public void setup()
	    { 
	        System.out.println("Hospital: " +getAID().getName());
	    }
	*/

}