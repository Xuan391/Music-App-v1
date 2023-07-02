package com.app.music_application.repositories;

import com.app.music_application.models.Category;
import com.app.music_application.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserName(String userName);
    @Query(value = "SELECT * FROM  users WHERE name LIKE %:searchText%", nativeQuery = true)
    List<User> searchUsersByName(@Param("searchText") String searchText);

    @Query(value = "SELECT * FROM users WHERE user_name LIKE :username AND password like :password AND is_admin = false", nativeQuery = true)
    List<User> checkLogin(@Param("username") String username, @Param("password") String password);

    @Query(value = "SELECT * FROM users WHERE user_name LIKE :username AND password LIKE :password AND is_admin = true", nativeQuery = true)
    List<User> findAdminUsers(@Param("username") String username, @Param("password") String password);
}
