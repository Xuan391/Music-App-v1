package com.app.music_application.repositories;

import com.app.music_application.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByName(String name);
    @Query(value = "SELECT * FROM  Song WHERE name LIKE %:searchText%", nativeQuery = true)
    List<Song> searchSongsByName(@Param("searchText") String searchText);


}
