package io.github.seulgi.shipit.user.domain;

public enum RoleType {
    DEVELOPER("개발자"),
    TA("TA"),
    ADMIN("관리자");
    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
