package doctors;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class DoctorsRS {
    @Context 
    private ServletContext sctx;          // dependency injection
    private static DoctorsList dlist; // set in populate()

    public DoctorsRS() { }

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML}) 
    public Response getXml() {
    	checkContext();
    	return Response.ok(dlist, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML}) // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
    	checkContext();
    	return toRequestedType(id, "application/xml");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json")
    public Response getJson() {
    	checkContext();
    	return Response.ok(toJson(dlist), "application/json").build();
    }

    @GET    
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json/{id: \\d+}")
    public Response getJson(@PathParam("id") int id) {
    	checkContext();
    	return toRequestedType(id, "application/json");
    }

    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN}) 
    public String getPlain() {
    	checkContext();
    	return dlist.toString();
    }
    
    @GET
    @Produces({MediaType.TEXT_PLAIN}) 
    @Path("/plain/{id: \\d+}")   
    public Response getPlain(@PathParam("id") int id) {
    	checkContext();
    	return toRequestedType(id, "text/plain");
    }

    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create")
    public Response create(@FormParam("drName") String drName) { 
    	checkContext();
    	String msg = null;
    	// Require dr property. 
    	if (drName == null) { 
    		msg = "Property 'drName' is missing.\n";
    		return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
    	}	    
    	// Otherwise, create the Prediction and add it to the collection.
    	int id = addDoctor(drName);
    	msg = "Doctor " + id + " created: (drName = " + drName + ").\n";
    	return Response.ok(msg, "text/plain").build();
    }
    
    // Add a single patient via CURL request
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/add")
    public Response create(@FormParam("drId") String drId, @FormParam("patientName") String pntName, @FormParam("insuranceNum") String insNum) { 
    	checkContext();
    	String msg = null;
    	// Require all properties to create.
    	if (drId == null || pntName == null || insNum == null) { 
    		msg = "Missing the doctor id, patient name, or insurance number. \n";
    		return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
    	}	    
    	
    	int id = Integer.parseInt(drId);
    	Patient pnt = new Patient(pntName, insNum);
    	dlist.find(id).addPatient(pnt);
    	msg = pntName + " - " + insNum + " has been added to " + dlist.find(id).getDrName() + "'s patients. \n";
    	return Response.ok(msg, "text/plain").build();
    }

    // Add multiple patients via db file
     
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/addList")
    public Response create(@FormParam("dbFile") File dbFile) { 
    	checkContext();
    	String msg = null;
    	Doctor doc = null;
    	int i = 0;
    	
    	if(dbFile != null) {
    		try {
    			BufferedReader reader = new BufferedReader(new FileReader(dbFile));
    			String record = null;
    			while((record = reader.readLine()) != null) {
    				String[] parts = record.split("!");
    				Patient patient = new Patient();
    				patient.setPatientName(parts[0]);
    				patient.setInsuranceNum(parts[1]);
    				doc = dlist.find(Integer.parseInt(parts[2]));
    				doc.addPatient(patient);
    				i++;
    			}
    		}
    		
    		catch (Exception ex) {
    			throw new RuntimeException("I/O failed!");
    		}
    		
    		msg = i + " patients added from db local file.";
    	}

    	return Response.ok(msg, "text/plain").build();
    }
    
    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("id") int id, @FormParam("drName") String drName) { 
    	checkContext();

    	// Check that sufficient data are present to do an edit.
    	String msg = null;
    	if (drName == null) 
    		msg = "drName is not present in request: nothing to edit.\n";

    	Doctor d = dlist.find(id);
    	if (d == null)
    		msg = "There is no doctor with ID " + id + "\n";

    	if (msg != null)
    		return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
    	// Update.
    	if (drName != null) d.setDrName(drName);
    	msg = "Doctor " + id + " has been updated.\n";
    	return Response.ok(msg, "text/plain").build();
    }

    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/delete/{id: \\d+}")
    public Response delete(@PathParam("id") int id) {
    	checkContext();
    	String msg = null;
    	Doctor d = dlist.find(id);
    	if (d == null) {
    		msg = "There is no doctor with ID " + id + ". Cannot delete.\n";
    		return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
    	}
    	dlist.getDoctors().remove(d);
    	msg = "Doctor " + id + " deleted.\n";

    	return Response.ok(msg, "text/plain").build();
    }

    //** utilities
    private void checkContext() {
    	if (dlist == null) populate();
    }

    private void populate() {
    	dlist = new DoctorsList();

    	String filename = "/WEB-INF/data/drs.db";
    	InputStream in = sctx.getResourceAsStream(filename);
	
    	// Read the data into the array of Predictions. 
    	if (in != null) {
    		try {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    			int i = 0;
    			String record = null;
    				while ((record = reader.readLine()) != null) {
						addDoctor(record);
    				}
    		}
	    catch (Exception e) { 
	    	throw new RuntimeException("I/O failed!"); 
	    	}
    	}
    	
    	populatePatients();
    }
    
    private void populatePatients() {
    	String filename = "/WEB-INF/data/patients.db";
    	InputStream in = sctx.getResourceAsStream(filename);
    	
    	if(in !=null) {
    		try {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    			int i = 0;
    			String record = null;
    			while((record = reader.readLine()) != null) {
    				String[] parts = record.split("!");
    				Patient patient = new Patient();
    				patient.setPatientName(parts[0]);
    				patient.setInsuranceNum(parts[1]);
    				Doctor doc = dlist.find(Integer.parseInt(parts[2]));
    				doc.addPatient(patient);
    			}
    		}
    		catch(Exception e) {
    			throw new RuntimeException("I/O failed!");
    		}
    	}
    }

    // Add a new prediction to the list.
    private int addDoctor(String drName) {
    	int id = dlist.add(drName);
    	return id;
    }

    // Prediction --> JSON document
    private String toJson(Doctor prediction) {
    	String json = "If you see this, there's a problem.";
    	try {
    		json = new ObjectMapper().writeValueAsString(prediction);
    	}
    	catch(Exception e) { }
    	return json;
    }

    // PredictionsList --> JSON document
    private String toJson(DoctorsList dlist) {
    	String json = "If you see this, there's a problem.";
    	try {
    		json = new ObjectMapper().writeValueAsString(dlist);
    	}
    	catch(Exception e) { }
    	return json;
    }

    // Generate an HTTP error response or typed OK response.
    private Response toRequestedType(int id, String type) {
    	Doctor pred = dlist.find(id);
    	if (pred == null) {
    		String msg = id + " is a bad ID.\n";
    		return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
    	}
    	else if (type.contains("json"))
    		return Response.ok(toJson(pred), type).build();
    	else if (type.contains("text"))
    		return Response.ok(pred.toString(), type).build();
    	else
    		return Response.ok(pred, type).build(); // toXml is automatic
    }
}