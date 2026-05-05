package vn.hunter.job.service;

import java.util.regex.*;
import org.springframework.stereotype.Service;
import vn.hunter.job.domain.CVData;

@Service
public class CvParserService {

    public CVData parse(String text) {
        CVData cv = new CVData();

        text = text.replaceAll("\\r", "");
        text = text.replaceAll("\\n+", "\n");
        text = text.replaceAll("\\s{2,}", " ");
        text = text.replaceAll("([0-9]{4})([A-ZÀ-Ỹ])", "$1 $2");
        Matcher emailMatcher = Pattern
                .compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
                .matcher(text);
        if (emailMatcher.find()) {
            cv.email = emailMatcher.group();
        }

        Matcher phoneMatcher = Pattern
                .compile("\\b0\\d{9}\\b")
                .matcher(text);
        if (phoneMatcher.find()) {
            cv.phone = phoneMatcher.group();
        }

        if (text.contains("Ha Noi") || text.contains("Hà Nội")) {
            cv.location = "Hà Nội";
        }
        if (text.contains("HCM")) {
            cv.location = "TP.HCM";
        }
        String[] skills = {
                "Java", "Spring Boot", "React", "TypeScript",
                "MySQL", "PostgreSQL", "MongoDB", "Python", "Typescript", "SQL Server", "C++", "C", "Nodejs", "Express",
                "NestJs", "NextJs"
        };

        for (String skill : skills) {
            if (text.toLowerCase().contains(skill.toLowerCase())) {
                cv.skills.add(skill);
            }
        }

        return cv;
    }
}