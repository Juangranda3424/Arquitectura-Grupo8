package ec.edu.monster.banquito.repository;

import ec.edu.monster.banquito.entity.UserProfile;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface UserProfileRepository extends ListCrudRepository<UserProfile, UUID> {}
