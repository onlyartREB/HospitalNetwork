package rampup;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private int zoneIndex;
    private double proportionOfPatients;
    private List<HospitalData> hospitals;

    public Zone(int zoneIndex, double proportionOfPatients) {
        this.zoneIndex = zoneIndex;
        this.proportionOfPatients = proportionOfPatients;
        this.hospitals = new ArrayList<>();
    }

    public int getZoneIndex() {
        return zoneIndex;
    }

    public double getProportionOfPatients() {
        return proportionOfPatients;
    }

    public List<HospitalData> getHospitals() {
        return hospitals;
    }

    public void addHospitalTarget(String name, int cost) {
        hospitals.add(new HospitalData(name, cost));
    }


}