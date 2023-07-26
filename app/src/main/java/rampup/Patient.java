package rampup;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

import java.util.List;
import java.util.Random;

public class Patient extends Agent {
    private List<String> hospitalNames; // List of available hospitals in the container

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            hospitalNames = (List<String>) args[0]; // Get the list of hospital names
        }

        System.out.println("Patient: " + getLocalName());
        chooseHospital(); // Send a request message to find a hospital
        addBehaviour(new LifeBehaviour());

    }

    private void chooseHospital() {
        // Randomly choose a hospital from the list
        int randomIndex = new Random().nextInt(hospitalNames.size());
        String chosenHospital = hospitalNames.get(randomIndex);
        
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent(getLocalName()); // Send the patient's name as content
        message.addReceiver(new AID(chosenHospital, AID.ISLOCALNAME));
        send(message);

        System.out.println("Patient " + getLocalName() + " chose Hospital " + chosenHospital);
    }


    private class LifeBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage reply = receive();
            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.AGREE) {
                    System.out.println("Patient " + getLocalName() + " - Accepted by Hospital: " + reply.getSender().getLocalName());
                    // Proceed with the patient's life behavior now that a hospital has accepted the patient
                } else {
                    System.out.println("Patient " + getLocalName() + " - Rejected by Hospital: " + reply.getSender().getLocalName());
                }
            } else {
                block();
            }
        }
    }
}
