package com.i3cnam.gofast.dao;

//import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.i3cnam.gofast.model.User;

//@Stateless
public class UserDAO {
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
	
	public void create( User user) throws DAOException {
      try {
      	getEntityManager().getTransaction().begin();

      	getEntityManager().persist( user );
      	getEntityManager().getTransaction().commit();
      } catch ( Exception e ) {
          throw new DAOException( e );
      }
  }

	public User get(String nickName) {
		return getEntityManager().find(User.class, nickName);
	}

	public User getByPhone(String phone) {
		User user;
		try {
			Query query = getEntityManager().createQuery("SELECT u FROM User u WHERE u.phoneNumber = '" + phone + "'");
			user = (User) query.getSingleResult();
		} catch ( javax.persistence.NoResultException e ) {
			user = null;
		}

		return user;
	}
	
	public List<User> getAll() {
		List<User> allUsers = new ArrayList<>();
		try {
			Query query = getEntityManager().createQuery("SELECT u FROM User u");
			allUsers = (List<User>) query.getResultList();
      } catch ( Exception e ) {
          throw new DAOException( e );
      }
		return allUsers;
  }

}
