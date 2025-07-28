package io.github.seulgi.shipit.user.domain;

public enum UserStatus {
    ACTIVE("활동 중"),
    WITHDRAWN("탈퇴함");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}