package com.app.music_application.repositories;

import com.app.music_application.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByName(String name);
    @Query(value = "select s.id, s.created_at, s.download_count, s.listened_count, s.name, s.thumbnail_url, s.url, s.category_id, s.creator_id " +
            "from song as s left join category as c on s.category_id = c.id where s.name like %:searchText% or c.name like %:searchText%", nativeQuery = true)
    List<Song> searchSongsByName(@Param("searchText") String searchText);

    @Query("SELECT s FROM Song s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate ORDER BY s.listenedCount DESC")
    List<Song> findTop10SongsByListenedCountAndDateRange(LocalDateTime startDate, LocalDateTime endDate);


}
