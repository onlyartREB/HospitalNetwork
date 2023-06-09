package rampup;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.apache.commons.math3.distribution.PoissonDistribution;
public class Hospital extends Agent {

	int dur�e; 
	protected void setup() {
	System.out.println("Hospital  : "+getLocalName());
	Object[] args = getArguments() ;
	if (args != null) {
	dur�e = Integer.parseInt(args[0].toString());
	addBehaviour(new Gestion(this, dur�e));
	}
	}
	public class Gestion extends TickerBehaviour { //comportement Addition
	public Gestion(Agent ag, int dur�e){
	super(ag, dur�e);
	}
	public void onTick(){
		for(int i=1;i<=5;i++) {
			double lambda = 10; // mean
			PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
			int randomNumber = poissonDistribution.sample();
			System.out.println("Hospital "+getLocalName()+" I received : " +randomNumber+ " patients");
		}

	} 
	}
	/*int dur�e; 
	protected void setup() {
	System.out.println("Je suis l'hopital : "+getLocalName());
	Object[] args = getArguments() ;
	if (args != null) {
	dur�e = Integer.parseInt(args[0].toString());
	addBehaviour(new Addition(this, dur�e));
	}
	}
	
	public class Addition extends TickerBehaviour { //comportement Addition
	public Addition(Agent ag, int dur�e){
	super(ag, dur�e);
	}
	public void onTick(){
		for(int i=1;i<=5;i++) {
			PoissonDistribution D1 = new PoissonDistribution(15);
			int value = D1.sample();
			System.out.println("Hopital"+getLocalName()+":j�ai g�n�r� :"+value+" patient");
		}

	} 
	}
	public void setup()
	    { 
	        System.out.println("Hospital: " +getAID().getName());
	    }
	*/
}
