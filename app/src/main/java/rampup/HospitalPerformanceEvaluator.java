package rampup;

public class HospitalPerformanceEvaluator {

    public static double evaluatePerformance(int bedOccupancyRate, int averageWaitTime) {
        double weightBedOccupancyRate = 0.75;
        double weightAverageWaitTime = 0.25;

        double normalizedBedOccupancy = bedOccupancyRate / 100.0;
        double normalizedAverageWaitTime = averageWaitTime / 100.0;

        double score = (weightBedOccupancyRate * normalizedBedOccupancy) +
                       (weightAverageWaitTime * normalizedAverageWaitTime);

        return score;
    }

}