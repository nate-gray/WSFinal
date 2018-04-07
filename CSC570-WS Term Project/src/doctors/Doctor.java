package doctors;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlRootElement(name = "doctor")
public class Doctor implements Comparable<Doctor> {
    private String drName;   // the doctors name
    private int    drId;    // identifier used as lookup-key
    private ArrayList<Patient> patientList = new ArrayList<Patient>(); // the list of patients for the doctor

    public Doctor() { }

    @Override
    public String toString() {
    	String patientList_string = "\n";
    	for(int i = 0; i < patientList.size(); i++){
    		patientList_string += i + ": " + patientList.get(i).getPatientName() + " - " + patientList.get(i).getInsuranceNum() + "\n";
    	}   		
    	return String.format("%2d: ", drId) + drName + ": " + " ==> " + patientList_string + "\n";
    }
    
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
	
}