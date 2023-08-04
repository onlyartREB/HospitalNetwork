package rampup;

import org.apache.commons.math3.distribution.PoissonDistribution;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.List;
import java.util.Random;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.List;

public class patientGeneratorOptFnct {

    private int patientCounter = 0; // Initialize a counter for patients
    private long patientCreationDelay = 3000; // Delay between patient creation in milliseconds

    public void generate(List<Integer> lambdas, AgentContainer container, List<Zone> zones) {
        for (int lambda : lambdas) {
            if (lambda > 0) {
                PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);
                double randomNumber = poissonDistribution.sample();
                System.out.println("Number of patients coming _________________________________________________________" + randomNumber);
                try {
                    for (Zone zone : zones) {
                        int nPatZone = (int) Math.round(zone.getProportionOfPatients() * randomNumber);
                        for (int i = 1; i <= nPatZone; i++) {
                            String patientName = "Patient" + System.currentTimeMillis() + "_" + patientCounter;
                            patientCounter++; // Increment the patient counter for the next patient
                            AgentController patientController = container.createNewAgent(patientName, "rampup.Patient",
                                    new Object[] { zone });
                            patientController.start();
                            Thread.sleep(patientCreationDelay);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}