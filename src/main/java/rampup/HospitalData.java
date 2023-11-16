package rampup;

public class HospitalData {
    private String name;
    private int cost;

    public HospitalData(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }
}