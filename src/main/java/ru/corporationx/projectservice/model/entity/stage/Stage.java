package ru.corporationx.projectservice.model.entity.stage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.corporationx.projectservice.model.entity.Project;
import ru.corporationx.projectservice.model.entity.Task;
import ru.corporationx.projectservice.model.entity.TeamMember;

import java.util.List;

@Entity
@Table(name = "project_stage")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class Stage {
    @Id
    @Column(name = "project_stage_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stageId;

    @Column(name = "project_stage_name", nullable = false)
    private String stageName;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL)
    private List<StageRoles> stageRoles;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "stage_id")
    private List<Task> tasks;

    @ManyToMany
    @JoinTable(name = "project_stage_executors",
            joinColumns = @JoinColumn(name = "stage_id"),
            inverseJoinColumns = @JoinColumn(name = "executor_id"))
    private List<TeamMember> executors;
}