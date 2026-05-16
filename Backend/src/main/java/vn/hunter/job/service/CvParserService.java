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
                "Java", "Spring Boot", "React", "TypeScript", "JavaScript",
                "MySQL", "PostgreSQL", "MongoDB", "Python", "SQL Server", 
                "C\\+\\+", "C#", "C", "Nodejs", "Express", "NestJs", "NextJs",
                "Angular", "Vue", "AWS", "Docker", "Kubernetes", "PHP",
                "Ruby", "Go", "Golang", "Rust", "Swift", "Kotlin", ".NET"
        };

        for (String skill : skills) {
            String regex;
            if (skill.equals("C\\+\\+")) {
                regex = "(?i)\\bC\\+\\+";
            } else if (skill.equals("C#")) {
                regex = "(?i)\\bC#";
            } else if (skill.equals(".NET")) {
                regex = "(?i)\\.NET\\b";
            } else if (skill.equalsIgnoreCase("Nodejs")) {
                regex = "(?i)\\b(Nodejs|Node\\.js|Node js)\\b";
            } else if (skill.equalsIgnoreCase("React")) {
                regex = "(?i)\\b(React|Reactjs|React\\.js)\\b";
            } else if (skill.equalsIgnoreCase("Vue")) {
                regex = "(?i)\\b(Vue|Vuejs|Vue\\.js)\\b";
            } else {
                regex = "(?i)\\b" + Pattern.quote(skill) + "\\b";
            }

            if (Pattern.compile(regex).matcher(text).find()) {
                String normalizedSkill = skill.replace("\\+\\+", "++");
                if (!cv.skills.contains(normalizedSkill)) {
                    cv.skills.add(normalizedSkill);
                }
            }
        }

        return cv;
    }
}