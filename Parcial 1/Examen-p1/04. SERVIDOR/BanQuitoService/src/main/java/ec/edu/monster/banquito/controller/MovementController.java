package ec.edu.monster.banquito.controller;

import ec.edu.monster.banquito.dto.MovementDto;
import ec.edu.monster.banquito.service.MovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movements")
public class MovementController {
    private final MovementService movementService;

    @GetMapping
    public List<MovementDto> getMovements() {
        return movementService.getCurrentUserMovements();
    }
}
