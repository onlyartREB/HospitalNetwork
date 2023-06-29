package rampup;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class Patient extends Agent {

    private int lifelos; // LOS of the patient
    private Hospital hospital; // Reference to the creating Hospital agent

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            hospital = (Hospital) args[0]; // Set the reference to the creating Hospital agent
        }
        
        System.out.println("Patient: " + getLocalName());

        // Set the lifelos randomly between 1 and 10
        lifelos = (int) (Math.random() * 10) + 1;
        addBehaviour(new LifeBehaviour());
    }

    private class LifeBehaviour extends CyclicBehaviour {
        public void action() {
            if (lifelos > 0) {
                System.out.println("Patient " + getLocalName() + " - LOS: " + lifelos);
                lifelos--;
            } else {
                System.out.println("Patient " + getLocalName() + " - LOS over. Terminating...");
                if (hospital != null) {
                    hospital.decrementActivePatients();
                }

                myAgent.doDelete(); // Terminate the agent
            }
        }
    }
}