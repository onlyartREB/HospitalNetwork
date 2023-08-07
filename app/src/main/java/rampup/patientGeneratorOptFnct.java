package rampup;

import org.apache.commons.math3.distribution.PoissonDistribution;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.List;
import java.util.Random;

public class patientGeneratorOptFnct {

	public static int patientCounter = 0; // Global counter for patients
	public static final Object counterLock = new Object(); // Lock object for synchronization

	public static void generate(List<Integer> lambdas, AgentContainer container, List<Zone> zones) {
		for (int lambda : lambdas) {
			if (lambda > 0) {

				PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
				double randomNumber = poissonDistribution.sample();
				System.out.println("Number of patients coming: " + randomNumber);
				System.out.println("__________________________________________________________");

				try {
					for (Zone zone : zones) {
						int nPatZone = (int) Math.round(zone.getProportionOfPatients() * randomNumber);
						for (int i = 1; i <= nPatZone; i++) {

							String patientName = "Patient" + java.util.UUID.randomUUID() + "_" + patientCounter;
							patientCounter++; // Increment the global patient counter
							AgentController patientController = container.createNewAgent(patientName, "rampup.Patient",
									new Object[] { zone });
							patientController.start();
							
						}
					}
					Thread.sleep(1000);

                       
					synchronized (counterLock) {
						while (patientCounter != 0) {

							counterLock.wait(); // Wait until patientCounter becomes zero
						}
					}
				} catch (Exception e) {

					e.printStackTrace();
				}

			}
			else {
				System.out.println("No patient are coming ");
			}
		}
	}

	public static int getCounter() {
		return patientCounter;

	}

	public static void decrementCounter() {
		synchronized (counterLock) {
			patientCounter--;
			if (patientCounter == 0) {
				counterLock.notify(); // Notify waiting generator thread
			}
		}
	}

}
