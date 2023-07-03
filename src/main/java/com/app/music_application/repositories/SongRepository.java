package com.app.music_application.repositories;

import com.app.music_application.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByName(String name);
    @Query(value = "SELECT * FROM  song WHERE name LIKE %:searchText%", nativeQuery = true)
    List<Song> searchSongsByName(@Param("searchText") String searchText);

    @Query("SELECT s FROM Song s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate ORDER BY s.listenedCount DESC")
    List<Song> findTop10SongsByListenedCountAndDateRange(LocalDateTime startDate, LocalDateTime endDate);


}
