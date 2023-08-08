package faang.school.projectservice.dto.moment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentDto {
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private Long createdBy;
    private List<Long> userIds;
}
