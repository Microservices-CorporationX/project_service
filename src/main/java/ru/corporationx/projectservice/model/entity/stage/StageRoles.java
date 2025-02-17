package ru.corporationx.projectservice.model.entity.stage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.corporationx.projectservice.model.entity.TeamRole;

@Entity
@Table(name = "project_stage_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamRole teamRole;

    @Column(name = "count", nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "project_stage_id", nullable = false)
    private Stage stage;
}
