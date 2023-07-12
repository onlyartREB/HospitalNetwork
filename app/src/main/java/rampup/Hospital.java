package rampup;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class Hospital extends Agent {
    private int capacity = 20; // Maximum number of patients the hospital can handle
    private int activePatients = 0; // Number of currently active patients
    private List<List<String>> patientLists = new ArrayList<>(); // Separate patient lists for each hospital
    private List<String> patientList = new ArrayList<>();

    protected void setup() {
        System.out.println("Hospital: " + getLocalName());
        addBehaviour(new Gestion(this, 1000));
        patientLists.add(new ArrayList<>()); // List for Hospital0
        patientLists.add(new ArrayList<>()); // List for Hospital1
    }

    public void decrementActivePatients() {
        activePatients--;
    }

    public List<String> getPatientList() {
        return patientList;
  }  
    public void printPatients() {
        int hospitalIndex = Integer.parseInt(getLocalName().replaceAll("\\D+", "")); // Extract the hospital index from the agent name
        List<String> patients = patientLists.get(hospitalIndex);
        System.out.println("Patients in " + getLocalName() + ":");
        for (String patient : patients) {
            System.out.println(patient);
        }
    }

    public class Gestion extends TickerBehaviour {
        public Gestion(Agent ag, int period) {
            super(ag, period);
        }

        protected void onTick() {
            ACLMessage message = receive();
            while (message != null) {
                String patientAgentName = message.getContent();
                System.out.println("Received patient: " + patientAgentName);
                int hospitalIndex = Integer.parseInt(getLocalName().replaceAll("\\D+", "")); // Extract the hospital index from the agent name
                patientLists.get(hospitalIndex).add(patientAgentName); // Add the patient to the appropriate hospital's list
                message = receive();
            }

            if (activePatients < capacity && !patientList.isEmpty()) {
                String patientAgentName = patientList.remove(0);
                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                reply.setContent(patientAgentName); // Set the content as the patient's name
                reply.addReceiver(getAID());
                send(reply);
                activePatients++;
                printPatients();

            }
            

        }
    }
}


