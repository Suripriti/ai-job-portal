package jar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "job_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user who applied
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // job applied to
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    // stored resume file location
    private String resumePath;

    // AI similarity score
    private Double score;
}