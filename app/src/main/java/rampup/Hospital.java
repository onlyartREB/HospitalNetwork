package rampup;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import java.io.FileOutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
public class Hospital extends Agent {

	private int capacity = 4; // Maximum number of patients the hospital can handle
	private boolean isSpecialHospital = false; // Flag to identify the special hospital
	private List<PatientData> patientList; // List to store the admitted patients
	private int bedOccupancyRate;
	private double performanceScore;
	private int numRejections = 0;
	public  int numAdmissions = 0;
	public int numRejectionsRate=0;
	public boolean allAdmit = false;
	private Main mainThread;
	private int currentDay = 0; // Initialize the day counter
    public static int totalRejection;
	/**
	 * Extract main thread from passed arguments
	 * 
	 * @param args
	 * @return
	 */
	private Main extractMainThreadFrmArgs(Object[] args) {
		Main mainThread = null;
		for (Object arg : args) {
			if (arg instanceof Main) {
				mainThread = (Main) arg;
			}
		}
		return mainThread;
	}

	/**
	 * Extract isSpacialHospital from passed arguments
	 * 
	 * @param args
	 * @return
	 */
	private Boolean extractIsSpecialHospitalFrmArgs(Object[] args) {
		Boolean isSpecialHospital = false;
		for (Object arg : args) {
			if (arg instanceof Boolean) {
				isSpecialHospital = (Boolean) arg;
			}
		}
		return isSpecialHospital;
	}

	protected void setup() {

		safePrintln("Hospital: " + getLocalName());
		Object[] args = getArguments();
		Main mainThread = extractMainThreadFrmArgs(args);

		Boolean isSpecialHospital = extractIsSpecialHospitalFrmArgs(args);
		if (isSpecialHospital) {
			this.capacity = 10000; // Set a higher capacity for the special hospital
		}


				
		

		patientList = new ArrayList<>();
		addBehaviour(new PatientReceiver(this, 1, this, mainThread));

	}

	public class PatientReceiver extends TickerBehaviour {

		private Main mainThread;
		private Hospital hospital;
		private boolean isFirstTreatment = true;
		HospitalDataExcelHandler excelhandler = new HospitalDataExcelHandler(getLocalName() + "_data.xls");


		public Hospital getHospital() {
			return hospital;
		}

		public PatientReceiver(Agent a, long period, Hospital hospital, Main mainThread) {
			super(a, period);
			this.mainThread = mainThread;
			this.hospital = hospital;
			this.isFirstTreatment = true;
			PatientTreatment.getInstance(this.mainThread).addNewPatientReceiver(this);

			// TODO Auto-generated constructor stub
		}

		/**
		 * 
		 */

		public void onTick() {

			// Receive the patient request message using the registered message template
			if (PatientTreatment.getInstance(this.mainThread).getPatientReceivers().containsKey(this)
					&& !PatientTreatment.getInstance(this.mainThread).getPatientReceivers().get(this)) {
				safePrintln(this.getHospital().getLocalName() + " : "
						+ PatientTreatment.getInstance(this.mainThread).getPatientReceivers().get(this));

				if (this.isFirstTreatment) {
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				this.isFirstTreatment = false;
				currentDay++;

				while (PatientTreatment.getInstance(this.mainThread).getWaitingPatientCount() > 0) {

					ACLMessage message = receive();
					if (message != null) {
						String patientAgentName = message.getContent();
						safePrintln(getLocalName() + " Received patient: " + patientAgentName);
						if (patientList.size() < capacity) {
							patientList.add(new PatientData(patientAgentName,
									new AID(patientAgentName, AID.ISLOCALNAME), getLocalName()));
							ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
							reply.addReceiver(message.getSender());
							send(reply);
							safePrintln(
									"Patient " + patientAgentName + " admitted to " + getLocalName() + "_____________"
											+ " with a LOS of " + patientList.get(patientList.size() - 1).getLifeLos());

							synchronized (Hospital.class) {
								if ((PatientTreatment.getInstance(this.mainThread).getWaitingPatientCount() > 0)) {
									numAdmissions++;
									;
								}

								safePrintln("Number of admissions : " + numAdmissions);
							}
							synchronized (PatientTreatment.getInstance(this.mainThread)) {
								if ((PatientTreatment.getInstance(this.mainThread).getWaitingPatientCount() > 0)) {
									PatientTreatment.getInstance(this.mainThread).consumePatient();
								}

							}

						} else {

							ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
							reply.addReceiver(message.getSender());
							send(reply);
							safePrintln(
									"Patient " + message.getContent() + " rejected by " + getLocalName() + "*********");
							synchronized (Hospital.class) {
								if ((PatientTreatment.getInstance(this.mainThread).getWaitingPatientCount() > 0)) {
									numRejections++;
									totalRejection+=numRejections;
									;
								}

								safePrintln("Number of rejections : " + numRejections);
							}

						}
					}

				}

				synchronized (this) {
					try {
						treatPatientAndFinishTreatment();
				        excelhandler.addHospitalData(currentDay, numAdmissions, numRejections, bedOccupancyRate, performanceScore, capacity, patientList.size());
				        numAdmissions=0;
				        numRejections=0;
				        this.wait();

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}

		private void treatPatientAndFinishTreatment() {

			treatPatients();
			safePrintln(this.getHospital().getLocalName() + " are treating patient");
			checkCapacity();
			synchronized (PatientTreatment.getInstance(this.mainThread)) {
				safePrintln(this.getHospital().getLocalName() + " finished treatment ");
				PatientTreatment.getInstance().addFinishedPatientReceiver(this);

			}

		}

	}

	void treatPatients() {

		safePrintln(
				"Number of hospitals treating------------------------------------------ " + "  by  :" + getLocalName());

		List<PatientData> treatedPatients = new ArrayList<>();

		for (PatientData patient : patientList) {
			patient.decrementLifeLos();
			safePrintln("---------------------Treating patient  " + patient.getName() + ", Life loss: "
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
			safePrintln("Patient " + treatedPatient.getName() + " treated and released from " + getLocalName());
		}
	}

	private void checkCapacity() {
		updateBedOccupancyRate();
		updatenumRejection();
		performanceScore = HospitalPerformanceEvaluator.evaluatePerformance(bedOccupancyRate, numRejectionsRate);
		safePrintln("current capacity of " + getLocalName() + " : " + capacity);
		adjustCapacity();

	}

	private void updatenumRejection() {
		try {
			numRejectionsRate = (numRejections * 100) / capacity;

		} catch (Exception e) {
			numRejectionsRate = 0;
			// TODO: handle exception
		}
	}

	private void updateBedOccupancyRate() {
		try {
			bedOccupancyRate = (patientList.size() * 100) / capacity;

		} catch (Exception e) {
			bedOccupancyRate = 0;
			// TODO: handle exception
		}
	}

	private void adjustCapacity() {

		capacity = HospitalCapacityAdjuster.adjustCapacity(performanceScore, capacity);
		safePrintln(getLocalName() + "Adjusted Capacity: " + capacity);

	}

	// HOSPITAL DATA CENTER
	// *******************************************************************************
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

	public void safePrintln(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}

}