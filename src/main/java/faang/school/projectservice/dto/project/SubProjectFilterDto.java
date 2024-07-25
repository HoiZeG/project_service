package faang.school.projectservice.dto.project;


import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubProjectFilterDto {
    private String namePattern;
    private ProjectStatus status;
}