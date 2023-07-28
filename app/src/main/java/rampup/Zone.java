package rampup;
import java.util.ArrayList;
import java.util.List;

public class Zone {
	 private String zoneName;
	    private List<Hospital> hospitals;
	    private List<Patient> patients;

	    public Zone(String zoneName) {
	        this.zoneName = zoneName;
	        this.hospitals = new ArrayList<>();
	        this.patients = new ArrayList<>();
	    }

	    public void addHospital(Hospital hospital) {
	        hospitals.add(hospital);
	    }

	    public void addPatient(Patient patient) {
	        patients.add(patient);
	    }

}