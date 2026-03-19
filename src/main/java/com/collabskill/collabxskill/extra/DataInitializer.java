package com.collabskill.collabxskill.extra;

import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.repo.UserProfileRepo;
import com.collabskill.collabxskill.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserProfileRepo userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 1) {
            log.info("Dummy data already exists — skipping");
            return;
        }
        createDummyUsers();
        log.info("Dummy users created successfully");
    }

    private void createDummyUsers() {
        List<Object[]> dummyData = List.of(
                new Object[]{"alice@test.com", "BACKEND", "INTERMEDIATE", List.of("Spring", "Docker", "MySQL")},
                new Object[]{"bob@test.com", "FRONTEND", "BEGINNER", List.of("React", "CSS", "JavaScript")},
                new Object[]{"charlie@test.com", "BACKEND", "ADVANCED", List.of("Spring", "Kafka", "Redis")},
                new Object[]{"diana@test.com", "AI", "INTERMEDIATE", List.of("Python", "TensorFlow", "Docker")},
                new Object[]{"eve@test.com", "DEVOPS", "ADVANCED", List.of("Docker", "Kubernetes", "Jenkins")},
                new Object[]{"frank@test.com", "FRONTEND", "INTERMEDIATE", List.of("React", "TypeScript", "Redux")},
                new Object[]{"grace@test.com", "BLOCKCHAIN", "BEGINNER", List.of("Solidity", "Web3", "JavaScript")},
                new Object[]{"henry@test.com", "BACKEND", "BEGINNER", List.of("Spring", "MySQL", "REST")},
                new Object[]{"iris@test.com", "AI", "ADVANCED", List.of("Python", "PyTorch", "Kafka")},
                new Object[]{"jack@test.com", "FRONTEND", "ADVANCED", List.of("React", "Next.js", "TypeScript")},
                new Object[]{"kevin@test.com", "BACKEND", "INTERMEDIATE", List.of("Spring", "Docker", "Redis")},
                new Object[]{"luna@test.com", "AI", "BEGINNER", List.of("Python", "Pandas", "NumPy")},
                new Object[]{"mike@test.com", "DEVOPS", "INTERMEDIATE", List.of("Docker", "Jenkins", "Linux")},
                new Object[]{"nina@test.com", "FRONTEND", "BEGINNER", List.of("HTML", "CSS", "JavaScript")},
                new Object[]{"oscar@test.com", "BLOCKCHAIN", "ADVANCED", List.of("Solidity", "Rust", "Web3")},
                new Object[]{"priya@test.com", "BACKEND", "ADVANCED", List.of("Spring", "Kafka", "MySQL", "Docker")},
                new Object[]{"quinn@test.com", "AI", "INTERMEDIATE", List.of("Python", "TensorFlow", "Flask")},
                new Object[]{"raj@test.com", "DEVOPS", "BEGINNER", List.of("Docker", "Linux", "Git")},
                new Object[]{"sara@test.com", "FRONTEND", "INTERMEDIATE", List.of("React", "Vue", "TypeScript")},
                new Object[]{"tom@test.com", "BACKEND", "BEGINNER", List.of("Spring", "MySQL", "Java")}
        );


        dummyData.forEach(data -> {
            String email = (String) data[0];

            // User banao
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("test1234"));
            user.setVerified(true);
            userRepository.save(user);

            // Profile banao
            UserProfile profile = new UserProfile();
            profile.setUser(user);
            profile.setName(email.split("@")[0]);
            profile.setPrimaryDomain(PrimaryDomain.valueOf((String) data[1]));
            profile.setExperienceLevel(ExperienceLevel.valueOf((String) data[2]));
            profile.setTechStack((List<String>) data[3]);
            profile.setBio("I am a " + data[2] + " " + data[1] + " developer");
            profile.setGithubUrl("https://github.com/" + email.split("@")[0]);
            userProfileRepository.save(profile);
        });
    }
}
