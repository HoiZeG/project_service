package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/internship")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipValidator internshipValidator;

    @PostMapping("/")
    public InternshipDto saveNewInternship(@Valid @RequestBody InternshipDto internshipDto) {
        internshipValidator.validateControllerInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }

    @PutMapping("/{id}")
    public InternshipDto updateInternship(@Valid @RequestBody InternshipDto internshipDto, @Valid @PathVariable Long id) {
        internshipValidator.validateControllerInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }

    @PostMapping("/{projectId}/get-by-filter")
    public List<InternshipDto> findInternshipsWithFilter(@RequestBody InternshipFilterDto filterDto, @Valid @PathVariable Long projectId) {
        return internshipService.findInternshipsWithFilter(projectId, filterDto);
    }

    @GetMapping("/all")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{id}")
    public InternshipDto findAllInternshipById(@Valid @PathVariable Long id) {
        return internshipService.findInternshipById(id);
    }
}