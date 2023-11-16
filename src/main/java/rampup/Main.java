package rampup;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.distribution.PoissonDistribution;

public class Main {

  private int numHospitals;

  public Main(int numHospitals) {
    this.numHospitals = numHospitals;
  }

  public void run() {
    System.out.println("Starting the simulation...");

    // JADE runtime instance
    Runtime runtime = Runtime.instance();

    try {
      // Create a container for the Hospital agents
      Profile hospitalProfile = new ProfileImpl();
      hospitalProfile.setParameter(Profile.MAIN_HOST, "localhost");
      hospitalProfile.setParameter(Profile.MAIN_PORT, "1099");
      AgentContainer hospitalContainer = runtime
          .createMainContainer(hospitalProfile);

      // Create a container for the Patient agents
      Profile patientProfile = new ProfileImpl();
      patientProfile.setParameter(Profile.MAIN_HOST, "localhost");
      patientProfile.setParameter(Profile.MAIN_PORT, "1099");
      AgentContainer patientContainer = runtime
          .createAgentContainer(patientProfile);

      // Create Hospital agents
      List<String> hospitalNames = new ArrayList<>();
      for (int i = 1; i <= numHospitals - 1; i++) {
        String hospitalName = "Hospital" + i;

        Object[] args = new Object[] { this, false, hospitalName };

        AgentController hospitalController = hospitalContainer
            .createNewAgent(hospitalName, Hospital.class.getName(), args);
        hospitalController.start();
        hospitalNames.add(hospitalName);
      }

      // Create Special Hospital agent
      String hospitalName = "SpecialHospital";
      // Pass 'true' as an argument to identify a special hospital
      Object[] specialArgs = new Object[] { this, true, hospitalName };
      AgentController hospitalController = hospitalContainer
          .createNewAgent(hospitalName, Hospital.class.getName(),
              specialArgs);
      hospitalController.start();
      hospitalNames.add(hospitalName);

      // INPUT
      // _________________________________________________________________
      int numZones = 15;
      List<Double> proportions = Arrays.asList(0.01, 0.04, 0.09, 0.04,
          0.01, 0.03, 0.03, 0.01,
          0.03, 0.15, 0.08, 0.1,
          0.22, 0.12, 0.04);

      // Lambdas is the number of patients
      List<Integer> lambdas = Arrays.asList(0, 0, 0, 0, 0, 0, 40, 42, 51,
          64, 74, 84, 86, 86, 86, 83, 75,
          72, 62, 59, 53, 49, 45, 41, 34,
          30, 27, 26, 25, 23, 22, 21, 22,
          23, 20, 17, 17, 16, 14, 12, 9,
          9, 9, 9, 9, 9, 9, 9, 8, 7, 6,
          6, 6, 6, 5, 5, 5, 5, 5, 5);

      int[][] costMatrix = {
          // Zone 1
          { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 },
          // Zone 2
          { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 2, 3, 4, 5, 6 },
          // Zone 3
          { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22 },
          // Zone 4
          { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 },
          // Zone 5
          { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 },
          // Zone 6
          { 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 },
          // Zone 7
          { 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 },
          // Zone 8
          { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
          // Zone 9
          { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 },
          // Zone 10
          { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 },
          // Zone 11
          { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22 },
          // Zone 12
          { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 2, 3, 4, 5, 6 },
          // Zone 13
          { 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 },
          // Zone 14
          { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21 },
          // Zone 15
          { 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 } };

      startTreatment(runtime, costMatrix, lambdas, proportions, numZones,
          hospitalNames, hospitalContainer, patientContainer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method for generating zone and patients
   */
  public void startTreatment(Runtime runtime, int[][] costMatrix,
      List<Integer> lambdas, List<Double> proportions, int numZones,
      List<String> hospitalNames, AgentContainer hospitalContainer,
      AgentContainer patientContainer) {

    List<Zone> zones = generateZones(proportions, numZones, hospitalNames,
        costMatrix);

    System.out.println("Simulation started.");
    int day = 0;
    for (int lambda : lambdas) {
      if (lambda > 0) {
        PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
        final int randomNumber = poissonDistribution.sample();
        System.out.println("WE ARE IN THE DAY  " + ++day);
        System.out.println("Number of patient coming :   _____________  " + randomNumber);

        PatientTreatment.getInstance(this)
            .generate(randomNumber, patientContainer, zones);

        // hospitalTreating count of hospitals that finished the treatment
        if (PatientTreatment.getInstance(this).getPatientReceivers()
            .size() <= this.numHospitals) {
          synchronized (this) {
            try {
              this.wait();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }

        Hospital.numAdmissions = 0;

      } else {
        System.out.println("no patients coming");
      }
    }

    // Terminate the agents and the JADE platform
    PatientTreatment.getInstance(this).stopAllPatientReceiverTreatment();
    runtime.shutDown();
    System.out.println("Simulation terminated.");
  }

  private List<Zone> generateZones(List<Double> proportions, int numZones,
      List<String> hospitalNames, int[][] costMatrix) {
    List<Zone> zones = new ArrayList<>();

    for (int i = 0; i < proportions.size(); i++) {
      // Proportion affectation
      Double proportion = proportions.get(i);
      Zone zone = new Zone(i + 1, proportion);
      System.out.println(
          "ZONE CREATED " + (i + 1) + " THE PROPORTION IS " +
              proportion + " #");

      int[] costs = costMatrix[i];

      // Create a list to store the hospitals and their costs
      List<HospitalData> hospitals = new ArrayList<>();
      for (int j = 0; j < this.numHospitals; j++) {
        String hospitalName = hospitalNames.get(j);
        if (!hospitalName.equals("SpecialHospital")) {
          HospitalData hospital = new HospitalData(hospitalName, costs[j]);
          hospitals.add(hospital);
        }
      }

      // Sort the hospitals by cost in ascending order
      hospitals.sort(Comparator.comparingInt(HospitalData::getCost));

      // Add the sorted hospitals to the zone
      for (HospitalData hospital : hospitals) {
        zone.addHospital(hospital.getName(), hospital.getCost());
      }

      // Creating Zones and adding to the list
      zones.add(zone);
    }

    return zones;
  }

  public int getNumbHospital() {
    return this.numHospitals;
  }

  public static void main(String[] args) {
    Main main = new Main(16);
    main.run();
  }
}
