package com.zidio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zidio.model.Upload;
import com.zidio.repository.UploadRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class UploadService {

    private final UploadRepository uploadRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public UploadService(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }

    public Upload storeAndParse(MultipartFile file, Long userId) throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
        String filename = UUID.randomUUID() + "-" + Objects.requireNonNull(file.getOriginalFilename());
        Path dest = Paths.get(uploadDir).resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }

        List<String> headers = new ArrayList<>();
        try (InputStream in = Files.newInputStream(dest);
             Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header != null) {
                for (Cell c : header) {
                    headers.add(c.getStringCellValue());
                }
            }
        }

        Upload up = new Upload();
        up.setUserId(userId);
        up.setFilename(filename);
        up.setFilepath(dest.toAbsolutePath().toString());
        up.setHeadersJson(mapper.writeValueAsString(headers));
        return uploadRepository.save(up);
    }

    public List<String> getHeaders(Long uploadId) throws IOException {
        var opt = uploadRepository.findById(uploadId);
        if (opt.isEmpty()) return Collections.emptyList();
        return mapper.readValue(opt.get().getHeadersJson(), List.class);
    }
}

// analyzeColumns method
public java.util.Map<String,Object> analyzeColumns(Long uploadId, String xCol, String yCol, String zCol) throws Exception {
    var up = uploadRepository.findById(uploadId).orElseThrow();
    Path p = Paths.get(up.getFilepath());
    java.util.List<Double> xs = new ArrayList<>();
    java.util.List<Double> ys = new ArrayList<>();
    java.util.List<Double> zs = zCol != null ? new ArrayList<>() : null;

    try (InputStream in = Files.newInputStream(p); Workbook wb = WorkbookFactory.create(in)) {
        Sheet sheet = wb.getSheetAt(0);
        Row header = sheet.getRow(0);
        if (header == null) throw new IllegalArgumentException("No header row");
        java.util.Map<String,Integer> idx = new java.util.HashMap<>();
        for (Cell c : header) idx.put(c.getStringCellValue(), c.getColumnIndex());
        Integer xi = idx.get(xCol);
        Integer yi = idx.get(yCol);
        Integer zi = zCol == null ? null : idx.get(zCol);
        if (xi==null || yi==null) throw new IllegalArgumentException("Columns not found");

        for (int r=1; r<=sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            Double xv = getNumericFromCell(row.getCell(xi));
            Double yv = getNumericFromCell(row.getCell(yi));
            Double zv = zi==null ? null : getNumericFromCell(row.getCell(zi));
            if (xv != null && yv != null && (zi==null || zv != null)) {
                xs.add(xv); ys.add(yv); if (zs!=null) zs.add(zv);
            }
        }
    }

    java.util.Map<String,Object> resp = new java.util.HashMap<>();
    resp.put("type", zs==null ? "scatter2d" : "scatter3d");
    resp.put("x", xs);
    resp.put("y", ys);
    if (zs!=null) resp.put("z", zs);
    resp.put("count", xs.size());
    resp.put("meta", java.util.Map.of("xColumn", xCol, "yColumn", yCol, "zColumn", zCol));
    return resp;
}

private Double getNumericFromCell(Cell cell) {
    if (cell == null) return null;
    try {
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            String s = cell.getStringCellValue().trim();
            if (s.isEmpty()) return null;
            return Double.parseDouble(s);
        }
    } catch (Exception ignored) {}
    return null;
}
