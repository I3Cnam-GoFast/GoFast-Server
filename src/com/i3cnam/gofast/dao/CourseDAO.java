package com.i3cnam.gofast.dao;

//import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.google.android.gms.maps.model.LatLng;
import com.i3cnam.gofast.model.Carpooling;
import com.i3cnam.gofast.model.DriverCourse;
import com.i3cnam.gofast.model.Place;
import com.i3cnam.gofast.model.User;

//@Stateless
public class CourseDAO {
	@PersistenceContext( unitName = "jpa_project" )
	private EntityManager em;
	private static EntityManagerFactory factory;

	private EntityManagerFactory getFactory() {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory("jpa_project");
		}
		return factory;
	}
  
	private EntityManager getEntityManager() {
		if (em == null) {
			em = getFactory().createEntityManager();
		}
		return em;
	}
	
	public int create( DriverCourse course ) throws DAOException {
		try {
			getEntityManager().getTransaction().begin();
			

			// if they dont exist in database, persist origin and / or destination objects
			if (getEntityManager().find(Place.class, course.getOrigin().getPlaceId()) == null) {
				getEntityManager().persist(course.getOrigin());
			}
			if (getEntityManager().find(Place.class, course.getDestination().getPlaceId()) == null) {
				getEntityManager().persist(course.getDestination());
			}

			if (!course.boundsDefined()) {
				course.defineBounds();
			}
      	
			getEntityManager().persist( course );
			getEntityManager().getTransaction().commit();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return course.getId();
	}

	public void update( DriverCourse course ) throws DAOException {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().persist( course );
			getEntityManager().getTransaction().commit();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
	}

	public DriverCourse get(int id) {
		return getEntityManager().find(DriverCourse.class, id);
	}
	
	
	public void remove(DriverCourse course) {
		try {
		getEntityManager().getTransaction().begin();
		if (!getEntityManager().contains(course)) {
			course = getEntityManager().merge(course);
		}
		getEntityManager().remove(course);
		getEntityManager().getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DriverCourse getByUser(User user){
		DriverCourse course;
		try {
			Query	 query = getEntityManager().createQuery("SELECT c FROM DriverCourse c WHERE c.driver=:requesteduser");
	        query.setParameter( "requesteduser" , user);
	        course = (DriverCourse) query.getSingleResult();

	      } catch ( javax.persistence.NonUniqueResultException | javax.persistence.NoResultException e ) {
	          course = null;
	      } catch ( Exception e ) {
	          throw new DAOException( e );
	      }
		return course;
	}
	
	
	public List<DriverCourse> getAll() {
		List<DriverCourse> allCourses = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM DriverCourse c");
			allCourses = (List<DriverCourse>) query.getResultList();
      } catch ( Exception e ) {
          throw new DAOException( e );
      }
		return allCourses;
  }

	public List<DriverCourse> findByZone(LatLng point1, LatLng point2) {
		// calculate the input bounds 
	    double northest;
	    double southest;
	    double westest;
	    double eastest;
	    
		if (point1 != null) {
			if (point2 != null) {
	            northest = Math.max(point1.latitude, point2.latitude);
	            southest = Math.min(point1.latitude, point2.latitude);
	            westest = Math.max(point1.longitude, point2.longitude);
	            eastest = Math.min(point1.longitude, point2.longitude);
			}
			else {
	            northest = point1.latitude;
	            southest = point1.latitude;
	            westest = point1.longitude;
	            eastest = point1.longitude;				
			}
		}
		else {
			return null;
		}
		
		// init return variable
		List<DriverCourse> courses = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM DriverCourse c WHERE c.northest > " + northest + 
															" AND c.southest < " + southest +
															" AND c.westest > " + westest +
															" AND c.eastest < " + eastest	);
			courses = (List<DriverCourse>) query.getResultList();
      } catch ( Exception e ) {
          throw new DAOException( e );
      }
		return courses;
  }

}
