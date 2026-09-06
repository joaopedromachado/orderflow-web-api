package br.com.orderflow.stub;

import br.com.orderflow.domain.user.Role;

public final class RoleStub {

    private RoleStub() {
    }

    public static Role basicRole() {
        final Role role = new Role();
        role.setRoleId(Role.Values.BASIC.getRole());
        role.setName(Role.Values.BASIC.name());
        return role;
    }
}
