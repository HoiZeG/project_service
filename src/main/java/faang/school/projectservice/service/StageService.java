package faang.school.projectservice.service;

import faang.school.projectservice.dto.MethodDeletingStageDto;
import faang.school.projectservice.dto.ProjectStatusFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.projectstatusfilter.ProjectStatusFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;

    private final TaskRepository taskRepository;

    private final StageMapper stageMapper;

    private final List<ProjectStatusFilter> projectStatusFilters;

    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public StageDto createStage(StageDto stageDto) {
        validationStageDto(stageDto);
        Stage save = stageRepository.save(stageMapper.toEntity(stageDto));
        return stageMapper.toDto(save);
    }

    public List<StageDto> getStagesByProjectStatus(ProjectStatusFilterDto filter) {
        Stream<Stage> allStages = stageRepository.findAll().stream();
        List<ProjectStatusFilter> projectStatusFiltersIsApplicable = projectStatusFilters.stream()
                .filter(projectStatusFilter -> projectStatusFilter.isApplicable(filter))
                .toList();
        for (ProjectStatusFilter projectStatusFilter : projectStatusFiltersIsApplicable) {
            allStages = projectStatusFilter.apply(allStages, filter);
        }
        return allStages.map(stageMapper::toDto).toList();
    }

    public void deleteStage(Long id, MethodDeletingStageDto methodDeletingStageDto, Long stageId) {
        Stage stageById = stageRepository.getById(id);
        List<Task> tasks = stageById.getTasks();
        if (MethodDeletingStageDto.CASCADE.equals(methodDeletingStageDto)) {
            taskRepository.deleteAll(tasks);
        } else if (MethodDeletingStageDto.CLOSE.equals(methodDeletingStageDto)) {
            tasks.forEach(task -> task.setStatus(TaskStatus.DONE));
        } else if (MethodDeletingStageDto.MOVE_TO_NEXT_STAGE.equals(methodDeletingStageDto)) {
            stageById.setTasks(List.of());
            Stage stageByIdForAddTasks = stageRepository.getById(stageId);
            if (stageByIdForAddTasks.getTasks() == null) {
                stageByIdForAddTasks.setTasks(new ArrayList<>());
            }
            stageByIdForAddTasks.getTasks().addAll(tasks);
            stageRepository.save(stageByIdForAddTasks);
        }
        stageRepository.delete(stageById);
    }

    public void updateStageRoles(Long id, StageRolesDto stageRoles) {
        Stage stageById = stageRepository.getById(id);
        int countTeamRoles = getTotalTeamRoles(stageRoles, stageById);
        if (countTeamRoles >= stageRoles.getCount()) {
            throw new DataValidationException(stageRoles.getTeamRole().name() + " no longer required");
        } else {
            invitationMembersToStage(stageRoles, stageById, countTeamRoles);
        }
    }

    private void invitationMembersToStage(StageRolesDto stageRoles, Stage stageById, int countTeamRoles) {
        List<TeamMember> teamMembersInProject = teamMemberJpaRepository.findByProjectId(stageById.getProject().getId());
        teamMembersInProject.stream()
                .filter(teamMember -> teamMember.getStages().stream()
                        .noneMatch(stage -> stage.getStageId().equals(stageById.getStageId())))
                .filter(teamMember -> teamMember.getRoles().contains(stageRoles.getTeamRole()))
                .limit(stageRoles.getCount() - countTeamRoles)
                .forEach(teamMember -> teamMember.getStages().add(stageById));
        changeStageRolesToActual(stageRoles, stageById, countTeamRoles);
        teamMemberJpaRepository.saveAll(teamMembersInProject);
    }

    private void changeStageRolesToActual(StageRolesDto stageRoles, Stage stageById, int countTeamRoles) {
        stageById.getStageRoles().stream()
                .filter(stageRole -> stageRole.getTeamRole().equals(stageRoles.getTeamRole()))
                .findFirst()
                .ifPresent(stageRole -> stageRole.setCount(stageRole.getCount() - countTeamRoles + stageRoles.getCount()));
        stageRepository.save(stageById);
    }

    private int getTotalTeamRoles(StageRolesDto stageRoles, Stage stageById) {
        return stageById.getStageRoles().stream()//количество разработчиков на этапе
                .filter(stageRole -> stageRole.getTeamRole().equals(stageRoles.getTeamRole()))
                .mapToInt(StageRoles::getCount)
                .sum();
    }

    private void validationStageDto(StageDto stageDto) {
        if (stageDto.getProject().getStatus().equals(ProjectStatus.COMPLETED) ||
                stageDto.getProject().getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException("You cannot create a stage in a closed or canceled project");
        }

    }
}
