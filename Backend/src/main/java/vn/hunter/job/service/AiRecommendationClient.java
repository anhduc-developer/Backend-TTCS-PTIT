package vn.hunter.job.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import vn.hunter.job.domain.response.AiResumeAnalysisDTO;

@Service
public class AiRecommendationClient {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public AiRecommendationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AiResumeAnalysisDTO analyzeResume(MultipartFile file) {
        String url = aiServiceUrl + "/resume/analyze";

        try {
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isBlank()) {
                filename = "resume.pdf";
            }
            final String resourceFilename = filename;

            byte[] fileBytes = file.getBytes();
            ByteArrayResource resource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return resourceFilename;
                }

                @Override
                public long contentLength() {
                    return fileBytes.length;
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            System.out.println("Sending request to: " + url);
            System.out.println("File name: " + filename);
            System.out.println("File size: " + file.getSize());

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

            org.springframework.http.HttpEntity<MultiValueMap<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(
                    body, headers);

            ResponseEntity<AiResumeAnalysisDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    AiResumeAnalysisDTO.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("AI service returned an invalid response: " + response.getStatusCode()
                        + " - " + response.getBody());
            }

            return response.getBody();
        } catch (Exception ex) {
            System.out.println("AI service error: " + ex.getMessage());
            throw new IllegalStateException("Failed to analyze resume with AI service", ex);
        }
    }
}
