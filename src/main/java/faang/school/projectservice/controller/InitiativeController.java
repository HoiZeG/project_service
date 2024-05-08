package faang.school.projectservice.controller;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.service.initiative.InitiativeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/initiative")
@RequiredArgsConstructor
@Tag(name = "Initiatives")
public class InitiativeController {
    private final InitiativeService initiativeService;

    @Operation(summary = "Create new initiative")
    @PostMapping("/create")
    public InitiativeDto create(@RequestBody InitiativeDto initiative) {
        return initiativeService.create(initiative);
    }

    @Operation(summary = "Update initiative")
    @PutMapping("/update")
    public InitiativeDto update(@RequestBody InitiativeDto initiative) {
        return initiativeService.update(initiative);
    }

    @Operation(summary = "Get all initiatives by filter")
    @GetMapping
    public List<InitiativeDto> getAllByFilter(@RequestBody InitiativeFilterDto filters) {
        return initiativeService.getAllByFilter(filters);
    }

    @Operation(summary = "Get all initiatives")
    @GetMapping
    public List<InitiativeDto> getAll() {
        return initiativeService.getAll();
    }

    @Operation(summary = "Get initiative by id")
    @GetMapping("/{id}")
    public InitiativeDto getById(@PathVariable long id) {
        return initiativeService.getById(id);
    }
}