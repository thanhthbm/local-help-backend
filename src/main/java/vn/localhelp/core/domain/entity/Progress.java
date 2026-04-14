package vn.localhelp.core.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.localhelp.core.util.constant.JobProgress;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private JobProgress name;

    @Column(name = "description")
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}