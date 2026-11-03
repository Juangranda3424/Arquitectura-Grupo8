package ec.edu.monster.banquito.service;

import ec.edu.monster.banquito.dto.MovementDto;

import java.util.List;

public interface MovementService {
    List<MovementDto> getCurrentUserMovements();
    List<MovementDto> getMovementsByIdentificationNumber(String identificationNumber);
}
