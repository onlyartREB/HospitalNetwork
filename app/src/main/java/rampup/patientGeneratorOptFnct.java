package rampup;
import org.apache.commons.math3.distribution.PoissonDistribution;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.List;
import java.util.concurrent.Semaphore;

public class patientGeneratorOptFnct {
    static int patientCounter = 0;
    private static int numHospitals;
    public static boolean generating;
	public static final Object counterLock = new Object(); // Lock object for synchronization

    public static void generate(List<Integer> lambdas, AgentContainer container, List<Zone> zones, int numHospitals) {
        for (int lambda : lambdas) {
        	generating=true;
            if (lambda > 0) {
                patientCounter = 0;
                patientGeneratorOptFnct.numHospitals = numHospitals;
                PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
                int randomNumber = poissonDistribution.sample();
                System.out.println("Number of patients coming: " + randomNumber);
                System.out.println("__________________________________________________________");

                try {
                    for (Zone zone : zones) {
                        int nPatZone = (int) Math.round(zone.getProportionOfPatients() * randomNumber);
                        for (int i = 1; i <= nPatZone; i++) {
                            String patientName = "Patient" + java.util.UUID.randomUUID() + "_" + patientCounter;
                            patientCounter++;
                            AgentController patientController = container.createNewAgent(patientName, "rampup.Patient",
                                    new Object[] { zone });
                            patientController.start();
                        }
                    }
                    Thread.sleep(1000);
                    generating=false;

        			synchronized (counterLock) {
        				while (patientCounter > 0) {

        					counterLock.wait(); // Wait until patientCounter becomes zero
        				}
        			}
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Wait for hospitals to admit all patients
             
            } else {
                System.out.println("No patients are coming");
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