package faang.school.projectservice.dto;

import faang.school.projectservice.dto.client.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventDto {
    @Positive
    private long id;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @NotNull
    private UserDto owner;
    private List<String> attendeeEmails;
    private String calendarEventId;
}