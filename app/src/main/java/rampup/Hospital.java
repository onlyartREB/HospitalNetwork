package rampup;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class Hospital extends Agent {
	private int capacity = 20; // Maximum number of patients the hospital can handle
	private int capacityCheckInterval = 5; // Number of ticks (weeks) between capacity checks

	private int activePatients = 0; // Number of currently active patients
	private boolean isSpecialHospital = false; // Flag to identify the special hospital
	private List<PatientData> patientList; // List to store the admitted patients
	private int bedOccupancyRate;
	private int averageWaitTime;
	double performanceScore;
    private Coordinates coordinates; // Add this variable to store the hospital's coordinates


	protected void setup() {

		System.out.println("Hospital: " + getLocalName());
		Object[] args = getArguments();
		if (args != null && args.length > 0 && args[0] instanceof Boolean) {
			isSpecialHospital = (Boolean) args[0];
			if (isSpecialHospital) {
				capacity = 100; // Set a higher capacity for the special hospital
			}
		}
	    if (args != null && args.length > 1 && args[1] instanceof Coordinates) {
            coordinates = (Coordinates) args[1]; // Get the hospital's coordinates from the arguments
        }
		patientList = new ArrayList<>();
		addBehaviour(new PatientReceiver(this, 1000));

		// double performanceScore = evaluatePerformance(75, 25); // Exemple to
		// calculate performanceScore
		// System.out.println("Performance Score: " + performanceScore);
	}

	private class PatientReceiver extends TickerBehaviour {
		public PatientReceiver(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		public void onTick() {
			ACLMessage message = receive();
			if (message != null) {
				String patientAgentName = message.getContent();
				System.out.println("__________________________________________________________");
				System.out.println(getLocalName() + " Received patient: " + patientAgentName);
				if (patientList.size() < capacity) {
					patientList.add(new PatientData(patientAgentName, new AID(patientAgentName, AID.ISLOCALNAME),
							getLocalName()));
					ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
					reply.addReceiver(message.getSender());
					send(reply);
					System.out.println(
							"Patient " + patientAgentName + " admitted to " + getLocalName() + "_____________");
					treatPatients(); // Treat the patients at each tick (day)

				} else {
					ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
					reply.addReceiver(message.getSender());
					send(reply);

					System.out
							.println("Patient " + patientAgentName + " rejected by " + getLocalName() + "***********");
				}
			} else {
				block();
			}
			  if (getTickCount() % capacityCheckInterval == 0) {
		            checkCapacity();
		        }
		}

	}

	private void checkCapacity() {
		updateBedOccupancyRate();
		updateAverageWaitTime();
		performanceScore = HospitalPerformanceEvaluator.evaluatePerformance(bedOccupancyRate, averageWaitTime);
		System.out.println("Performance Score of " + getLocalName() + " : " + performanceScore);
		adjustCapacity();
	}

	private void updateBedOccupancyRate() {
		bedOccupancyRate = (activePatients * 100) / capacity;

	}

	private void updateAverageWaitTime() {
		int totalWaitTime = 0;
		for (PatientData patient : patientList) {
			totalWaitTime += patient.getWaitingTime();
		}
		averageWaitTime = patientList.isEmpty() ? 0 : totalWaitTime / patientList.size();

		/*
		 * if (patientList.isEmpty()) { averageWaitTime = 0; } else { averageWaitTime =
		 * totalWaitTime / patientList.size(); }
		 */
	}

	private void adjustCapacity() {
		System.out.println("current capacity of " + getLocalName() + " : " + capacity);
		capacity = HospitalCapacityAdjuster.adjustCapacity(performanceScore, capacity);
		System.out.println(getLocalName() + "Adjusted Capacity: " + capacity);

	}

	private void treatPatients() {
		List<PatientData> treatedPatients = new ArrayList<>();

		for (PatientData patient : patientList) {
			patient.decrementLifeLos();
			System.out.println("Treating patient  " + patient.getName() + ", Life loss: " + patient.getLifeLos()
					+ ", Hospital: " + patient.getHospitalName());

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
			this.lifeLos = (int) (Math.random() * 10) + 1;
			; // Random lifelos
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

		long getWaitingTime() {
			return System.currentTimeMillis() - entryTime;
		}

		void decrementLifeLos() {
			lifeLos--;
		}

	}

}