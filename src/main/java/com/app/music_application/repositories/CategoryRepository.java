package com.app.music_application.repositories;

import com.app.music_application.models.Category;
import com.app.music_application.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByName(String productName);
}
