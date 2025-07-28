package io.github.seulgi.shipit.user.domain;

public enum TeamType {
    MAINTENANCE("유지관리팀"),
    OPS_1("운영1팀"),
    OPS_2("운영2팀"),
    NEW_BUILD("신규구축팀"),
    TRANSFER("운영전환팀");

    private final String teamName;

    TeamType(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }
}
