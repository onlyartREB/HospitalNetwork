package rampup;

import jade.wrapper.AgentContainer;

import jade.wrapper.AgentController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
public class PatientTreatment {
	private int waitingPatientCounter = 0;
	private static int numHospitals;
	public static boolean generating;
	public static int dailyPatient;
	public static final Object lock = new Object();
	private static PatientTreatment instance;
	// Hospital that have finished the treatment

	Map<Hospital.PatientReceiver,Boolean> patientReceivers = new HashMap<>();
	private Main mainThread;

	private int totalHospitals = 0;

	public PatientTreatment() {

	}

	public PatientTreatment(Main mainThread) {
		this.mainThread = mainThread;
	}

	public int getWaitingPatientCount() {
		return waitingPatientCounter;
	}

	public void consumePatient() {
		this.waitingPatientCounter--;
	}

	public void generate(int numPatient, AgentContainer container, List<Zone> zones) {
		generating = true;
		this.waitingPatientCounter = 0;


		// System.out.println("Number of patients coming: " + numPatient);
		safePrintln("________________________________________________________");

		try {

			for (Zone zone : zones) {
				int nPatZone = (int) Math.round(zone.getProportionOfPatients() * numPatient);
				for (int i = 1; i <= nPatZone; i++) {
					String patientName = "Patient" + java.util.UUID.randomUUID() + "_" + waitingPatientCounter;
					this.waitingPatientCounter++;
					AgentController patientController = container.createNewAgent(patientName, "rampup.Patient",
							new Object[] { zone });
					patientController.start();
				}
			}
			dailyPatient = this.waitingPatientCounter;
			safePrintln("Number of patients generated " + dailyPatient);
			//Notify all hospitals in order to resume treatment
			for (Hospital.PatientReceiver patientReceiver : this.patientReceivers.keySet()) {
				synchronized(patientReceiver) {
					patientReceiver.notify();
					safePrintln("Notify "+ patientReceiver.getHospital().getLocalName());
				}
		    }
			generating = false;

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Wait for hospitals to admit all patients

	}

	public static int getNumHospitals() {
		return numHospitals;
	}
	public void addNewPatientReceiver(Hospital.PatientReceiver hospital) {
		synchronized(this) {
			this.patientReceivers.put(hospital, false);
			safePrintln(hospital.getHospital().getLocalName() + "is registered" );
			safePrintln("Current size of hospitals that are registred : "+ PatientTreatment.getInstance(this.mainThread).getPatientReceivers().size());
			
		}
		
		
	}

	// Add new hospital that finished the treatment
	public void addFinishedPatientReceiver(Hospital.PatientReceiver patientReceiver) {
		synchronized (this) {
			this.patientReceivers.put(patientReceiver,true);
			if (this.patientReceivers.size() == this.mainThread.getNumbHospital() &&  this.checkIfAllPatientReceiversHaveFinished()) {
				synchronized(this.mainThread) {
					if (this.patientReceivers.size() == this.mainThread.getNumbHospital() && this.checkIfAllPatientReceiversHaveFinished()) {
						synchronized (this) {
							resetPatientReceiverTreatment();
						}
						this.mainThread.notify();
						safePrintln("Size of hospitals that finished " + this.patientReceivers.size() );

					}
				}
			}
		}
	}
	public void stopAllPatientReceiverTreatment() {
		for(Hospital.PatientReceiver patientReceiver: this.patientReceivers.keySet()) {
			synchronized(patientReceiver) {
				patientReceiver.stop();
			}
			
		}
	}
	public boolean checkIfAllPatientReceiversHaveFinished() {
		boolean haveFinished = true;
		for(Hospital.PatientReceiver patientReceiver: this.patientReceivers.keySet()) {
			haveFinished = haveFinished& this.patientReceivers.get(patientReceiver);
		}
		
		return haveFinished;
	}
	
	public void resetPatientReceiverTreatment() {
		for(Hospital.PatientReceiver patientReceiver: this.patientReceivers.keySet()) {
			this.patientReceivers.put(patientReceiver,false);
		}
		
	}



	public Map<Hospital.PatientReceiver, Boolean> getPatientReceivers() {
		return patientReceivers;
	}

	public static PatientTreatment getInstance(Main mainThread) {
		if (instance == null) {
			instance = new PatientTreatment(mainThread);
		}
		return instance;
	}

	public static PatientTreatment getInstance() {
		if (instance == null) {
			instance = new PatientTreatment();
		}
		return instance;
	}
	public void safePrintln(String s) {
		  synchronized (System.out) {
		    System.out.println(s);
		  }
	}

}