package com.app.music_application.repositories;

import com.app.music_application.models.ListenedHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListenedHistoryRepository extends JpaRepository<ListenedHistory, Long> {
}
