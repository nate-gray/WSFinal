package doctors;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlRootElement(name = "doctor")
public class Doctor implements Comparable<Doctor> {
    private String drName;   // the doctors name
    // private String what;  // his/her prediction
    private int    drId;    // identifier used as lookup-key
//    private String drExtId; //external id for the doctor
    private ArrayList<Patient> patientList = new ArrayList<Patient>(); // the list of patients for the doctor

    public Doctor() { }

    @Override
    public String toString() {
    	String test = "\n";
    	for(int i = 0; i < patientList.size(); i++){
    		test += i + ": " + patientList.get(i).getPatientName() + " - " + patientList.get(i).getInsuranceNum() + "\n";
    	}
    		
    	return String.format("%2d: ", drId) + drName + ": " + " ==> " + test + "\n";
    }
    
    //** properties
    public void setDrName(String drName) {
	this.drName = drName;
    }
    @XmlElement
    public String getDrName() {
	return this.drName;
    }

    public void addPatient(Patient patient) {
    	this.patientList.add(patient);
    }
    
    @XmlElementWrapper
    @XmlElement(name = "patient")
    public ArrayList<Patient> getPatients() {
    	return this.patientList;
    }

    public void setDrId(int id) {
	this.drId = id;
    }
    @XmlElement
    public int getDrId() {
	return this.drId;
    }

    // implementation of Comparable interface
    public int compareTo(Doctor other) {
	return this.drId - other.drId;
    }
    

//	public String getDrExtId() {
//		return drExtId;
//	}
//
//	public void setDrExtId(String drExtId) {
//		this.drExtId = drExtId;
//	}	
}