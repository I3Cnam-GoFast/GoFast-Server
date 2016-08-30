package webservice;


import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.i3cnam.gofast.dao.CourseDAO;
import com.i3cnam.gofast.dao.TravelDAO;
import com.i3cnam.gofast.dao.UserDAO;
import com.i3cnam.gofast.model.Carpooling;
import com.i3cnam.gofast.model.DriverCourse;
import com.i3cnam.gofast.model.PassengerTravel;
import com.i3cnam.gofast.model.Place;
import com.i3cnam.gofast.model.User;
import com.i3cnam.gofast.server.Management;

/**
 * Created by GoFast team.
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
    	// TODO faire des services pour supprimer les objets de la base de données:
    	// - Un covoiturage a pris fin (valider fin trajet)
    	// - Le conducteur arrive à destination sans avoir pris de passager
    	// - Le passager ne trouve pas de covoiturages disponibles
    	// - ?
        Map<String, String> parms = session.getParms();	

        String response = "";
        System.out.println(session.getUri());
        switch (session.getUri()) {
        case "/declare_user" :
        	response = declareUser(parms);
        	break;
        case "/retrieve_account" :
        	response = retrieveAccount(parms);
        	break;
        case "/declare_course" :
        	response = declareCourse(parms);
        	break;
        case "/declare_travel" :
        	response = declareTravel(parms);
        	break;
        case "/get_user_course" :
        	response = getUserCourse(parms);
        	break;
        case "/get_user_travel" :
        	response = getUserTravel(parms);
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
        case "/abort_course" :
        	abortCourse(parms);
        	break;
        case "/abort_travel" :
        	abortTravel(parms);
        	break;
        default: 
        	response = "bad request";
        }
        
        return newFixedLengthResponse(response);
    }
    

    /**
     * constructs one user object with the parameters passed as parameter
     * and save it in the database if it is not already taken or phone is used
     * @param parms
     * @return a json string with the status (ok, existing, taken) 
     */
    private String declareUser(Map<String, String> parms){
    	JSONObject json = new JSONObject();
    	// create user object
    	User user = new User();
    	user.setNickname(parms.get("nickname"));
    	user.setPhoneNumber(parms.get("phone_number"));
    	
    	// look if the phone number is registered
    	UserDAO uDao = new UserDAO();
    	User existingUser = uDao.getByPhone(user.getPhoneNumber());
    	// if already registered, return the registered nickname (status: existing)
    	if (existingUser != null) {
    		try {
				json.put("status", "existing");
	    		json.put("nickname", existingUser.getNickname());
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		return json.toString();
    	}
    	
    	// look if the nickname is already taken
    	existingUser = uDao.get(user.getNickname());
    	// if so, return the status: taken
    	if (existingUser != null) {
    		try {
    			json.put("status", "taken");
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		return json.toString();
    	}

    	// if tel number and nickname are unknown register the new user
    	uDao.create(user);
    	// and return the status : ok
    	try {
    		json.put("status", "ok");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();

    }    
    

    /**
     * constructs one user object with the parameters passed as parameter
     * and save it in the database if it is not already taken or phone is used
     * @param parms
     * @return a json string with the status (ok, existing, taken) 
     */
    private String retrieveAccount(Map<String, String> parms){
    	JSONObject json = new JSONObject();
    	
    	// look if the phone number is registered
    	UserDAO uDao = new UserDAO();
    	User existingUser = uDao.getByPhone(parms.get("phone_number"));
    	// if already registered, return the registered nickname (status: existing)
    	if (existingUser != null) {
    		try {
				json.put("status", "existing");
	    		json.put("nickname", existingUser.getNickname());
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		return json.toString();
    	}
    	
    	// and return the status : new
    	try {
    		json.put("status", "new");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();

    }    
    
    /**
     * constructs one course object with the parameters passed as parameter
     * and save it in the database
     * @param parms
     * @return a json string with the course_id 
     */
    private String declareCourse(Map<String, String> parms){
    	// create course object
    	DriverCourse course = new DriverCourse();

    	// get the user from the database and set it to the object
    	UserDAO uDao = new UserDAO();
    	User user = uDao.get(parms.get("driver"));
    	if (user == null) {return "{\"ERROR\": \"The user is not registered in GoFast\"}";}
    	course.setDriver(user);

    	// set origin to the object
    	String[] coordinatesArr = parms.get("origin").split(",");
    	double lat = Double.parseDouble(coordinatesArr[0]);
    	double lng = Double.parseDouble(coordinatesArr[1]);
    	course.setOrigin(new Place(new LatLng(lat,lng)));
    	
    	// set destination to the object
    	coordinatesArr = parms.get("destination").split(",");
    	lat = Double.parseDouble(coordinatesArr[0]);
    	lng = Double.parseDouble(coordinatesArr[1]);
    	course.setDestination(new Place(new LatLng(lat,lng)));
    	System.out.println(course.getDestination());
    	
    	// set encoded points to the object
    	course.setEncodedPoints(parms.get("encoded_points"));
    	
    	// declare course
    	int courseId = Management.declareCourse(course);

    	// construct output
    	JSONObject json = new JSONObject();
    	try {
			json.put("course_id", courseId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return json.toString();
    	
    }
    

    /**
     * constructs one travel object with the values passed as parameter
     * and save it in the database.
     * parms must contain following keys:
     * "passenger", "origin", "destination", "radius"
     * @param parms 
     * @return a json string with the course_id 
     */
    private String declareTravel(Map<String, String> parms) {
    	// create travel object
    	PassengerTravel travel = new PassengerTravel();
    	
    	// get the user from the database and set it to the object
    	UserDAO uDao = new UserDAO();
    	User user = uDao.get(parms.get("passenger"));
    	if (user == null) {return "{\"ERROR\": \"The user is not registered in GoFast\"}";}
    	travel.setPassenger(user);

    	// set origin to the object
    	String[] coordinatesArr = parms.get("origin").split(",");
    	double lat = Double.parseDouble(coordinatesArr[0]);
    	double lng = Double.parseDouble(coordinatesArr[1]);
    	travel.setOrigin(new Place(new LatLng(lat,lng)));
    	
    	// set destination to the object
    	coordinatesArr = parms.get("destination").split(",");
    	lat = Double.parseDouble(coordinatesArr[0]);
    	lng = Double.parseDouble(coordinatesArr[1]);
    	travel.setDestination(new Place(new LatLng(lat,lng)));

    	// set radius to the object
    	travel.setRadius(Integer.parseInt(parms.get("radius")));

    	// declare travel
    	int travelId = Management.declareTravel(travel);

    	// construct output
    	JSONObject json = new JSONObject();
    	try {
			json.put("travel_id", travelId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return json.toString();
    }
    
    
    private String getUserCourse(Map<String, String> parms) {
    	UserDAO uDao = new UserDAO();
    	User user = uDao.get(parms.get("user_id"));
    	if (user == null) {return "{\"ERROR\": \"The user is not registered in GoFast\"}";}
    	DriverCourse course = Management.getCourseByDriver(user);
    	if (course == null) {return "{\"ERROR\": \"The are no course for this user\"}";}
         
    	return course.getJsonObject().toString();
    }
    
    private String getUserTravel(Map<String, String> parms) {
    	UserDAO uDao = new UserDAO();
    	User user = uDao.get(parms.get("user_id"));
    	if (user == null) {return "{\"ERROR\": \"The user is not registered in GoFast\"}";}
    	PassengerTravel travel = Management.getTravelByPassenger(user);
    	if (travel == null) {return "{\"ERROR\": \"The are no travel for this user\"}";}
         
    	return travel.getJsonObject().toString();
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

    private void abortCourse(Map<String, String> parms) {
    	CourseDAO cDao = new CourseDAO();
    	DriverCourse course = cDao.get(Integer.parseInt(parms.get("course_id")));
    	System.out.println(course);
    	Management.abortCourse(course);
    }

    private void abortTravel(Map<String, String> parms) {
    	TravelDAO tDao = new TravelDAO();
    	PassengerTravel travel = tDao.get(Integer.parseInt(parms.get("travel_id")));
    	System.out.println(travel);
    	Management.abortTravel(travel);
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
    	JSONObject json = new JSONObject();
    	JSONArray array = new JSONArray();
    	
    	for (Carpooling carpooling : carpoolings) {
    		// add carpool json string
    		array.put(carpooling.getJsonObject());
    	}

    	try {
			json.put("matches", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}

    	return json.toString();
    }    
    
    
}
