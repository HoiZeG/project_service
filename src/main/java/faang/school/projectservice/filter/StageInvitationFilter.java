package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.List;

public interface StageInvitationFilter {
    List<StageInvitation> apply(List<StageInvitation> invitations, StageInvitationFilterDto filterDto);
}
