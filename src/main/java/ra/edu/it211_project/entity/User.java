package ra.edu.it211_project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import ra.edu.it211_project.entity.TokenBlacklist;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Submission> studentSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "lecturer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Submission> gradedSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TokenBlacklist> tokenBlacklists = new ArrayList<>();

    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Course> enrolledCourses = new ArrayList<>();
}
