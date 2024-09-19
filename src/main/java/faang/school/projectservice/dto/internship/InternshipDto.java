package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InternshipDto(
        @Null(message = "Id must be null", groups = {CreateGroup.class, UpdateGroup.class})
        Long id,

        @NotNull(message = "", groups = {CreateGroup.class})
        Long projectId,

        @NotNull(message = "", groups = {CreateGroup.class})
        Long mentorId,

        @NotEmpty(message = "Interns list mustn't be empty", groups = {CreateGroup.class, UpdateGroup.class})
        List<Long> internsId,

        @NotBlank(message = "Name mustn't be blank", groups = {CreateGroup.class, UpdateGroup.class})
        String name,

        @NotNull(message = "Description mustn't be null", groups = {CreateGroup.class, UpdateGroup.class})
        String description,

        @NotNull(message = "Status mustn't be null", groups = {CreateGroup.class})
        InternshipStatus status,

        @NotNull(message = "Start date mustn't be null", groups = {CreateGroup.class})
        LocalDateTime startDate,

        @NotNull(message = "End date mustn't be null", groups = {CreateGroup.class})
        LocalDateTime endDate) {
}
