package ec.edu.monster.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private List<PermissionDto> permissions;
}
