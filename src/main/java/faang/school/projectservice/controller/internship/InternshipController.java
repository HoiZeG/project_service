package faang.school.projectservice.controller.internship;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

//        Создать стажировку. Стажировка ВСЕГДА относится к какому-то одному проекту.
//        Создать стажировку можно только в том случае, если есть кого стажировать.
//        При создании нужно проверить, что стажировка длится не больше 3 месяцев, и что у стажирующихся есть ментор из команды проекта.

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService service;

    public Internship createInternship(Internship internship) {
        createInternshipValidation(internship);
        return service.createInternship(internship);
    }

    private void createInternshipValidation(Internship internship) {
        if (internship == null) {
            throw new DataValidationException("Internship is null!");
        }
        if (internship.getName() == null || internship.getName().isBlank()) {
            throw new DataValidationException("Internship name can not be blank or null!");
        }
        if (internship.getProject() == null || internship.getProject().getId() < 1) {
            throw new DataValidationException("Internship relation project error!");
        }
    }
}
