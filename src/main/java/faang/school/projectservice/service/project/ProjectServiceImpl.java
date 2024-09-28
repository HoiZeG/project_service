package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.filter.ProjectFilters;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ValidatorProject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final List<ProjectFilters> filters;
    private final ValidatorProject validator;

    @Override
    public void createProject(ProjectDto projectDto) {
        Project project = mapper.toEntity(projectDto);
        validator.validateProject(projectDto);
        validationDuplicateProjectNames(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
    }

    @Override
    public void updateStatus(ProjectDto projectDto, ProjectStatus status) {
        Project project = projectRepository.getProjectById(projectDto.getId());

        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(project);
    }

    @Override
    public void updateDescription(ProjectDto projectDto, String description) {
        Project project = projectRepository.getProjectById(projectDto.getId());

        project.setDescription(description);
        project.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(project);
    }

    @Override
    public List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto, TeamMemberDto requester) {
        Stream<Project> projectStream = projectRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projectStream,
                        (project, filter) -> filter.apply(project, filterDto),
                        (s1, s2) -> s1)
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PRIVATE)
                        && checkUserByPrivateProject(project, requester.getUserId()))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getProjects() {
        return projectRepository.findAll()
                .stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public boolean checkUserByPrivateProject(Project project, long requester) {
        Set<Long> teamMemberIds = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .collect(Collectors.toSet());

        return teamMemberIds.contains(requester);
    }

    @Override
    public ProjectDto findById(long id) {
        return mapper.toDto(projectRepository.getProjectById(id));
    }

    private List<Project> findByName(String name) {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .filter(project -> project.getName().equals(name))
                .toList();
    }

    private void validationDuplicateProjectNames(ProjectDto projectDto) {
        Project existingProject = findProjectByNameAndOwnerId(projectDto.getName(),
                projectRepository.getProjectById(projectDto.getId()).getOwnerId());

        if (existingProject != null && existingProject.getId().equals(projectDto.getId())) {
            throw new NoSuchElementException("This user already has a project with this name");
        }
    }

    private Project findProjectByNameAndOwnerId(String name, Long ownerId) {
        List<Project> projects = findByName(name);
        for (Project project : projects) {
            if (project.getOwnerId().equals(ownerId)) {
                return project;
            }
        }
        return null;
    }
}