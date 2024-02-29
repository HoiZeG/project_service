package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.s3.CoverHandler;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ProjectService projectService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final CoverHandler coverHandler;
    private final ProjectValidator projectValidator;


    public ResourceDto addCoverToProject(long projectId, long userId, MultipartFile file) {
        Project project = projectService.getById(projectId);
        TeamMember teamMember = teamMemberRepository.findById(userId);

        coverHandler.checkCoverSize(file.getSize());

        BigInteger occupiedCapacity = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        projectValidator.checkStorageSizeExceeded(project.getMaxStorageSize(), occupiedCapacity);

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource = resourceRepository.save(resource);

        project.setStorageSize(occupiedCapacity);
        project.setCoverImageId(resource.getKey());
        projectService.save(project);

        return resourceMapper.toDto(resource);
    }

    public InputStream downloadCoverByProjectId(long projectId) {
        Resource resource = resourceRepository.findResourceByProjectId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Ресурс не найден"));
        return s3Service.downloadFile(resource.getKey());
    }
}
