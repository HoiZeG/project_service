package faang.school.projectservice.service.project.cover.impl;

import faang.school.projectservice.exception.InvalidFileException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.cover.ProjectCoverService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.config.ResourceConfig;
import faang.school.projectservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectCoverServiceImpl implements ProjectCoverService {
    private static final int BYTES_IN_MEGABYTE = 1024 * 1024;

    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ResourceConfig resourceConfig;

    @Override
    @Transactional
    public String uploadProjectCover(Long projectId, MultipartFile file) {
        log.info("Uploading cover for projectId: {}", projectId);
        log.info("Max file size: {} bytes", resourceConfig.getMaxSize());

        if (file.getSize() > resourceConfig.getMaxSize()) {
            throw new InvalidFileException("File size exceeds " +
                    (resourceConfig.getMaxSize() / BYTES_IN_MEGABYTE) + " MB");
        }

        if (!isImageFile(file)) {
            throw new InvalidFileException("File must be an image (JPEG, PNG, GIF)");
        }

        String fileExtension = FileUtils.getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;

        Project project = projectRepository.getProjectById(projectId);
        project.setCoverImageId(fileName);
        projectRepository.save(project);

        s3Service.uploadFile(file, fileName);

        log.info("Cover uploaded successfully with ID: {}", fileName);
        return fileName;
    }

    @Override
    @Transactional
    public void deleteProjectCover(Long projectId) {
        log.info("Deleting cover for projectId: {}", projectId);
        Project project = projectRepository.getProjectById(projectId);
        String coverImageId = project.getCoverImageId();
        if (coverImageId != null) {
            s3Service.deleteFile(coverImageId);
            project.setCoverImageId(null);
            projectRepository.save(project);
            log.info("Cover deleted successfully for projectId: {}", projectId);
        } else {
            throw new InvalidFileException("Project cover not found with ID: " + projectId);
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/png") ||
                        contentType.equalsIgnoreCase("image/gif")
        );
    }
}