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
        	parms.get("origin");
        	parms.get("destination");
        	parms.get("encoded_points");
        	response = "{"
        			+ "\"course_id\":1"
        			+ "}";
        	break;
        case "/find_matches" :
        	parms.get("origin");
        	parms.get("destination");
        	parms.get("radius");
        	response = "{\"matches\": ["
        			+ "{"
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.61,"
        			+ "\"long\":1.45"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.65,"
        			+ "\"long\":1.41"
        			+ "},"
        			+ "\"pickup_time\": \"08:51\","
        			+ "\"state\": \"POTENTIAL\""
        			+ "},"
        			+ "{"
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.62,"
        			+ "\"long\":1.46"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.66,"
        			+ "\"long\":1.42"
        			+ "},"
        			+ "\"pickup_time\": \"08:52\", "
        			+ "\"state\": \"POTENTIAL\""
        			+ "},"
        			+ "{"
        			+ "\"pickup_point\" : {"
        			+ "\"lat\":43.63,"
        			+ "\"long\":1.47"
        			+ "},"
        			+ "\"dropoff_point\" : {"
        			+ "\"lat\":43.64,"
        			+ "\"long\":1.40"
        			+ "},"
        			+ "\"pickup_time\": \"08:53\", "
        			+ "\"state\": \"POTENTIAL\""
        			+ "}"
        			+ "]}";
        	break;
        case "/request_carpooling" :
        	parms.get("carpool_id");
        	break;
        case "/accept_carpooling" :
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
        case "/get_carpooling" :
        	parms.get("course");
        	parms.get("travel");
        	break;
        default: 
        	response = "bad request";
        }
        
        

        return newFixedLengthResponse(response);
    }
}
