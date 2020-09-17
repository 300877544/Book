package com.dexlock.book.repository;

import com.dexlock.book.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = "Select * from users where username=?",
            nativeQuery = true
    )
    public User findByUsername(String username);
}
