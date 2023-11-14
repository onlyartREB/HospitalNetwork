package rampup;

import jade.core.AID;

public class HospitalInfo {
    private String name;
    private AID agentAID;

    public HospitalInfo(String name, AID agentAID) {
        this.name = name;
        this.agentAID = agentAID;
    }

    public String getName() {
        return name;
    }

    public AID getAID() {
        return agentAID;
    }
}