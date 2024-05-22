package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.*;
import faang.school.projectservice.mapper.InvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @Spy
    private InvitationMapper invitationMapper;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private StageRepository stageRepository;

    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;
    private StageInvitation stageInvitation1;
    private StageInvitation stageInvitation2;
    private StageInvitation stageInvitationFromDB;
    private InvitationFilterDto invitationFilterDto;
    private InvitationAuthorFilter invitationAuthorFilter;
    private InvitationStageFilter invitationStageFilter;
    private InvitationStatusFilter invitationStatusFilter;
    private Stage stage;
    private TeamMember teamMember;

    @BeforeEach
    public void setUp() {
        teamMember = TeamMember.builder().id(2L).userId(1L).build();
        stage = Stage.builder().stageId(1L).stageName("Name").executors(List.of(teamMember)).build();
        stageInvitationDto = StageInvitationDto.builder().id(1L).stageId(1L).authorId(1L).invitedId(1L).build();
        stageInvitation = StageInvitation.builder().id(1L).stage(stage).author(teamMember).invited(teamMember).build();
        stageInvitation1 = StageInvitation.builder().id(1L).stage(stage).author(teamMember).invited(teamMember).build();
        stageInvitation2 = StageInvitation.builder().id(1L).stage(new Stage()).author(teamMember).invited(teamMember).build();
        stageInvitationFromDB = StageInvitation.builder().id(1L).stage(stage).author(teamMember).invited(teamMember).build();
        invitationFilterDto = InvitationFilterDto.builder().stageId(1L).build();

        invitationAuthorFilter = Mockito.mock(InvitationAuthorFilter.class);
        invitationStageFilter = Mockito.mock(InvitationStageFilter.class);
        invitationStatusFilter = Mockito.mock(InvitationStatusFilter.class);
        List<InvitationFilter> invitationFilters = List.of(invitationAuthorFilter, invitationStageFilter, invitationStatusFilter);

        stageInvitationService = new StageInvitationService(stageInvitationValidator, invitationMapper,
                stageInvitationRepository, stageRepository, invitationFilters, teamMemberRepository);
    }

    @Test
    public void testCorrectWorkCreateInvitation() {
        when(stageRepository.getById(stageInvitationDto.getStageId())).thenReturn(stage);
        when(teamMemberRepository.findById(stageInvitationDto.getInvitedId())).thenReturn(teamMember);

        assertDoesNotThrow(() -> stageInvitationValidator.createValidationService(stage, teamMember));

        when(invitationMapper.toEntity(stageInvitationDto)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);
        when(invitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        StageInvitationDto result = stageInvitationService.createInvitation(stageInvitationDto);

        assertEquals(stageInvitationDto, result);
        verify(stageRepository).getById(stageInvitationDto.getStageId());
        verify(teamMemberRepository).findById(stageInvitationDto.getInvitedId());
        verify(invitationMapper).toEntity(stageInvitationDto);
        verify(stageInvitationRepository).save(stageInvitation);
        verify(invitationMapper).toDto(stageInvitation);
    }

    @Test
    public void testCreateInvitationWithRepeatStageInvitationDto() {
        when(stageRepository.getById(stageInvitationDto.getStageId())).thenReturn(stage);
        when(teamMemberRepository.findById(stageInvitationDto.getInvitedId())).thenReturn(teamMember);

        doThrow(DataValidationException.class).when(stageInvitationValidator).createValidationService(stage, teamMember);
        assertThrows(DataValidationException.class, () -> stageInvitationService.createInvitation(stageInvitationDto));
    }

    @Test
    public void testCreateInvitationWithStageNotAttendInDb() {
        doThrow(DataValidationException.class).when(stageRepository).getById((stageInvitationDto.getStageId()));
        assertThrows(DataValidationException.class, () -> stageInvitationService.createInvitation(stageInvitationDto));
    }

    @Test
    public void testCreateInvitationWithInvitedNotAttendInDb() {
        doThrow(DataValidationException.class).when(teamMemberRepository).findById((stageInvitationDto.getInvitedId()));
        assertThrows(DataValidationException.class, () -> stageInvitationService.createInvitation(stageInvitationDto));
    }

    @Test
    public void testCorrectWorkAcceptInvitation() {
        assertDoesNotThrow(() -> stageInvitationValidator.acceptOrRejectInvitationService(stageInvitationDto));
        when(invitationMapper.toEntity(stageInvitationDto)).thenReturn(stageInvitation);
        when(stageInvitationRepository.findById(anyLong())).thenReturn(stageInvitationFromDB);
        when(teamMemberRepository.findById(stageInvitationFromDB.getInvited().getId())).thenReturn(teamMember);
        when(invitationMapper.toDto(stageInvitationFromDB)).thenReturn(stageInvitationDto);

        StageInvitationDto result = stageInvitationService.acceptInvitation(stageInvitationDto);

        assertEquals(stageInvitationDto, result);
        verify(invitationMapper).toEntity(stageInvitationDto);
        verify(stageInvitationRepository).findById(anyLong());
        verify(invitationMapper).toDto(stageInvitationFromDB);
        verify(stageInvitationRepository).save(stageInvitationFromDB);
    }

    @Test void testAcceptInvitationWithIncorrectInput() {
        doThrow(DataValidationException.class).when(stageInvitationValidator).acceptOrRejectInvitationService(stageInvitationDto);
        assertThrows(DataValidationException.class, () -> stageInvitationService.acceptInvitation(stageInvitationDto));
    }

    @Test
    public void testCorrectWorkRejectInvitation() {
        assertDoesNotThrow(() -> stageInvitationValidator.acceptOrRejectInvitationService(stageInvitationDto));
        when(invitationMapper.toEntity(stageInvitationDto)).thenReturn(stageInvitation);
        when(stageInvitationRepository.findById(anyLong())).thenReturn(stageInvitationFromDB);
        when(invitationMapper.toDto(stageInvitationFromDB)).thenReturn(stageInvitationDto);

        StageInvitationDto result = stageInvitationService.rejectInvitation(stageInvitationDto);

        assertEquals(stageInvitationDto, result);
        verify(invitationMapper).toEntity(stageInvitationDto);
        verify(stageInvitationRepository).findById(stageInvitation.getId());
        verify(invitationMapper).toDto(stageInvitationFromDB);
    }

    @Test
    public void testRejectInvitationWithIncorrectInputInfo() {
        doThrow(DataValidationException.class).when(stageInvitationValidator).acceptOrRejectInvitationService(stageInvitationDto);
        assertThrows(DataValidationException.class, () -> stageInvitationService.rejectInvitation(stageInvitationDto));
    }

    @Test
    public void testCorrectWorkShowAllInvitationForMember() {
        List<StageInvitation> invitationList = new ArrayList<>();
        invitationList.add(stageInvitation);
        invitationList.add(stageInvitation1);
        invitationList.add(stageInvitation2);

        when(stageInvitationRepository.findAll()).thenReturn(invitationList);

        when(invitationAuthorFilter.isApplicable(invitationFilterDto)).thenReturn(false);
        when(invitationStageFilter.isApplicable(invitationFilterDto)).thenReturn(true);
        when(invitationStatusFilter.isApplicable(invitationFilterDto)).thenReturn(false);

        when(invitationStageFilter.apply(any(), eq(invitationFilterDto))).thenReturn(Stream.of(stageInvitation, stageInvitation1));

        List<StageInvitationDto> expected = Stream.of(stageInvitation, stageInvitation1).map(invitationMapper::toDto).toList();

        assertEquals(expected, stageInvitationService.showAllInvitationForMember(2L, invitationFilterDto));
    }

    @Test
    public void testShowAllInvitationForMemberWithMistakeValidation() {
        doThrow(DataValidationException.class).when(stageInvitationRepository).findAll();
        assertThrows(DataValidationException.class, () -> stageInvitationService.showAllInvitationForMember(2L, invitationFilterDto));
    }
}
