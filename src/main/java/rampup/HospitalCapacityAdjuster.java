
package rampup;

public class HospitalCapacityAdjuster {

    public static int adjustCapacity(double performanceScore, int capacity) {
        double lowerThreshold = 0.5; // Lower value to decrease capacity
        double upperThreshold = 0.8; // Upper value to increase capacity 
        double decreaseFactor = 0.9;
        double increaseFactor = 1.1;

        if (performanceScore < lowerThreshold) {
            capacity = (int) (capacity * decreaseFactor);
        } else if (performanceScore > upperThreshold) {
            capacity = (int) (capacity * increaseFactor);
        }

        return capacity;
    }

}