package com.i3cnam.gofast.server;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.i3cnam.gofast.dao.CarpoolDAO;
import com.i3cnam.gofast.dao.CourseDAO;
import com.i3cnam.gofast.dao.TravelDAO;
import com.i3cnam.gofast.model.Carpooling;
import com.i3cnam.gofast.model.Carpooling.CarpoolingState;
import com.i3cnam.gofast.model.DriverCourse;
import com.i3cnam.gofast.model.PassengerTravel;
import com.i3cnam.gofast.model.User;

/**
 * Class with static methods to perform operation of the lifecycle of the object of the database
 * @author Nestor
 *
 */
public class Management {

	/**
	 * Inserts one course into database
	 * @param course
	 * @return the id of the inserted record
	 */
	public static int declareCourse(DriverCourse course) {
		// TODO: refuse insertion if the user involved into a course or travel
		CourseDAO cDao = new CourseDAO();
		return cDao.create(course);
	}
	
	/**
	 * Inserts one passenger travel into database
	 * @param travel
	 * @return the id of the inserted record
	 */
	public static int declareTravel(PassengerTravel travel) {
		// TODO: refuse insertion if the user involved into a course or travel
		TravelDAO tDao = new TravelDAO();
		return tDao.create(travel);
	}
	
	/**
	 * Search the current course for the driver in the database and return the object
	 * @param driver
	 * @return
	 */
	public static DriverCourse getCourseByDriver(User driver) {
		CourseDAO cDao = new CourseDAO();
		return cDao.getByUser(driver);
	}
	
	/**
	 * Search the current travel for the passenger in the database and return the object
	 * @param driver
	 * @return
	 */
	public static PassengerTravel getTravelByPassenger(User driver) {
		TravelDAO tDao = new TravelDAO();
		return tDao.getByUser(driver);
	}
	
	/**
	 * Search the possible carpools for the travel given id
	 * It insert the matches into the database (carpool table)
	 * @param travelId
	 * @return the list of carpoolings
	 */
	public static List<Carpooling> findMatches(int travelId) {
		// TODO: what to do if other carpool for thiis travel exist already in database?
		// what if they have diferent states than POTENTIAL
    	System.out.println("Management find matches");
		TravelDAO tDao = new TravelDAO();
		CarpoolDAO cDao = new CarpoolDAO();
		PassengerTravel travel = tDao.get(travelId);
		MatchFinder matcher = new MatchFinder(travel);
		List<Carpooling> matches = matcher.findMatches();
		// save potential carpoolings into database
		cDao.removeByTravelWithState(travel, CarpoolingState.POTENTIAL);
		for (Carpooling carpool : matches) {
	    	System.out.println("Insert to DB ");
	    	// if not exists in db 
	    	if (cDao.getByTravelAndDriver(carpool.getDriver(), travel).size() == 0) {
	    		cDao.create(carpool);
		    	System.out.println(carpool.getId());
	    	}
		}
		return matches;
	}
	
	/**
	 * It changes the state of a carpooling from POTENTIAL to IN_DEMAND
	 * (If the previous stat is not POTENTIAL, nothng is done)
	 * @param carpoolId
	 */
	public static void requestCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		System.out.println(carpooling);
		if (carpooling.getState() == CarpoolingState.POTENTIAL) {
			carpooling.setState(CarpoolingState.IN_DEMAND);
			System.out.println(carpooling);
			cDao.update(carpooling);
		}
	}
	
	/**
	 * It changes the state of a carpooling from IN_DEMAND to POTENTIAL
	 * (If the previous stat is not IN_DEMAND, nothng is done)
	 * @param carpoolId
	 */
	public static void cancelCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.IN_DEMAND) {
			carpooling.setState(CarpoolingState.POTENTIAL);
			cDao.update(carpooling);
		}
	}
	
	/**
	 * It changes the state of a carpooling from IN_DEMAND to IN_PROGRESS
	 * (If the previous stat is not IN_DEMAND, nothng is done)
	 * Remove all other carpooling of the travel (POTENTIAL and IN_DEMAND)
	 * @param carpoolId
	 */
	public static boolean acceptCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.IN_DEMAND) {
			// change state of carpooling and save it into the database
			carpooling.setState(CarpoolingState.IN_PROGRESS);
			cDao.update(carpooling);
			// remove all other carpooling of the travel from the database
			cDao.removeByTravelWithState(carpooling.getPassengerTravel(), CarpoolingState.POTENTIAL);
			cDao.removeByTravelWithState(carpooling.getPassengerTravel(), CarpoolingState.IN_DEMAND);
			// keep other states (could be CONFLICT)
			return true;
		}
		else return false;
	}
	
	/**
	 * It changes the state of a carpooling from IN_DEMAND to REFUSED
	 * (If the previous stat is not IN_DEMAND, nothng is done)
	 * @param carpoolId
	 */
	public static void refuseCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.IN_DEMAND) {
			carpooling.setState(CarpoolingState.REFUSED);
			cDao.update(carpooling);
		}		
	}

	/**
	 * It changes the state of a carpooling from IN_PROGRESS to CONFLICT
	 * (If the previous stat is not CONFLICT, nothng is done)
	 * @param carpoolId
	 */
	public static void abortCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.IN_PROGRESS) {
			carpooling.setState(CarpoolingState.CONFLICT);
			cDao.update(carpooling);
		}
	}
	
	/**
	 * It changes the state of a carpooling from IN_PROGRESS to CONFLICT
	 * (If the previous stat is not CONFLICT, nothng is done)
	 * @param carpoolId
	 */
	public static void validateEndCarpooling(int carpoolId, String role) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		boolean changeState = false;
		if (carpooling.getState() == CarpoolingState.IN_PROGRESS) {
			if (role.equals("driver")) {
				carpooling.setValidatedByDriver(true);
				changeState = carpooling.isValidatedByPassenger();
			}
			else if (role.equals("passenger")) {
				carpooling.setValidatedByPassenger(true);
				changeState = carpooling.isValidatedByDriver();
			}
			if (changeState) {
				carpooling.setState(CarpoolingState.ACHIEVED);
			}
			cDao.update(carpooling);
		}
	}

	/**
	 * Remove one course and all the associated carpoolings
	 * (The IN_PROGRESS carpools pass to state CONFLICT)
	 * @param course
	 */
	public static void abortCourse(int courseId) {
		CarpoolDAO cDao = new CarpoolDAO(); 
		CourseDAO dcDao = new CourseDAO();
		DriverCourse course = dcDao.get(courseId);
		cDao.removeStateByCourse(course, CarpoolingState.POTENTIAL);
		System.out.println("Potentials deleted");
		cDao.removeStateByCourse(course, CarpoolingState.IN_DEMAND);
		System.out.println("Requested deleted");
		cDao.removeStateByCourse(course, CarpoolingState.REFUSED);
		System.out.println("Refused deleted");
		// WE KEEP ONLY COnFLICT and ACHIEVED (for historical purposes)
		// Passes IN_PROGRESS to CONFLICT
		for (Carpooling courseCarpool : cDao.getByCourse(course, true)) {
			if(courseCarpool.getState() != CarpoolingState.IN_PROGRESS) {
				System.out.println("State changed");
				courseCarpool.setState(CarpoolingState.CONFLICT);
				cDao.update(courseCarpool);
			}
		}
		course.setObsolete(true);
		System.out.println("remov");
		dcDao.update(course);
		System.out.println("end");
	}

	/**
	 * Remove one travel and all the associated carpoolings
	 * (The IN_PROGRESS carpools pass to state CONFLICT)
	 * @param travel
	 */
	public static void abortTravel(int travelId) {
		CarpoolDAO cDao = new CarpoolDAO(); 
		TravelDAO tDao = new TravelDAO();
		PassengerTravel travel = tDao.get(travelId);
		cDao.removeByTravelWithState(travel, CarpoolingState.POTENTIAL);
		cDao.removeByTravelWithState(travel, CarpoolingState.IN_DEMAND);
		cDao.removeByTravelWithState(travel, CarpoolingState.REFUSED);
		for (Carpooling courseCarpool : cDao.getByTravel(travel)) {
			if(courseCarpool.getState() != CarpoolingState.IN_PROGRESS) {
				System.out.println("State changed");
				courseCarpool.setState(CarpoolingState.CONFLICT);
				cDao.update(courseCarpool);
			}
		}
		travel.setObsolete(true);
		System.out.println("remov");
		tDao.update(travel);
		System.out.println("end");
	}


	/**
	 * Update the actual position of the course and update the path
	 * @param courseId
	 * @param newPosition
	 */
	public static void updatePosition(int courseId, LatLng newPosition) {
		CourseDAO cDao = new CourseDAO();
		DriverCourse course = cDao.get(courseId);
		course.setActualPosition(newPosition);
		course.updatePath();
		cDao.update(course);
	}
	
	/**
	 * Update the actual position of the course and the path
	 * @param courseId
	 * @param newPosition
	 * @param encodedPoints
	 */
	public static void updateCourse(int courseId, LatLng newPosition, String encodedPoints) {
		CourseDAO cDao = new CourseDAO();
		DriverCourse course = cDao.get(courseId);
		course.setActualPosition(newPosition);
		course.setEncodedPoints(encodedPoints);
		cDao.update(course);
	}

	/**
	 * Get the actual states of the carpools related to the travel 
	 * @param travelId
	 * @return
	 */
	public static List<Carpooling> getTravel(int travelId) {
		TravelDAO tDao = new TravelDAO();
		CarpoolDAO carpoolDao = new CarpoolDAO();
		PassengerTravel travel= tDao.get(travelId);
		return carpoolDao.getByTravel(travel);
	}

	/**
	 * Get the actual states of the carpools related to the course
	 * @param courseId
	 * @return
	 */
	public static List<Carpooling> getCourse(int courseId) {
		CourseDAO cDao = new CourseDAO();
		CarpoolDAO carpoolDao = new CarpoolDAO();
		DriverCourse course = cDao.get(courseId);
		return carpoolDao.getByCourse(course, true);
	}
		
}
