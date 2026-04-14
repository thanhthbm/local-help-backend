package vn.localhelp.core.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.localhelp.core.util.constant.JobProgress;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_application")
@Data
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "helper_id", nullable = false)
    private User helper;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_progress")
    private JobProgress currentProgress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Progress> progressHistory;
}