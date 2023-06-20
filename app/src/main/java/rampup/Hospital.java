package rampup;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import org.apache.commons.math3.distribution.PoissonDistribution;
public class Hospital extends Agent {

	int durée; 
	protected void setup() {
		
	System.out.println("Hospital  : "+getLocalName());
	Object[] args = getArguments();
	if (args != null) {
	durée = Integer.parseInt(args[0].toString());
	addBehaviour(new Gestion(this, durée));
	}
	}	public class Gestion extends TickerBehaviour { //comportement Addition
		public Gestion(Agent ag, int durée){
		super(ag, durée);
		}
		
		public void onTick(){
			for(int i=1;i<=5;i++) {
				double lambda = 10; // mean
				PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
				int randomNumber = poissonDistribution.sample();
			}
	

		} 
    }
		
	  public static void main(String[] args) {
		  
		    Runtime runtime = Runtime.instance();
	        Profile profile = new ProfileImpl();
	        profile.setParameter(Profile.MAIN_HOST, "localhost"); // Set the hostname of the JADE main container
	        profile.setParameter(Profile.MAIN_PORT, "1099"); // Set the port of the JADE main container
	        profile.setParameter(Profile.GUI, "true"); // Enable the JADE GUI

	        try {
	            ContainerController container = runtime.createMainContainer(profile);
	            AgentController agentController = container.createNewAgent("Hospital", Hospital.class.getName(), null);
	            agentController.start();
	        } catch (StaleProxyException e) {
	            e.printStackTrace();
	        }
	    }

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

