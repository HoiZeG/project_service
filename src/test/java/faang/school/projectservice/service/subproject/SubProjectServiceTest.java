package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubProjectServiceTest {
    @InjectMocks
    private SubProjectService subProjectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private SubProjectMapper subProjectMapper;
    private SubProjectDto subProjectDto;
    private Project project = new Project();
    private Project parentProject = new Project();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Long rightId = 1L;

        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        project.setParentProject(parentProject);

        subProjectDto = subProjectDto.builder()
                .id(rightId)
                .description("Disc")
                .name("Name")
                .ownerId(rightId)
                .parentProjectId(rightId)
                .build();

        Mockito.when(subProjectMapper.toEntity(subProjectDto))
                .thenReturn(project);
    }

    @Test
    void testCreateProject() {
        subProjectService.createProject(subProjectDto);

        Mockito.verify(subProjectMapper, Mockito.times(1))
                .toEntity(subProjectDto);
        Mockito.verify(projectRepository, Mockito.times(1))
                .save(project);
    }

    @Test
    void testPrepareProjectForCreate() {
        subProjectService.createProject(subProjectDto);

        boolean timeTest = project.getCreatedAt().isBefore(LocalDateTime.now());
        assertTrue(timeTest);
        assertEquals(ProjectStatus.CREATED, project.getStatus());
        assertEquals(parentProject.getVisibility(), project.getVisibility());
    }
}