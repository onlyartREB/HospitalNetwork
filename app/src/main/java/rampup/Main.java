
package rampup;

import jade.core.Profile;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.PoissonDistribution;

import java.util.logging.Level;
import java.util.logging.Logger;

import jade.util.ExtendedProperties;
import jade.util.leap.Properties;

public class Main {

	private int numHospitals;
	public HospitalDataExcelHandler excelHandler;

	public Main(int numHospitals) {
		this.numHospitals = numHospitals;
	}

	public void run() {
		System.out.println("Starting the simulation...");

		// Get the JADE runtime instance
		Runtime runtime = Runtime.instance();

		// Create a profile with the desired settings
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.MAIN_PORT, "1099");
		profile.setParameter(Profile.GUI, "true"); // This enables the GUI

		try {
			// Create the main container for the Hospital agents
			AgentContainer mainContainer = runtime.createMainContainer(profile);
			mainContainer.start();

			// Create a container for the Patient agents
			Profile patientProfile = new ProfileImpl();
			patientProfile.setParameter(Profile.MAIN_HOST, "localhost");
			patientProfile.setParameter(Profile.MAIN_PORT, "1099");
			AgentContainer patientContainer = runtime.createAgentContainer(patientProfile);
			patientContainer.start();
			List<String> hospitalNames = new ArrayList<>();
			Object[] args = new Object[] { this };
			for (int i = 1; i <= numHospitals - 1; i++) {
				String hospitalName = "Hospital" + i;

				AgentController hospitalController = mainContainer.createNewAgent(hospitalName,
						Hospital.class.getName(), args);
				hospitalController.start();
				hospitalNames.add(hospitalName);

			}

			String specialHospitalName = "SpecialHospital";
			Object[] specialArgs = new Object[] { true, this }; // Pass 'true' as an argument to identify it as the
																// special
			// hospital
			AgentController specialHospitalController = mainContainer.createNewAgent(specialHospitalName,
					Hospital.class.getName(), specialArgs);
			specialHospitalController.start();
			hospitalNames.add(specialHospitalName);

            
			// INPUT ______________________________________________________________________
			int numZones = 15;
			List<Double> proportions = Arrays.asList(0.01, 0.04, 0.09, 0.04, 0.01, 0.03, 0.03, 0.01, 0.03, 0.15, 0.08,
					0.1, 0.22, 0.12, 0.04);
			// Lambdas is the number of patients
			List<Integer> lambdas = Arrays.asList(0, 0, 0, 0, 0, 0, 40, 42, 51, 64, 74, 84, 86, 86, 86, 83, 75, 72, 62,
					59, 53, 49, 45, 41, 34, 30, 27, 26, 25, 23, 22, 21, 22, 23, 20, 17, 17, 16, 14, 12, 9, 9, 9, 9, 9,
					9, 9, 9, 8, 7, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5);
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
			startTreatment(runtime, costMatrix, lambdas, proportions, numZones, hospitalNames, mainContainer,
					patientContainer);

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * Method for generating zone and patiants
	 */

	public void startTreatment(Runtime runtime, int[][] costMatrix, List<Integer> lambdas, List<Double> proportions,
			int numZones, List<String> hospitalNames, AgentContainer mainContainer, AgentContainer patientContainer) {
		List<Zone> zones = generateZones(proportions, numZones, hospitalNames, costMatrix);

		System.out.println("Simulation started.");
		int sumtotalRejection=0; 
		//for (int numIteration = 0; numIteration <= 1000; numIteration++) { // 
        Hospital.totalRejection=0;
		int day = 0;
		for (int lambda : lambdas) {
			day++;
			if (lambda > 0) {
				PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
				final int randomNumber = poissonDistribution.sample();
				System.out.println("WE ARE IN THE DAY  " + day);
				System.out.println("Number of patient coming :   _____________  " + randomNumber);
				PatientTreatment.getInstance(this).generate(randomNumber, patientContainer, zones);

				// hospitalTreating count of hospitals that finished the treatment
				if (PatientTreatment.getInstance(this).getPatientReceivers().size() <= this.numHospitals) {
					synchronized (this) {
						try {

							this.wait(); 

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}


			} else {
				System.out.println("no patients coming"); // LAMBDA 0
			}
		}
		//System.out.println("Total rejection is : "+Hospital.totalRejection);
		sumtotalRejection+=Hospital.totalRejection;
		//}
		
		//System.out.println("mean rejection is : "+sumtotalRejection/1000);
		
		
		// Generating Excel file for ZONES
		HospitalDataExcelHandler hospitalZoneInfoExcelHandler = new HospitalDataExcelHandler("hospital_zone_info.xls");
		for (Zone zone : zones) {
		        hospitalZoneInfoExcelHandler.addZoneInfo(zone.getZoneIndex(), zone.getProportionOfPatients());
		}
		




		runtime.shutDown();

		System.out.println("Simulation terminated.");

	}

	public static void main(String[] args) {
		Main main = new Main(16);
		main.run();

	}

	List<Zone> generateZones(List<Double> proportions, int numZones, List<String> hospitalNames,
			int[][] costMatrix) { 
		List<Zone> zones = new ArrayList<>();

		for (int i = 1; i <= proportions.size(); i++) {
			// Proportion affectation
			Double proportion = proportions.get(i - 1);
			Zone zone = new Zone(i, proportion);
			System.out.println("ZONE CREATED " + i + " THE PROPORTION IS " + proportion + " #");

			int[] costs = costMatrix[i - 1];

			// Create a list to store the hospitals and their costs
			List<HospitalData> hospitals = new ArrayList<>();
			for (int j = 0; j < this.numHospitals; j++) {
				String hospitalName = hospitalNames.get(j);
				if (!hospitalName.equals("SpecialHospital")) {
					int cost = costs[j];
					HospitalData hospital = new HospitalData(hospitalName, cost);
					hospitals.add(hospital);
				}
			}

			// Sort the hospitals by cost in ascending order
			hospitals.sort(Comparator.comparingInt(HospitalData::getCost));

			// Add the sorted hospitals to the zone
			for (HospitalData hospital : hospitals) {
				zone.addHospitalTarget(hospital.getName(), hospital.getCost());
			}

			zones.add(zone); // Creating Zones and adding to the list
		}

		
		return zones;
	}

	public int getNumbHospital() {
		return this.numHospitals;
	}

}