package com.zidio.repository;

import com.zidio.model.Upload;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UploadRepository extends JpaRepository<Upload, Long> {
    List<Upload> findByUserId(Long userId);
}
