package com.i3cnam.gofast.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import systr.cartographie.Operations;

import com.google.android.gms.maps.model.LatLng;
import com.i3cnam.gofast.dao.CourseDAO;
import com.i3cnam.gofast.model.Carpooling;
import com.i3cnam.gofast.model.DriverCourse;
import com.i3cnam.gofast.model.PassengerTravel;


/**
 * @author Nestor
 */
public class MatchFinder {
	private PassengerTravel travel;

	public MatchFinder(PassengerTravel travel) {
		super();
		this.travel = travel;
	}
	

	/**
	 * Returns a list of the potential carpoolings for the travel with the courses existing in database
	 * 
	 * PROCEDURE:
	 * - Select into database, the courses whose limits include those from the travel
	 * - Search for each course, one pickup point and one dropoff point which satisfy
	 * the origin and the destination of the passenger and the radius of the travel
	 * - If the two point are found, create a Carpool object which includes: the pickup point,
	 * the dropoff point, the pickup time and the fare, and the state POTENTIAL
	 * 
	 * @return the list of the potential carpoolings 
	 */
	// for each current course into the zone:
	// browse the path
	// search for satisfying pickup point
	// if found:
	// continue browsing
	// search for satisfying dropoff point
	// if found: create carpool
	public List<Carpooling> findMatches() {
    	System.out.println("Match Finder");
		// return variable
		List<Carpooling> potentialCarpoolings = new ArrayList<>();
		// list of courses on the zone
		List<DriverCourse> possibleCoursesForCarpooling = getCoursesForTravelZone();

        int i, pickupSegment = 0;
        LatLng nearestPickupPoint = null, nearestDropoffPoint = null, nearestSegmentPoint;

        double distanceToPickup, distanceToDropoff, distanceToNeasrestSegmentPoint;
        
		for (DriverCourse oneCourse : possibleCoursesForCarpooling) {
	    	System.out.println("Looking : " + oneCourse.getId());

			// extract path from encoded points
			List<LatLng> actualPath = oneCourse.getPath();

	        i = 0;
	        distanceToPickup = 100000; // unlikely distance (100km)
	        distanceToDropoff = 100000; // unlikely distance (100km)
			// browse path
			// search origin
	        while (i < (actualPath.size() - 1)) {
	            // calculate nearest point from the passenger origin to this segment and the distance to this point
	        	nearestSegmentPoint = Operations.nearestPoint(actualPath.get(i), actualPath.get(i + 1),  travel.getOrigin().getCoordinates());
	        	distanceToNeasrestSegmentPoint = Operations.dist2PointsEnM(travel.getOrigin().getCoordinates(), nearestSegmentPoint);
	        	
	        	//look if this is the nearest found point and if so, update variables
	        	if (distanceToNeasrestSegmentPoint < distanceToPickup) {
	        		nearestPickupPoint = nearestSegmentPoint;
	        		distanceToPickup = distanceToNeasrestSegmentPoint;
	        		pickupSegment = i; // store the segment on the path to restart from here searching the dropoff point
	        	}
	        	i++;
	        }
	        
	    	System.out.println("Distance to pick up : " + distanceToPickup);

	        // evaluate if the nearest found point is satisfying
	        if (distanceToPickup < travel.getRadius()) {
				// continue browsing
				// search destination
	        	i = pickupSegment;
		        while (i < (actualPath.size() - 1)) {
		            // calculate nearest point from the passenger destination to this segment and the distance to this point
		        	nearestSegmentPoint = Operations.nearestPoint(actualPath.get(i), actualPath.get(i + 1),  travel.getDestination().getCoordinates());
		        	distanceToNeasrestSegmentPoint = Operations.dist2PointsEnM(travel.getDestination().getCoordinates(), nearestSegmentPoint);
		        	
		        	//look if this is the nearest found point and if so, update variables
		        	if (distanceToNeasrestSegmentPoint < distanceToDropoff) {
		        		nearestDropoffPoint = nearestSegmentPoint;
		        		distanceToDropoff = distanceToNeasrestSegmentPoint;
		        	}
		        	i++;
		        	
		        }
		    	System.out.println("Distance to drop off : " + distanceToDropoff);

		        // evaluate if the nearest found point is satisfying
		        if (distanceToDropoff < travel.getRadius()) {
		        	// !!!! THIS COURSE MATCHES WITH THE PASSANGER TRAVEL
					// construct carpool object
		        	Carpooling potentialCarpooling = new Carpooling();
		        	potentialCarpooling.setDriverCourse(oneCourse);
		        	potentialCarpooling.setPassengerTravel(travel);
		        	potentialCarpooling.setPickupPoint(nearestPickupPoint);
		        	potentialCarpooling.setDropoffPoint(nearestDropoffPoint);
		        	// TODO calculer heure et tarif
		        	potentialCarpooling.setPickupTime(new Date());
			    	System.out.println(potentialCarpooling);

		        	// add carpool object to the result array
		        	potentialCarpoolings.add(potentialCarpooling);
		        }
	        	
	        }

		}
		return potentialCarpoolings;
	}

	private List<DriverCourse> getCoursesForTravelZone(){
		CourseDAO cDao = new CourseDAO();
		return cDao.findByZone(travel.getOrigin().getCoordinates(), travel.getDestination().getCoordinates());
	}
	
}
