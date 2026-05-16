package vn.hunter.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hunter.job.domain.CvUpload;

@Repository
public interface CvUploadRepository extends JpaRepository<CvUpload, Long> {
}