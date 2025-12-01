package com.zidio.controller;

import com.zidio.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analyses")
public class AnalysisController {
    private final UploadService uploadService;

    public AnalysisController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<?> analyze(@RequestBody Map<String,String> body) throws Exception {
        Long uploadId = Long.parseLong(body.get("uploadId"));
        String x = body.get("xColumn");
        String y = body.get("yColumn");
        String z = body.get("zColumn");
        Map<String,Object> result = uploadService.analyzeColumns(uploadId, x, y, z);
        return ResponseEntity.ok(result);
    }
}
