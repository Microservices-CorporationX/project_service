package ru.corporationx.projectservice.model.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentDto {

    @Positive
    private Long id;

    @NotNull(message = "Moment name must not be null")
    @NotBlank(message = "Moment name must not be blank")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;
    @NotNull(message = "Moment description must not be null")
    @NotBlank(message = "Moment description must not be blank")
    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;

    private LocalDateTime date;

    @NotEmpty
    private List<@NotNull Long> projectIds;

}
