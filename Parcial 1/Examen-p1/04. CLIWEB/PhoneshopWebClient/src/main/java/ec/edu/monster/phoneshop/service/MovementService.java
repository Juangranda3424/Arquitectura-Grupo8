package ec.edu.monster.phoneshop.service;

import ec.edu.monster.phoneshop.dto.MovementDto;

import java.util.List;

public interface MovementService {
    List<MovementDto> getCurrentUserMovements();
}
