package ec.edu.monster.banquito.service.impl;

import ec.edu.monster.banquito.dto.MovementDto;
import ec.edu.monster.banquito.entity.User;
import ec.edu.monster.banquito.repository.MovementRepository;
import ec.edu.monster.banquito.service.MovementService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {
    private final MovementRepository movementRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<MovementDto> getCurrentUserMovements() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return movementRepository.findAllBySender(currentUser)
                .stream()
                .map(movement -> modelMapper.map(movement, MovementDto.class))
                .toList();
    }

    @Override
    public List<MovementDto> getMovementsByIdentificationNumber(String identificationNumber) {
        return movementRepository.findAllBySenderIdentificationNumber(identificationNumber)
                .stream()
                .map(movement -> modelMapper.map(movement, MovementDto.class))
                .toList();
    }
}
