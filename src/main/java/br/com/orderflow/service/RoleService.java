package br.com.orderflow.service;

import br.com.orderflow.domain.security.Role;
import br.com.orderflow.repository.RoleRepository;
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
