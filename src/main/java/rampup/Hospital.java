package rampup;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;

public class Hospital extends Agent {

  // Name
  private String name;
  // Cost
  private int cost;
  // Maximum number of patients
  private int capacity = 10000;
  // List admitted patients
  private List<PatientData> patientList;

  public static int numAdmissions = 0;
  public boolean allAdmit = false;
  private Main mainThread;

  protected void setup() {
    safePrintln("Hospital: " + getLocalName());

    Boolean isSpecialHospital = false;

    Object[] args = getArguments();

    this.mainThread = (Main) args[0];
    isSpecialHospital = (Boolean) args[1];
    this.name = (String) args[2];

    if (isSpecialHospital) {
      this.capacity = 10000;
    }

    patientList = new ArrayList<>();

    addBehaviour(new PatientReceiver(this, 1, this, mainThread, this.capacity));

  }

  public class PatientReceiver extends TickerBehaviour {

    private int capacity = 0;
    private Main mainThread;
    private Hospital hospital;
    private boolean isFirstTreatment = true;

    public Hospital getHospital() {
      return hospital;
    }


    public PatientReceiver(Agent a, long period, Hospital hospital, Main mainThread, int capacity) {
      super(a, period);
      this.mainThread = mainThread;
      this.capacity = capacity;
      this.hospital = hospital;
      this.isFirstTreatment = true;
      PatientTreatment.getInstance(this.mainThread).addNewPatientReceiver(this);
    }


    /**
     *
     */
    public void onTick() {

      // Receive the patient request message using the registered message
      // template
      if (PatientTreatment.getInstance(this.mainThread).getPatientReceivers()
          .containsKey(this)
          && !PatientTreatment.getInstance(this.mainThread)
              .getPatientReceivers().get(this)) {
        safePrintln(this.getHospital().getLocalName() + " : " + PatientTreatment
            .getInstance(this.mainThread).getPatientReceivers().get(this));

        if (this.isFirstTreatment) {
          synchronized (this) {
            try {
              this.wait();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
        this.isFirstTreatment = false;
        while (PatientTreatment.getInstance(this.mainThread)
            .getWaitingPatientCount() > 0) {
          ACLMessage message = receive();
          if (message != null) {
            String patientAgentName = message.getContent();
            safePrintln(
                getLocalName() + " Received patient: " + patientAgentName);
            if (patientList.size() < capacity) {
              patientList.add(new PatientData(patientAgentName,
                  new AID(patientAgentName, AID.ISLOCALNAME), getLocalName()));
              ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
              reply.addReceiver(message.getSender());
              send(reply);
              safePrintln("Patient " + patientAgentName + " admitted to "
                  + getLocalName() + "_____________" + " with a LOS of "
                  + patientList.get(patientList.size() - 1).getLifeLos());
              synchronized (Hospital.class) {
                if ((PatientTreatment.getInstance(this.mainThread)
                    .getWaitingPatientCount() > 0)) {
                  numAdmissions++;
                  ;
                }

                safePrintln("Number of admissions : " + numAdmissions);
              }
              synchronized (PatientTreatment.getInstance(this.mainThread)) {
                if ((PatientTreatment.getInstance(this.mainThread)
                    .getWaitingPatientCount() > 0)) {
                  PatientTreatment.getInstance(this.mainThread)
                      .consumePatient();
                }

              }

            } else {

              ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
              reply.addReceiver(message.getSender());
              send(reply);
              safePrintln("Patient " + message.getContent() + " rejected by "
                  + getLocalName() + "*********");
              safePrintln("Number of remaining patients " + PatientTreatment
                  .getInstance(this.mainThread).getWaitingPatientCount());

            }
          }
        }

        synchronized (this) {
          try {
            treatPatientAndFinishTreatment();
            this.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }


    private void treatPatientAndFinishTreatment() {

      treatPatients();
      safePrintln(this.getHospital().getLocalName() + " are treating patient");
      synchronized (PatientTreatment.getInstance(this.mainThread)) {
        safePrintln(this.getHospital().getLocalName() + " finished treatment ");
        PatientTreatment.getInstance().addFinishedPatientReceiver(this);

        safePrintln("Number of remaining patients " + PatientTreatment
            .getInstance(this.mainThread).getWaitingPatientCount());
      }
    }
  }

  void treatPatients() {
    safePrintln(
        "Number of hospitals treating------------------------------------------ "
            + "  by  :" + getLocalName());

    List<PatientData> treatedPatients = new ArrayList<>();

    for (PatientData patient : patientList) {
      patient.decrementLifeLos();
      safePrintln("---------------------Treating patient  " + patient.getName()
          + ", Life loss: " + patient.getLifeLos() + ", Hospital: "
          + patient.getHospitalName());

      // Test if the patient's life loss has reached 0, indicating that the
      // patient
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
      safePrintln("Patient " + treatedPatient.getName()
          + " treated and released from " + getLocalName());
    }
  }

  // HOSPITAL DATA CENTER
  // *******************************************************************************
  private static class PatientData {

    private String name;
    private int lifeLos;
    private AID patientAID;
    private String hospitalName; // The hospital where the patient is admitted

    PatientData(String name, AID patientAID, String hospitalName) {
      this.name = name;
      this.lifeLos = (int) (Math.random() * 10) + 1; // Random lifelos
      this.patientAID = patientAID;
      this.hospitalName = hospitalName;
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