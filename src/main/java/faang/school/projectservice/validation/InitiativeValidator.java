package faang.school.projectservice.validation;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitiativeValidator {
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void validateCurator(InitiativeDto initiative) {
        TeamMember curator = teamMemberRepository.findById(initiative.getCuratorId());

        if (!curator.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException("curator must have owner role");
        }

        if (!curator.getTeam().getProject().getId().equals(initiative.getProjectId())) {
            throw new DataValidationException("curator not in the initiative project");
        }
    }

    public void validateClosedInitiative(InitiativeDto initiative) {
        List<Stage> stages = stageRepository.findAll().stream()
                .filter(stage -> initiative.getStageIds().contains(stage.getStageId()))
                .toList();

        boolean areAllClosed = stages.stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus() == TaskStatus.DONE);

        if (!areAllClosed) {
            throw new DataValidationException("All tasks in all stages must be done to close the initiative");
        }
    }
}
