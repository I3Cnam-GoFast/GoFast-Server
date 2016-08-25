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

	public List<Carpooling> getByTravel(int travelId) {
		List<Carpooling> travelCarpoolings = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM Carpooling c WHERE c.PassengerTravel=");
			travelCarpoolings = (List<Carpooling>) query.getResultList();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return travelCarpoolings;
	}
	
	public List<Carpooling> getByCourse() {
		List<Carpooling> courseCarpoolings = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT c FROM Carpooling c");
			courseCarpoolings = (List<Carpooling>) query.getResultList();
		} catch ( Exception e ) {
			throw new DAOException( e );
		}
		return courseCarpoolings;
	}

}
