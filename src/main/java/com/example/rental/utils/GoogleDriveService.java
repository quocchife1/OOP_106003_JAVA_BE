package com.example.rental.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GoogleDriveService {

    // ⚠️ Giả lập upload file lên Google Drive.
    // Ở môi trường thật, bạn sẽ dùng Google Drive API và OAuth2 credentials.json.
    private static final String DRIVE_FOLDER = "https://drive.google.com/drive/folders/1XZ15rpl3f5iEw_WUR0dZ712cXv0j-l2T";

    public String uploadFile(MultipartFile file, String filename) throws IOException {
        // Ở đây chỉ demo: thực tế bạn sẽ gọi Google API để upload file.
        System.out.println("Uploading file " + file.getOriginalFilename() + " to Google Drive...");
        return DRIVE_FOLDER + "/view?file=" + filename.replace(" ", "_");
    }
}
