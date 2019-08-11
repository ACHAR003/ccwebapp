package com.neu.library.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.neu.library.model.User;
@Service
public class UserDAO {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public void saveUser(User user) {
		
		this.entityManager.merge(user);
	}
	
	public User getUser(String username) {
		TypedQuery<User> query = this.entityManager.createQuery("SELECT u from User u WHERE u.username = ?1",User.class);
		query.setParameter(1,username);
		return query.getSingleResult();
	}
	
	public String getStoredPasswordFromUser(String email) {
		String hashed_pw = "";
		try {
			Query query = this.entityManager.createQuery("SELECT u FROM User u WHERE u.username = ?1");
			query.setParameter(1, email);
			List<User> resultList = query.getResultList();
			hashed_pw = resultList.get(0).getPassword();

		} catch (Exception e) {
			
			hashed_pw = null;

		}

		
		return hashed_pw;
	}
	@Transactional
	public int checkIfUserExists(String email) {
		
		int result = 0;
		try {
			Query query = this.entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = ?1");
			query.setParameter(1, email);
			Long resultInLong = (Long) query.getSingleResult();
			
			result = Math.toIntExact(resultInLong);
		} catch (Exception e) {
			
			result = 0;
		}

		return result;
}
}
