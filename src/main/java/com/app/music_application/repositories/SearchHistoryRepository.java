package com.app.music_application.repositories;

import com.app.music_application.models.SearchHistory;
import com.app.music_application.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    SearchHistory findBySearchQuery(String searchQuery);
    SearchHistory findByUserIdAndSearchQuery(User user, String searchQuery);
    List<SearchHistory> findByUserId(User userId);
    @Query(value = "SELECT * FROM search_history WHERE user_id = :userId", nativeQuery = true)
    List<SearchHistory> getSearchHistoriesById(@Param("userId") Long id);

}
