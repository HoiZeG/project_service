package faang.school.projectservice.validation.team_member;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberValidatorImpl implements TeamMemberValidator {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public void validateExistence(long id) {
        boolean exists = teamMemberRepository.existsById(id);
        if (!exists) {
            var message = String.format("a team member with %d does not exist", id);

            throw new DataValidationException(message);
        }
    }
}