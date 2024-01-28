package faang.school.projectservice.validator.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectValidator {
    private final ProjectRepository projectRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public void validateName(String name) {
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Name of project cannot be empty or blank");
        }
    }

    public void validateDescription(String description) {
        if (description != null && (description.isEmpty() || description.isBlank())) {
            throw new IllegalArgumentException("Description of project cannot be empty or blank");
        }
    }

    public void validateAccessToProject(long ownerId) {
        if (!haveAccessToProject(ownerId)) {
            throw new SecurityException("User is not the owner of the project");
        }
    }

    public boolean haveAccessToProject(long ownerId) {
        return userContext.getUserId() == ownerId;
    }

    public void validateNameExistence(long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            throw new IllegalArgumentException("Project with this name already exists. Name: " + name);
        }
    }

    public void validateUserExistence(long ownerId) {
        UserDto user = userServiceClient.getUser(ownerId);
        if (user == null) {
            throw new EntityNotFoundException("User with id = " + ownerId + " not found");
        }
    }
}