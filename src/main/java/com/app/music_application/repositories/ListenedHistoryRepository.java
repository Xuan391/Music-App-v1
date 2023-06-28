package com.app.music_application.repositories;

import com.app.music_application.models.ListenedHistory;
import com.app.music_application.models.SearchHistory;
import com.app.music_application.models.Song;
import com.app.music_application.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListenedHistoryRepository extends JpaRepository<ListenedHistory, Long> {
    List<ListenedHistory> findByUserId(User userId);
    List<ListenedHistory> getListenedHistoriesBySongId(Song songId);
}
