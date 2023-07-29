package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {
    private String name;
    private Long id;
    private Long mentorId;
    private List<Long> interns;
    private TaskStatus taskStatus;
    private InternshipStatus internshipStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
