package webservice;


import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.i3cnam.gofast.dao.UserDAO;
import com.i3cnam.gofast.model.Carpooling;
import com.i3cnam.gofast.model.DriverCourse;
import com.i3cnam.gofast.model.PassengerTravel;
import com.i3cnam.gofast.model.Place;
import com.i3cnam.gofast.model.User;
import com.i3cnam.gofast.server.Management;

/**
 * Created by Thibaud on 17/09/15.
 */
public class App extends NanoHTTPD {

    public App() throws IOException {
        super(9090);
        //start();
        System.out.println("Server is strating on port 9090");
    }

    public static void main(String[] args) {
        ServerRunner.run(App.class);
    }

    public Response serve(IHTTPSession session) {
        String msg = "<html><h1>Nuxlight-Server</h1></html>";
        Map<String, String> parms = session.getParms();	
        msg = msg+parms.toString();
        msg += session.getUri();
        String response = "";
        System.out.println(session.getUri());
        switch (session.getUri()) {
        case "/declare_course" :
        	response = declareCourse(parms);
        	break;
        case "/declare_travel" :
        	response = declareTravel(parms);
        	break;
        case "/find_matches" :
        	response = findMatches(parms);
        	break;
        case "/request_carpooling" :
        	requestCarpooling(parms);
        	break;
        case "/cancel_request" :
        	cancelCarpooling(parms);
        	break;
        case "/accept_carpooling" :
        	acceptCarpooling(parms);
        	break;
        case "/refuse_carpooling" :
        	refuseCarpooling(parms);
        	break;
        case "/abort_carpooling" :
        	abortCarpooling(parms);
        	break;
        case "/update_position" :
        	updatePosition(parms);
        	break;
        case "/update_course" :
        	updateCourse(parms);
        	break;
        case "/get_travel" :
        	response = getTravel(parms);
        	break;
        case "/get_course" :
        	response = getCourse(parms);
        	break;
        default: 
        	response = "bad request";
        }
        
        return newFixedLengthResponse(response);
    }
    
    
    private String declareCourse(Map<String, String> parms){

    	// create course object
    	DriverCourse course = new DriverCourse();
    	UserDAO uDao = new UserDAO();
    	User user = uDao.get(parms.get("driver"));
    	if (user == null) {return "ERROR: The user is not registered in GoFast";}
    	course.setDriver(user);
    	System.out.println(course.getDriver());

    	String[] coordinatesArr = parms.get("origin").split(",");
    	double lat = Double.parseDouble(coordinatesArr[0]);
    	double lng = Double.parseDouble(coordinatesArr[1]);
    	course.setOrigin(new Place(new LatLng(lat,lng)));
    	System.out.println(course.getOrigin());
    	
    	coordinatesArr = parms.get("destination").split(",");
    	lat = Double.parseDouble(coordinatesArr[0]);
    	lng = Double.parseDouble(coordinatesArr[1]);
    	course.setDestination(new Place(new LatLng(lat,lng)));
    	System.out.println(course.getDestination());
    	
    	course.setEncodedPoints(parms.get("encoded_points"));
    	
    	// declare course
    	int courseId = Management.declareCourse(course);

    	return "{"
    			+ "\"course_id\":" + courseId
    			+ "}";
    }
    
    
    private String declareTravel(Map<String, String> parms) {

    	parms.get("passenger");
    	parms.get("origin");
    	parms.get("destination");
    	parms.get("radius");

    	// create travel object
    	PassengerTravel travel = new PassengerTravel();
    	UserDAO uDao = new UserDAO();
    	User user = uDao.get(parms.get("passenger"));
    	if (user == null) {return "ERROR: The user is not registered in GoFast";}
    	travel.setPassenger(user);

    	String[] coordinatesArr = parms.get("origin").split(",");
    	double lat = Double.parseDouble(coordinatesArr[0]);
    	double lng = Double.parseDouble(coordinatesArr[1]);
    	travel.setOrigin(new Place(new LatLng(lat,lng)));
    	
    	coordinatesArr = parms.get("destination").split(",");
    	lat = Double.parseDouble(coordinatesArr[0]);
    	lng = Double.parseDouble(coordinatesArr[1]);
    	travel.setDestination(new Place(new LatLng(lat,lng)));

    	travel.setRadius(Integer.parseInt(parms.get("radius")));
    	
    	int travelId = Management.declareTravel(travel);

    	return "{"
    			+ "\"travel_id\":" + travelId
    			+ "}";
    }
    
    
    private String findMatches(Map<String, String> parms) {    	
    	return convertToJsonCarpoolList(Management.findMatches(Integer.parseInt(parms.get("travel_id"))));
    }
    
    private void requestCarpooling(Map<String, String> parms) {
    	Management.requestCarpooling(Integer.parseInt(parms.get("carpool_id")));
    }
    
    private void cancelCarpooling(Map<String, String> parms) {
    	Management.cancelCarpooling(Integer.parseInt(parms.get("carpool_id")));
    }
    
    private void acceptCarpooling(Map<String, String> parms) {
    	Management.acceptCarpooling(Integer.parseInt(parms.get("carpool_id")));
    }
    
    private void refuseCarpooling(Map<String, String> parms) {
    	Management.refuseCarpooling(Integer.parseInt(parms.get("carpool_id")));
    }

    private void abortCarpooling(Map<String, String> parms) {
    	Management.abortCarpooling(Integer.parseInt(parms.get("carpool_id")));
    }

    private void updatePosition(Map<String, String> parms) {
    	String[] coordinatesArr = parms.get("new_position").split(",");
    	double lat = Double.parseDouble(coordinatesArr[0]);
    	double lng = Double.parseDouble(coordinatesArr[1]);

    	Management.updatePosition(Integer.parseInt(parms.get("course_id")), 
    			new LatLng(lat,lng));
    }

    private void updateCourse(Map<String, String> parms) {
    	String[] coordinatesArr = parms.get("new_position").split(",");
    	double lat = Double.parseDouble(coordinatesArr[0]);
    	double lng = Double.parseDouble(coordinatesArr[1]);

    	Management.updateCourse(Integer.parseInt(parms.get("course_id")), 
    			new LatLng(lat,lng), 
    			parms.get("new_encoded_points"));
    }
    
    private String getCourse(Map<String, String> parms) {
    	return convertToJsonCarpoolList(Management.getCourse(Integer.parseInt(parms.get("course_id"))));
    }
    
    private String getTravel(Map<String, String> parms) {
    	return convertToJsonCarpoolList(Management.getTravel(Integer.parseInt(parms.get("travel_id"))));
    }
    

    /**
     * Converts a list of carpoolings into a JSONArray string
     * @param carpoolings The input carpooling list
     * @return The Json string
     */
    private String convertToJsonCarpoolList(List<Carpooling> carpoolings) {
    	String returnJson = "";
    	
    	for (Carpooling carpooling : carpoolings) {
    		// add a colon if it is not the first object
    		if (!returnJson.equals("")) {returnJson += ",";}
    		// add carpool json string
    		returnJson += carpooling.toJsonString();
    	}
    	
    	return "{\"matches\": [" + returnJson + "]}";
    }    
    
    
}
