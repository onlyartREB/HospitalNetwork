package rampup;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Hospital extends Agent {
    private int capacity = 20; // Maximum number of patients the hospital can handle
	private int activePatients = 0; // Number of currently active patients

    private List<String> patientList; // List to store the names of admitted patients

    protected void setup() {
        System.out.println("Hospital: " + getLocalName());
        patientList = new ArrayList<>();

        addBehaviour(new Gestion(this, 1000));
    }

    private class Gestion extends TickerBehaviour {
       	Gestion(Agent ag, int period) {
       		super(ag, period);
		}
        public void onTick() {
            ACLMessage message = receive();
            if (message != null) {
                String patientAgentName = message.getContent();
                System.out.println("Received patient: " + patientAgentName);

                if (patientList.size() < capacity) {
                    patientList.add(patientAgentName);
                	ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
                    reply.addReceiver(message.getSender());
                    send(reply);
                    System.out.println("Patient " + patientAgentName + " admitted to " + getLocalName());
                } else {
                    ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
                    reply.addReceiver(message.getSender());
                    send(reply);

                    System.out.println("Patient " + patientAgentName + " rejected by " + getLocalName());
                }
            } else {
                block();
            }
        }
    }
}
