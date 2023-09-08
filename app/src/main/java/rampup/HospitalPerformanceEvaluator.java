package rampup;

public class HospitalPerformanceEvaluator {

    public static double evaluatePerformance(int bedOccupancyRate, int numRejection) {
        double weightBedOccupancyRate = 0.75;
        double weightnumRejection = 0.25;

        double normalizedBedOccupancy = bedOccupancyRate / 100.0;
        
        double normalizednumRejection = numRejection / 100.0;

        double score = (weightBedOccupancyRate * normalizedBedOccupancy) +
                       (weightnumRejection * normalizednumRejection);

        return score;
    }

}