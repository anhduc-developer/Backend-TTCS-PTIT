package vn.hunter.job.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hunter.job.domain.Resume;
import vn.hunter.job.domain.User;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume> {
    void deleteByUserId(Long id);

    List<Resume> findByJobId(Long jobId);
}
