package webservice;


import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

import java.io.IOException;
import java.util.Map;

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
        	parms.get("driver");
        	parms.get("origin");
        	parms.get("destination");
        	parms.get("encoded_points");
        	response = "{"
        			+ "\"course_id\":1"
        			+ "}";
        	break;
        case "/declare_travel" :
        	parms.get("passenger");
        	parms.get("origin");
        	parms.get("destination");
        	parms.get("radius");
        	response = "{"
        			+ "\"course_id\":2"
        			+ "}";
        	break;
        case "/find_matches" :
        	parms.get("travel_id");
        	response = "{\"matches\": ["
        			+ "{"
        			+ "\"id\": \"1\","
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.61,"
        			+ "\"long\":1.45"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.65,"
        			+ "\"long\":1.41"
        			+ "},"
        			+ "\"pickup_time\": \"08:51\","
        			+ "\"fare\": \"3.25\","
        			+ "\"state\": \"POTENTIAL\""
        			+ "},"
        			+ "{"
        			+ "\"id\": \"2\","
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.62,"
        			+ "\"long\":1.46"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.66,"
        			+ "\"long\":1.42"
        			+ "},"
        			+ "\"pickup_time\": \"08:52\", "
        			+ "\"fare\": \"3.30\","
        			+ "\"state\": \"POTENTIAL\""
        			+ "},"
        			+ "{"
        			+ "\"id\": \"3\","
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.63,"
        			+ "\"long\":1.47"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.64,"
        			+ "\"long\":1.40"
        			+ "},"
        			+ "\"pickup_time\": \"08:53\", "
        			+ "\"fare\": \"3.50\","
        			+ "\"state\": \"POTENTIAL\""
        			+ "}"
        			+ "]}";
        	break;
        case "/request_carpooling" :
        	parms.get("carpool_id");
        	break;
        case "/cancel_request" :
        	parms.get("carpool_id");
        	break;
        case "/accept_carpooling" :
        	parms.get("carpool_id");
        	break;
        case "/refuse_carpooling" :
        	parms.get("carpool_id");
        	break;
        case "/abort_carpooling" :
        	parms.get("carpool_id");
        	break;
        case "/update_position" :
        	parms.get("course_id");
        	parms.get("new_position");
        	break;
        case "/update_course" :
        	parms.get("course_id");
        	parms.get("new_position");
        	parms.get("new_encoded_points");
        	break;
        case "/get_travel" :
        	parms.get("travel_id");
        	response = "{\"matches\": ["
        			+ "{"
        			+ "\"id\": \"1\","
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.61,"
        			+ "\"long\":1.45"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.65,"
        			+ "\"long\":1.41"
        			+ "},"
        			+ "\"pickup_time\": \"08:52\","
        			+ "\"fare\": \"3.25\","
        			+ "\"state\": \"POTENTIAL\""
        			+ "},"
        			+ "{"
        			+ "\"id\": \"2\","
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.62,"
        			+ "\"long\":1.46"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.66,"
        			+ "\"long\":1.42"
        			+ "},"
        			+ "\"pickup_time\": \"08:54\", "
        			+ "\"fare\": \"3.30\","
        			+ "\"state\": \"POTENTIAL\""
        			+ "},"
        			+ "{"
        			+ "\"id\": \"3\","
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.63,"
        			+ "\"long\":1.47"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.64,"
        			+ "\"long\":1.40"
        			+ "},"
        			+ "\"pickup_time\": \"08:53\", "
        			+ "\"fare\": \"3.50\","
        			+ "\"state\": \"POTENTIAL\""
        			+ "}"
        			+ "]}";
        	break;
        case "/get_course" :
        	parms.get("course_id");
        	response = "{\"matches\": ["
        			+ "{"
        			+ "\"id\": \"1\","
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.61,"
        			+ "\"long\":1.45"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.65,"
        			+ "\"long\":1.41"
        			+ "},"
        			+ "\"pickup_time\": \"08:52\","
        			+ "\"fare\": \"3.25\","
        			+ "\"state\": \"REQUESTED\""
        			+ "}]}";
        	break;
        default: 
        	response = "bad request";
        }
        
        

        return newFixedLengthResponse(response);
    }
}
