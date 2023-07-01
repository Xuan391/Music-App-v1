package com.app.music_application.repositories;

import com.app.music_application.models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    @Query(value = "SELECT * FROM  Playlist WHERE name LIKE %:searchText%", nativeQuery = true)
    List<Playlist> searchPlaylistByName(@Param("searchText") String searchText);

    @Query(value = "SELECT * FROM Playlist WHERE creator_id LIKE :userId", nativeQuery = true)
    List<Playlist> searchPlaylistByCreatorId(@Param("userId") Long userId);
}
