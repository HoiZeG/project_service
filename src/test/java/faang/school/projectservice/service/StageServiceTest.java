package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageDtoForUpdate;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.StageStatus;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @InjectMocks
    private StageService stageService;
    @Mock
    private StageRepository stageRepository;
    @Spy
    private StageMapper stageMapper;
    @Mock
    private StageDto stageDto;
    @Mock
    private StageValidator stageValidator;
    @Mock
    private Stage stage;
    private Stage stage1;
    private Stage stage2;
    private String status;
    private Long stageId;
    private StageDtoForUpdate stageDtoForUpdate;
    private Stage stageEntity;

    @BeforeEach
    void setUp() {
        stageId = 1L;
        status = "created";
        stage1 = Stage.builder().
                stageId(1L).
                status(StageStatus.CREATED)
                .build();
        stage2 = Stage.builder().
                stageId(2L).
                status(StageStatus.IN_PROGRESS)
                .build();
        stageDtoForUpdate = StageDtoForUpdate.builder().
                stageId(1L).
                stageName("stageName")
                .projectId(1L)
                .status("IN_PROGRESS")
                .teamRoles(List.of(TeamRole.valueOf("OWNER"), TeamRole.valueOf("MANAGER")))
                .build();
        stageEntity = Stage.builder().
                stageId(1L)
                .status(StageStatus.CREATED)
                .project(Project.builder().id(1L).build())
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.valueOf("OWNER")).count(1).build()))
                .build();
    }

    @Test
    void testMethodCreateStage() {
        when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        stageService.createStage(stageDto);

        verify(stageMapper, times(1)).toEntity(stageDto);
        verify(stageRepository, times(1)).save(stage);
        verify(stageMapper, times(1)).toDto(stage);
        verifyNoMoreInteractions(stageMapper);
        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    void testMethodGetAllStagesByStatus() {
        when(stageRepository.findAll()).thenReturn(new ArrayList<>(List.of(stage1, stage2)));
        when(stageMapper.toDto(stage1)).thenReturn(stageDto);

        stageService.getAllStagesByStatus(status);

        verify(stageRepository, times(1)).findAll();
        verify(stageMapper, times(1)).toDto(stage1);
        verifyNoMoreInteractions(stageRepository);
        verifyNoMoreInteractions(stageMapper);
    }

    @Test
    void testMethodDeleteStagesById() {
        stageRepository.deleteById(stageId);

        verify(stageRepository, times(1)).deleteById(stageId);
        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    void testMethodUpdateStage() {
        StageDto stageAfterUpdate = stageService.updateStage(stageDtoForUpdate);
        assertAll(() -> {
            assertEquals(stageDtoForUpdate.getStageName(), stageAfterUpdate.getStageName());
            assertEquals(stageDtoForUpdate.getProjectId(), stageAfterUpdate.getProjectId());
            assertEquals(stageDtoForUpdate.getStatus(), stageAfterUpdate.getStatus());
            assertEquals(stageDtoForUpdate.getTeamRoles().get(0).toString(),
                    stageAfterUpdate.getStageRolesDto().get(0).getTeamRole());
            assertEquals(stageDtoForUpdate.getTeamRoles().get(1).toString(),
                    stageAfterUpdate.getStageRolesDto().get(1).getTeamRole());
        });
    }

    @Test
    void testMethodGetAllStages() {
        when(stageRepository.findAll()).thenReturn((List.of(stage)));

        stageService.getAllStages();

        verify(stageRepository, times(1)).findAll();
        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    void testMethodGetStageById() {
        when(stageRepository.getById(stageId)).thenReturn(stage);

        stageService.getStageById(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    void testMethodGetStageById_ThrowExceptionAndMessage() {
        when(stageRepository.getById(stageId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> stageService.getStageById(stageId), "Stage not found by id: " + stageId);
    }
}