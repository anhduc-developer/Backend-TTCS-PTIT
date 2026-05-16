package vn.hunter.job.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hunter.job.domain.CvUpload;
import vn.hunter.job.repository.CvUploadRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CvUploadService {
    private final CvUploadRepository cvUploadRepository;
    private final ObjectMapper objectMapper;

    public CvUpload saveCvUpload(String fileName, String filePath, List<String> skills) {
        CvUpload cvUpload = new CvUpload();
        cvUpload.setFileName(fileName);
        cvUpload.setFilePath(filePath);
        try {
            cvUpload.setSkillsJson(objectMapper.writeValueAsString(skills));
        } catch (Exception e) {
            cvUpload.setSkillsJson("[]");
        }
        return cvUploadRepository.save(cvUpload);
    }

    public Optional<CvUpload> getCvUploadById(Long id) {
        return cvUploadRepository.findById(id);
    }

    public List<String> getSkillsFromCvUpload(CvUpload cvUpload) {
        try {
            return objectMapper.readValue(cvUpload.getSkillsJson(), new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }
}