package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentValidator;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentValidator momentValidator;
    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public MomentDto createMoment(long momentId) {
        Moment moment = momentRepository.findById(momentId).orElseThrow();
        momentValidator.validateMoment(moment);
        moment.getProjects().forEach(projectValidator::validateProject);
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    public MomentDto updateMoment(long momentId, List<Long> addedProjectIds, List<Long> addedUserIds) {
        Moment moment = momentRepository.findById(momentId).orElseThrow();

        if (!addedProjectIds.isEmpty()) {
            addProjectsAndUsersToMoment(moment, addedProjectIds);
        }

        if (!addedUserIds.isEmpty()) {
            addUsersAndTheirProjectsToMoment(moment, addedUserIds);
        }

        momentValidator.validateMoment(moment);
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    private void addProjectsAndUsersToMoment(Moment moment, List<Long> projectIds) {
        List<Project> addedProjects = projectRepository.findAllByIds(projectIds);
        addedProjects.forEach(project -> {
            moment.getProjects().add(project);
            addUsersFromProjectToMoment(moment, project);
        });
    }

    private void addUsersFromProjectToMoment(Moment moment, Project project) {
        List<Team> teams = project.getTeams();
        teams.forEach(team -> {
            List<TeamMember> participants = team.getTeamMembers();
            participants.forEach(teamMember -> {
                moment.getUserIds().add(teamMember.getId());
            });
        });
    }

    private void addUsersAndTheirProjectsToMoment(Moment moment, List<Long> userIds) {
        List<TeamMember> addedUsers = teamMemberRepository.findByAllId(userIds);
        addedUsers.forEach(user -> {
            moment.getUserIds().add(user.getId());
            addProjectFromUserToMoment(moment, user);
        });
    }

    private void addProjectFromUserToMoment(Moment moment, TeamMember user) {
        Team team = user.getTeam();
        Project project = team.getProject();
        moment.getProjects().add(project);
    }

    public List<MomentDto> getAllProjectMomentsByDate(Long projectId, LocalDateTime month) {
        LocalDateTime endDate = month.plusMonths(1).minusDays(1);
        List<Moment> filteredMoments = projectRepository.getProjectById(projectId).getMoments().stream()
                .filter(moment -> moment.getCreatedAt().isAfter(month) && moment.getCreatedAt().isAfter(endDate))
                .toList();
        return momentMapper.toDtoList(filteredMoments);
    }

    public List<MomentDto> getAllMoments() {
        List<Moment> moments = momentRepository.findAll();
        return momentMapper.toDtoList(moments);
    }

    public MomentDto getMomentById(Long momentId) {
        Moment moment = momentRepository.findById(momentId).orElseThrow();
        return momentMapper.toDto(moment);
    }
}
