package com.zidio.controller;

import com.zidio.model.Upload;
import com.zidio.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {
    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file, Principal principal) throws Exception {
        Long userId = 1L;
        Upload up = uploadService.storeAndParse(file, userId);
        return ResponseEntity.ok(Map.of("uploadId", up.getId(), "headers", uploadService.getHeaders(up.getId())));
    }

    @GetMapping("/{id}/columns")
    public ResponseEntity<?> getColumns(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(Map.of("headers", uploadService.getHeaders(id)));
    }
}
