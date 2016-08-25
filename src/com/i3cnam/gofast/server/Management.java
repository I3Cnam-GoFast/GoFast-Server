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

public class Management {

	public static int declareCourse(DriverCourse course) {
		CourseDAO cDao = new CourseDAO();
		return cDao.create(course);
	}
	
	public static int declareTravel(PassengerTravel travel) {
		TravelDAO tDao = new TravelDAO();
		return tDao.create(travel);
	}
	
	public static List<Carpooling> findMatches(int travelId) {
    	System.out.println("Management find matches");
		TravelDAO tDao = new TravelDAO();
		CarpoolDAO cDao = new CarpoolDAO();
		PassengerTravel travel = tDao.get(travelId);
		MatchFinder matcher = new MatchFinder(travel);
		List<Carpooling> matches = matcher.findMatches();
		// save potential carpoolings into database
		for (Carpooling carpool : matches) {
	    	System.out.println("Insert to DB ");
			cDao.create(carpool);
	    	System.out.println(carpool.getId());
		}
		return matches;
	}
	
	public static void requestCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.POTENTIAL) {
			carpooling.setState(CarpoolingState.IN_DEMAND);
			cDao.update(carpooling);
		}
	}
	
	public static void cancelCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.IN_DEMAND) {
			carpooling.setState(CarpoolingState.POTENTIAL);
			cDao.update(carpooling);
		}
	}
	
	public static void acceptCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.IN_DEMAND) {
			carpooling.setState(CarpoolingState.IN_PROGRESS);
			cDao.update(carpooling);
		}		
	}
	
	public static void refuseCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.IN_DEMAND) {
			carpooling.setState(CarpoolingState.REFUSED);
			cDao.update(carpooling);
		}		
	}
	
	public static void abortCarpooling(int carpoolId) {
		CarpoolDAO cDao = new CarpoolDAO();
		Carpooling carpooling = cDao.get(carpoolId);
		if (carpooling.getState() == CarpoolingState.IN_PROGRESS) {
			carpooling.setState(CarpoolingState.CONFLICT);
			cDao.update(carpooling);
		}		
	}
	
	public static void updatePosition(int courseId, LatLng newPosition) {
		CourseDAO cDao = new CourseDAO();
		DriverCourse course = cDao.get(courseId);
		course.setActualPosition(newPosition);
		course.updatePath();
		cDao.update(course);
	}
	
	public static void updateCourse(int courseId, LatLng newPosition, String encodedPoints) {
		CourseDAO cDao = new CourseDAO();
		DriverCourse course = cDao.get(courseId);
		course.setActualPosition(newPosition);
		course.setEncodedPoints(encodedPoints);
		cDao.update(course);
	}
	
	public static List<Carpooling> getTravel(int travelId) {
		TravelDAO tDao = new TravelDAO();
		CarpoolDAO carpoolDao = new CarpoolDAO();
		PassengerTravel travel= tDao.get(travelId);
		return carpoolDao.getByTravel(travel);
	}
	
	public static List<Carpooling> getCourse(int courseId) {
		CourseDAO cDao = new CourseDAO();
		CarpoolDAO carpoolDao = new CarpoolDAO();
		DriverCourse course = cDao.get(courseId);
		return carpoolDao.getByCourse(course, true);
	}
		
}
