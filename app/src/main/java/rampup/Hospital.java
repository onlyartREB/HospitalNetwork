package rampup;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.SequentialBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Hospital extends Agent {
	private int capacity = 1000; // Maximum number of patients the hospital can handle

	private int activePatients = 0; // Number of currently active patients
	private boolean isSpecialHospital = false; // Flag to identify the special hospital
	private List<PatientData> patientList; // List to store the admitted patients
	private int bedOccupancyRate;
	private double performanceScore;
	private int numRejection = 0;

	protected void setup() {
		System.out.println("Hospital: " + getLocalName());
		Object[] args = getArguments();
		if (args != null && args.length > 0 && args[0] instanceof Boolean) {
			isSpecialHospital = (Boolean) args[0];
			if (isSpecialHospital) {
				capacity = 100; // Set a higher capacity for the special hospital
			}
		}

		patientList = new ArrayList<>();
		addBehaviour(new PatientReceiver(this,1000));

	}

	private class PatientReceiver extends TickerBehaviour {

		public PatientReceiver(Agent a, long period) {
			super(a, period);
			// Set the message template to receive REQUEST messages
		}

		public void onTick() {
			// Receive the patient request message using the registered message template

               while(patientGeneratorOptFnct.generating==true) {
            	   try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               }
               
               while(patientGeneratorOptFnct.patientCounter >0) {
				ACLMessage message = receive();
				if (message != null) {
					String patientAgentName = message.getContent();
					System.out.println(getLocalName() + " Received patient: " + patientAgentName);
					if (patientList.size() < capacity) {
						patientList.add(new PatientData(patientAgentName, new AID(patientAgentName, AID.ISLOCALNAME),
								getLocalName()));
						ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
						reply.addReceiver(message.getSender());
						send(reply);
						System.out.println(
								"Patient " + patientAgentName + " admitted to " + getLocalName() + "_____________"
										+ " with a LOS of " + patientList.get(patientList.size() - 1).getLifeLos());
						patientGeneratorOptFnct.decrementCounter();
						System.out.println("Nombre de patient restant : "+patientGeneratorOptFnct.patientCounter);
						
						
					} else {
						ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
						reply.addReceiver(message.getSender());
						send(reply);
						System.out.println(
								"Patient " + patientAgentName + " rejected by " + getLocalName() + "***********");
						numRejection++;
					}

				}
	
               }
			}
	
			
		}

	

	private void treatPatients() {
		List<PatientData> treatedPatients = new ArrayList<>();

		for (PatientData patient : patientList) {
			patient.decrementLifeLos();
			System.out.println("---------------------Treating patient  " + patient.getName() + ", Life loss: "
					+ patient.getLifeLos() + ", Hospital: " + patient.getHospitalName());

			// Test if the patient's life loss has reached 0, indicating that the patient
			// has been treated
			if (patient.getLifeLos() == 0) {
				treatedPatients.add(patient);
			}
		}

		// Remove the treated patients from the patient list
		patientList.removeAll(treatedPatients);
		for (PatientData treatedPatient : treatedPatients) {
			// Send a reply message to the patient indicating treatment completion and
			// release
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.addReceiver(treatedPatient.getPatientAID());
			send(reply);
			System.out.println("Patient " + treatedPatient.getName() + " treated and released from " + getLocalName());
		}

	}

	private void checkCapacity() {
		updateBedOccupancyRate();
		performanceScore = HospitalPerformanceEvaluator.evaluatePerformance(bedOccupancyRate, numRejection);
		// System.out.println("Performance Score of " + getLocalName() + " : " +
		// performanceScore);
		System.out.println("current capacity of " + getLocalName() + " : " + capacity);
		adjustCapacity();

	
	}

	private void updateBedOccupancyRate() {
		bedOccupancyRate = (activePatients * 100) / capacity;
	}

	private void adjustCapacity() {

		capacity = HospitalCapacityAdjuster.adjustCapacity(performanceScore, capacity);
		System.out.println(getLocalName() + "Adjusted Capacity: " + capacity);
		

	}

	// HOSPITAL DATA CENTER
	// *********************************************************************************
	private static class PatientData {
		private String name;
		private int lifeLos;
		private AID patientAID;
		private String hospitalName; // The hospital where the patient is admitted
		private long entryTime;

		PatientData(String name, AID patientAID, String hospitalName) {
			this.name = name;
			this.lifeLos = (int) (Math.random() * 10) + 1; // Random lifelos
			this.patientAID = patientAID;
			this.hospitalName = hospitalName;
			this.entryTime = System.currentTimeMillis();
		}

		public String getHospitalName() {
			return hospitalName;
		}

		public AID getPatientAID() {
			return patientAID;
		}

		String getName() {
			return name;
		}

		int getLifeLos() {
			return lifeLos;
		}

		void decrementLifeLos() {
			lifeLos--;
		}
	}
}
