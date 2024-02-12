package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.EventDto;
import faang.school.projectservice.dto.client.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${services.user-service.host}:${services.user-service.port}", path = "/api/v1/users")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping()
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);
}
