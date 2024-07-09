package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StageInvitationDtoValidator {
    private final StageInvitationRepository stageInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void validateAll(StageInvitationDto stageInvitationDto) {
        Long authorId = stageInvitationDto.getAuthorId();
        Long invitedId = stageInvitationDto.getInvitedId();

        validateEqualsId(authorId, invitedId);
        validateInvitedMemberTeam(authorId, invitedId);
    }

    public void validateEqualsId(Long authorId, Long invitedId) {
        if (authorId.equals(invitedId)) {
            throw new IllegalArgumentException("authorId can't be equals invitedIdd. \n" +
                    "authorId: " + authorId + "\n" +
                    "invitedId: " + invitedId);
        }
    }

    public void validateInvitedMemberTeam(Long authorId, Long invitedId) {
        Long authorTeamId = teamMemberRepository.findById(authorId).getTeam().getId();
        Long invitedTeamId = teamMemberRepository.findById(invitedId).getTeam().getId();

        if (!authorTeamId.equals(invitedTeamId)) {
            throw new RuntimeException("Stage invitation author and invited should be in one team.\n" +
                    "authorTeamId: " + authorTeamId + "\n" +
                    "invitedTeamId: " + invitedTeamId);
        }
    }
}
