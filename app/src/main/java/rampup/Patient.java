package rampup;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.introspection.AddedContainer;
import jade.wrapper.AgentController;

public class Patient extends Agent{	 
	
	public void setup()
{ 
    System.out.println("Patient: " +getAID().getName());
}

	protected void takeDown() {
        System.out.println(getAID().getName() + " : Terminating.");
}
}
