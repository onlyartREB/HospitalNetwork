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
        boolean accepted = false;
        boolean rejectedByAllRegularHospitals = false;

        while (!accepted && !rejectedByAllRegularHospitals && !hospitalNames.isEmpty()) {
            int randomIndex = new Random().nextInt(hospitalNames.size());
            String chosenHospital = hospitalNames.get(randomIndex);
            System.out.println("Patient " + getLocalName() + " chose Hospital " + chosenHospital);
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setContent(getLocalName()); // Send the patient's name as content
            message.addReceiver(new AID(chosenHospital, AID.ISLOCALNAME));
            send(message);

            long startTime = System.currentTimeMillis();
            ACLMessage reply;
            do {
                reply = blockingReceive(0); // Check for incoming messages with zero timeout
            } while (reply == null && (System.currentTimeMillis() - startTime) < 1000); // 1000 milliseconds timeout

            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.AGREE) {
                    accepted = true;
                } else if (reply.getPerformative() == ACLMessage.REFUSE) {
                    // The patient was rejected by the chosen hospital.
                    // Remove the rejected hospital from the hospitalNames list.
                    hospitalNames.remove(chosenHospital);
                    if (hospitalNames.isEmpty()) {
                        rejectedByAllRegularHospitals = true;
                    }
                }
            }
        }

        if (!accepted && rejectedByAllRegularHospitals) {
            // All regular hospitals rejected the patient, send a request to the special hospital.
            String specialHospitalName = "SpecialHospital"; // Change this to the actual name of the special hospital.
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setContent(getLocalName()); // Send the patient's name as content
            message.addReceiver(new AID(specialHospitalName, AID.ISLOCALNAME));
            send(message);

            ACLMessage reply = blockingReceive();
            if (reply != null && reply.getPerformative() == ACLMessage.AGREE) {
                System.out.println("Patient " + getLocalName() + " got accepted by the Special Hospital.");
            } else {
                System.out.println("Patient " + getLocalName() + " got rejected by the Special Hospital as well.");
            }
        }

     
    }

    private class LifeBehaviour extends CyclicBehaviour {
        public void action() {
    
        }
    }
}