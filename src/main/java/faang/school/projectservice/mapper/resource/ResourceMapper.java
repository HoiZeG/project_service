package faang.school.projectservice.mapper.resource;


import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    ResourceDto toDto(Resource resource);

    Resource toEntity(ResourceDto resourceDto);

}