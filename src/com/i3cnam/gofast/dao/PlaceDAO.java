package com.i3cnam.gofast.dao;

//import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.i3cnam.gofast.model.Place;

//@Stateless
public class PlaceDAO {
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
	
	public void create( Place place ) throws DAOException {
      try {
      	getEntityManager().getTransaction().begin();

      	getEntityManager().persist( place );
      	getEntityManager().getTransaction().commit();
      } catch ( Exception e ) {
          throw new DAOException( e );
      }
  }

	public Place get(String placeId) {
		return getEntityManager().find(Place.class, placeId);
	}

	public Place getByNom(String name) {
		Place place;
		Query query = getEntityManager().createQuery("SELECT p FROM Place p WHERE p.palcename = '" + name + "'");
		place = (Place) query.getSingleResult();
		return place;
	}
	
	public List<Place> getAll() {
		List<Place> allPlaces = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT p FROM Place p");
			allPlaces = (List<Place>) query.getResultList();
      } catch ( Exception e ) {
          throw new DAOException( e );
      }
		return allPlaces;
  }

}
