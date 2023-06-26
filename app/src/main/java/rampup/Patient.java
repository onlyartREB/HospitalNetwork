package rampup;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.introspection.AddedContainer;
import jade.wrapper.AgentController;
import jade.core.behaviours.CyclicBehaviour;

public class Patient extends Agent {

    private int lifelos; // LOS of the patient
    private Hospital hospital;

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }
    protected void setup() {
        System.out.println("Patient: " + getLocalName());
        // Set the lifelos randomly between 1 and 10
        lifelos = (int) (Math.random() * 10) + 1;
        addBehaviour(new LifeBehaviour());
    }

    private class LifeBehaviour extends CyclicBehaviour {
        public void action() {
            if (lifelos > 0) {
                System.out.println("Patient " + getLocalName() + " - Lifelos: " + lifelos);
                lifelos--;
            } else {
                System.out.println("Patient " + getLocalName() + " - Lifelos over. Terminating...");
                hospital.decrementActivePatients();
                myAgent.doDelete(); // Terminate the agent
            }
        }
        }
}