package com.apijavacourse.apijavacourse.dao;

import com.apijavacourse.apijavacourse.models.User;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class UserDaoImp implements UserDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<User> getUsers() {
        String query = "FROM User";
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public void remove(Long id) {
        User user = entityManager.find(User.class, id);
        entityManager.remove(user);
    }

    @Override
    public void register(User user) {
        entityManager.merge(user);
    }

    @Override
    public User findUserByCredentials(User user) {
        String query = "FROM User WHERE email = :email";
        List<User> userList = entityManager.createQuery(query)
                .setParameter("email", user.getEmail())
                .getResultList();

        if (userList.isEmpty()) return null;

        String password = userList.get(0).getPassword();
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

        if (!argon2.verify(password, user.getPassword().toCharArray())) return null;

        return userList.get(0);

    }

}
