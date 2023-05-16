package com.app.music_application.services;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface IStorageService {
    public String storeFile(MultipartFile file);
    public Stream<Path> loadAll(); // load All file inside a folder
    public byte[] readFileContent(String fileName);
    // hàm readFileContent trả về mảng các byte, từ mảng các byte để xem ảnh
    public void deleteAllFiles();
}
