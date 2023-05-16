package com.app.music_application.repositories;

import com.app.music_application.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByName(String name);
}
