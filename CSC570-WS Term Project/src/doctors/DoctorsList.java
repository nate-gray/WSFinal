package doctors;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "doctorsList")
public class DoctorsList {
    private List<Doctor> doctors; 
    private AtomicInteger drId;

    public DoctorsList() { 
    	doctors = new CopyOnWriteArrayList<Doctor>(); 
    	drId = new AtomicInteger();
    }

    @XmlElement 
    @XmlElementWrapper(name = "doctors") 
    public List<Doctor> getDoctors() { 
    	return this.doctors;
    } 
    
    public void setDoctors(List<Doctor> drs) { 
    	this.doctors = drs;
    }

    @Override
    public String toString() {
    	String s = "";
    	for (Doctor d : doctors) s += d.toString();
    	return s;
    }

    public Doctor find(int id) {
    	Doctor doc = null;
    	// Search the list -- for now, the list is short enough that
    	// a linear search is ok but binary search would be better if the
    	// list got to be an order-of-magnitude larger in size.
    	for (Doctor d : doctors) {
    		if (d.getDrId() == id) {
    			doc = d;
    			break;
    		}
    	}	
    	return doc;
    }
    
    public int add(String drName) {
    	int id = drId.incrementAndGet();
    	Doctor d = new Doctor();
    	d.setDrName(drName);
    	d.setDrId(id);
    	doctors.add(d);
    	return id;
    }
}