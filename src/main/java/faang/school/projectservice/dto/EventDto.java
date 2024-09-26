package faang.school.projectservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {
    private long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> attendeeEmails;
    private Long calendarEventId;
}