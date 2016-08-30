package com.i3cnam.gofast.dao;

//import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.i3cnam.gofast.model.Carpooling;
import com.i3cnam.gofast.model.DriverCourse;
import com.i3cnam.gofast.model.PassengerTravel;
import com.i3cnam.gofast.model.Carpooling.CarpoolingState;
import com.i3cnam.gofast.model.User;

//@Stateless
public class CarpoolDAO {
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
	
	public int create( Carpooling carpooling ) throws DAOException {
		try {
			getEntityManager().getTransaction().begin();            	
			getEntityManager().persist( carpooling );
			getEntityManager().getTransaction().commit();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return carpooling.getId();
	}


	public void update( Carpooling carpooling ) throws DAOException {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().persist( carpooling );
			getEntityManager().getTransaction().commit();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
	}
		
	
	public Carpooling get(int id) {
		return getEntityManager().find(Carpooling.class, id);
	}

	
	public List<Carpooling> getAll() {
		List<Carpooling> allCarpoolings = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM Carpooling c");
			allCarpoolings = (List<Carpooling>) query.getResultList();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return allCarpoolings;
	}

	public List<Carpooling> getByTravel(PassengerTravel travel) {
		List<Carpooling> travelCarpoolings = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM Carpooling c WHERE c.passengerTravel=:requestedtravel");
	        query.setParameter( "requestedtravel" , travel );
			travelCarpoolings = (List<Carpooling>) query.getResultList();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return travelCarpoolings;
	}
	
	public List<Carpooling> getByCourse(DriverCourse course, boolean... excludePotentials) {
		boolean exclude = false;
		if (excludePotentials.length > 0) {exclude = excludePotentials[0];}
		List<Carpooling> courseCarpoolings = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM Carpooling c WHERE c.driverCourse=:requestedcourse" + ( exclude ? " AND NOT c.state=:potentialstate" : ""));
	        query.setParameter( "requestedcourse" , course);
	        if (exclude) {
	        	query.setParameter( "potentialstate" , CarpoolingState.POTENTIAL);
	        }
			courseCarpoolings = (List<Carpooling>) query.getResultList();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return courseCarpoolings;
	}

	public void removePotentialsByTravel(PassengerTravel travel) {
		try {
			Query query = getEntityManager().createQuery("DELETE FROM Carpooling c WHERE c.passengerTravel=:requestedtravel AND c.state=:potentialstate");
	        query.setParameter( "requestedtravel" , travel);
	        query.setParameter( "potentialstate" , CarpoolingState.POTENTIAL);
			getEntityManager().getTransaction().begin();            	
	        query.executeUpdate();
			getEntityManager().getTransaction().commit();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
	}


	public void removePotentialsByCourse(DriverCourse course) {
		try {
			Query query = getEntityManager().createQuery("DELETE FROM Carpooling c WHERE c.driverCourse=:requestedcourse AND c.state=:potentialstate");
	        query.setParameter( "requestedcourse" , course);
	        query.setParameter( "potentialstate" , CarpoolingState.POTENTIAL);
			getEntityManager().getTransaction().begin();            	
	        query.executeUpdate();
			getEntityManager().getTransaction().commit();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
	}


	public List<Carpooling> getByTravelAndDriver(User driver, PassengerTravel travel) {
		List<Carpooling> carpoolings = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM Carpooling c WHERE c.passengerTravel=:requestedtravel AND c.driverCourse.driver=:requesteddriver");
	        query.setParameter( "requestedtravel" , travel);
	        query.setParameter( "requesteddriver" , driver);
			carpoolings = (List<Carpooling>) query.getResultList();			
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		System.out.println(driver);
		System.out.println(travel);
		System.out.println(carpoolings.size());
		return carpoolings;
	}
	
}
