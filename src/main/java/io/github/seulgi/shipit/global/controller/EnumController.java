package io.github.seulgi.shipit.global.controller;

import io.github.seulgi.shipit.user.domain.RoleType;
import io.github.seulgi.shipit.user.domain.TeamType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/team-types")
    public ResponseEntity<List<Map<String, String>>> getTeamTypes() {
        List<Map<String, String>> result = Arrays.stream(TeamType.values())
                .map(team -> Map.of(
                        "name", team.name(),
                        "label", team.getTeamName()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/role-types")
    public ResponseEntity<List<Map<String, String>>> getRoleTypes() {
        List<Map<String, String>> result = Arrays.stream(RoleType.values())
                .map(role -> Map.of("name", role.name(), "label", role.getRoleName()))
                .toList();
        return ResponseEntity.ok(result);
    }

}
