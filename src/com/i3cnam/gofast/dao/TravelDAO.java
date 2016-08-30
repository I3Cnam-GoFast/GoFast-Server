package com.i3cnam.gofast.dao;

//import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.i3cnam.gofast.model.DriverCourse;
import com.i3cnam.gofast.model.PassengerTravel;
import com.i3cnam.gofast.model.Place;
import com.i3cnam.gofast.model.User;

//@Stateless
public class TravelDAO {
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
	
	public int create( PassengerTravel travel ) throws DAOException {
		try {
			getEntityManager().getTransaction().begin();
      	
			// if they dont exist in database, persist origin and / or destination objects
			if (getEntityManager().find(Place.class, travel.getOrigin().getPlaceId()) == null) {
				getEntityManager().persist(travel.getOrigin());
			}
			if (getEntityManager().find(Place.class, travel.getDestination().getPlaceId()) == null) {
				getEntityManager().persist(travel.getDestination());
			}
      	
			getEntityManager().persist( travel );
			getEntityManager().getTransaction().commit();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return travel.getId();
	}

	public PassengerTravel get(int id) {
		return getEntityManager().find(PassengerTravel.class, id);
	}


	public void remove(PassengerTravel travel) {
		try {
		getEntityManager().getTransaction().begin();
		if (!getEntityManager().contains(travel)) {
			travel = getEntityManager().merge(travel);
		}
		getEntityManager().remove(travel);
		getEntityManager().getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PassengerTravel getByUser(User user){
		PassengerTravel travel;
		try {
			Query	 query = getEntityManager().createQuery("SELECT t FROM PassengerTravel t WHERE t.passenger=:requesteduser");
	        query.setParameter( "requesteduser" , user);
	        travel = (PassengerTravel) query.getSingleResult();
		} catch ( javax.persistence.NoResultException e ) {
			travel = null;
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return travel;
	}
	
	
	public List<PassengerTravel> getAll() {		
		List<PassengerTravel> allTravels = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT t FROM PassengerTravel t");
			allTravels = (List<PassengerTravel>) query.getResultList();
		} catch ( Exception e ) {
			throw new DAOException( e );
      	}
		return allTravels;
	}
	
}
