package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
uses = TeamMapper.class)
public interface ProjectMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "storageSize", target = "storageSize")
    @Mapping(source = "maxStorageSize", target = "maxStorageSize")
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(source = "parentProject", target = "parentProject")
    @Mapping(source = "children", target = "children")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(source = "coverImageId", target = "coverImageId")
    @Mapping(source = "resourceIds", target = "resources", qualifiedByName = "mapToResources")
    @Mapping(source = "momentIds", target = "moments", qualifiedByName = "mapToMoments")
    @Mapping(source = "teams", target = "teams")
    Project toEntity(ProjectDto projectDto);

    List<Project> toEntity(List<ProjectDto> projects);

    List<Team> toTeamEntity(List<TeamDto> teams);

    @Named("mapToResources")
    default List<Resource> mapToResources(List<Long> resourceIds) {
        if (resourceIds == null) {
            return null;
        }
        return resourceIds.stream()
                .map(resourceId -> {
                    Resource resource = new Resource();
                    resource.setId(resourceId);
                    return resource;
                })
                .toList();
    }

    @Named("mapToMoments")
    default List<Moment> mapToMoments(List<Long> momentIds) {
        if (momentIds == null) {
            return null;
        }
        return momentIds.stream()
                .map(momentId -> {
                    Moment moment = new Moment();
                    moment.setId(momentId);
                    return moment;
                })
                .toList();
    }


    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "storageSize", target = "storageSize")
    @Mapping(source = "maxStorageSize", target = "maxStorageSize")
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(source = "parentProject", target = "parentProject")
    @Mapping(source = "children", target = "children")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(source = "coverImageId", target = "coverImageId")
    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapToResourceIds")
    @Mapping(source = "moments", target = "momentIds", qualifiedByName = "mapToMomentIds")
    @Mapping(source = "teams", target = "teams")
    ProjectDto toDto(Project project);

    List<ProjectDto> toDto(List<Project> projects);
    List<TeamDto> toDtoTeam(List<Team> teams);

    @Named("mapToResourceIds")
    default List<Long> mapToResourceIds(List<Resource> resources) {
        if (resources == null) {
            return null;
        }
        return resources.stream()
                .map(Resource::getId)
                .toList();
    }

    @Named("mapToMomentIds")
    default List<Long> mapToMomentIds(List<Moment> moments) {
        if (moments == null) {
            return null;
        }
        return moments.stream()
                .map(Moment::getId)
                .toList();
    }
}
