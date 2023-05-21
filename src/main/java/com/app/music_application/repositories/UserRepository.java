package com.app.music_application.repositories;

import com.app.music_application.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM  User WHERE name LIKE %:searchText%", nativeQuery = true)
    List<User> searchUsersByName(@Param("searchText") String searchText);
}
