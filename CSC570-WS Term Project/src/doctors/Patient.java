package doctors;

public class Patient {
	private String patientName; // the patients name
	private String insuranceNum; // the patients insurance id number	

	public Patient() { }
	
	public Patient(String name, String insuranceNum){
		this.patientName = name;
		this.insuranceNum = insuranceNum;
	}
	
	public void setPatientName(String name) {
		this.patientName = name;
	}

	public String getPatientName() {
		return this.patientName;
	}
	
	public void setInsuranceNum(String insuranceNum) {
		this.insuranceNum = insuranceNum;
	}

	public String getInsuranceNum() {
		return this.insuranceNum;
	}

}
