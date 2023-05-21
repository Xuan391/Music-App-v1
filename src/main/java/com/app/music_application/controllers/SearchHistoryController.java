package com.app.music_application.controllers;

import com.app.music_application.models.ResponseObject;
import com.app.music_application.models.SearchHistory;
import com.app.music_application.repositories.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/SearchHistories")
public class SearchHistoryController {
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @GetMapping("/ShowAll")
    List<SearchHistory> getAllSearchHistory() {
        return searchHistoryRepository.findAll();
    }

    @GetMapping("/ShowHistoryByUserId")
    ResponseEntity<ResponseObject> getHistoryByUserId(@RequestParam("userId") Long userId) {
        List<SearchHistory> searchHistories = searchHistoryRepository.getSearchHistoriesById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("Ok","get searchHistories with userId successfully", searchHistories)
        );
    }

}
