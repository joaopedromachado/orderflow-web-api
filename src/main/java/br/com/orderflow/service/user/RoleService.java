package br.com.orderflow.service.user;

import br.com.orderflow.domain.user.Role;
import br.com.orderflow.repository.user.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(final String name) {
        return this.roleRepository.findRoleByName(name)
                .orElseThrow();
    }
}
