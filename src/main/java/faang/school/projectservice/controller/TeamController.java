package faang.school.projectservice.controller;

import faang.school.projectservice.dto.team.CreateMembersDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.team.TeamMemberService;
import faang.school.projectservice.service.team.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Team management", description = "Operations related to changing team members, and getting info about project members")
public class TeamController {
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;
    private final TeamMemberMapper memberMapper;
    private final TeamMapper teamMapper;

    @GetMapping("/teams/{team-id}")
    @Operation(summary = "Returns all members in a team", description = "Returns a list of members, registered with a particular team")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully all team members registered for a team with provided ID"),
            @ApiResponse(responseCode = "404", description = "No team has been found for provided ID")})
    public List<TeamMemberDto> getMembersForTeam(@Parameter(description = "Unique team ID", required = true)
                                                 @PathVariable("team-id") Long teamId) {
        List<TeamMember> inTeam = teamMemberService.getMembersForTeam(teamId);
        return memberMapper.toDtoList(inTeam);
    }

    @PostMapping("/teams")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new team for project", description = "Creates a new team, registered for a specific project")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created a team for the provided project ID"),
            @ApiResponse(responseCode = "400", description = """
            
            """)})
    public TeamDto createTeamForProject(@Parameter(description = "Unique project ID", required = true)
                                        @RequestParam("project-id") Long projectId,
                                        @Parameter(description = "Optional info in case if team is to be instantiated with members")
                                        @RequestBody(required = false) @Valid CreateMembersDto dto) {
        Team created = teamService.createTeam(projectId, dto);
        return teamMapper.toDto(created);
    }

    @PutMapping("/teams/{team-id}")
    @Operation(summary = "Adds members to an existing team", description = "Creates members based on user ID and assigns them to a team")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created members assigned for a particular team")})
    public List<TeamMemberDto> addMembersToTeam(@Parameter(description = "Unique team ID", required = true)
                                                @PathVariable("team-id") Long teamId,
                                                @Parameter(required = true, description = "User IDs indicating users to be added to a team")
                                                @RequestBody @Valid CreateMembersDto dto) {
        List<TeamMember> added = teamMemberService.addToTeam(teamId, dto.getRole(), dto.getUserIds());
        return memberMapper.toDtoList(added);
    }

    @PutMapping("/members/{member-id}/nickname")
    @Operation(summary = "Update nickname a team member", description = "Updates nickname of a single member in a team, based on the unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saves and return updated user info")})
    public TeamMemberDto updateMemberNickname(@Parameter(description = "Unique team member ID", required = true)
                                              @PathVariable("member-id") Long memberId,
                                              @Parameter(description = "A new member nickname", required = true)
                                              @RequestParam @Valid @NotBlank String nickname) {
        TeamMember updatedMember = teamMemberService.updateMemberNickname(memberId, nickname);
        return memberMapper.toDto(updatedMember);
    }

    @PutMapping("/members/{member-id}/roles")
    @Operation(summary = "Update info of a team member", description = "Updates info about a single member in a team, based on the unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saves and return updated user info")})
    public TeamMemberDto updateMemberRoles(@Parameter(description = "Unique team member ID", required = true)
                                           @PathVariable("member-id") Long memberId,
                                           @Parameter(description = "New roles for the team member")
                                           @RequestParam List<TeamRole> roles) {
        TeamMember updatedMember = teamMemberService.updateMemberRoles(memberId, roles);
        return memberMapper.toDto(updatedMember);
    }

    @PutMapping("/projects/{project-id}/removeMembers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Get all team members for provided user", description = "Returns all team members registered for the provided user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned members for provided user ID")})
    public void removeMembersFromProject(@Parameter(description = "Unique project ID", required = true)
                                         @PathVariable("project-id") Long projectId,
                                         @Parameter(description = "IDs of team members to be removed from project", required = true)
                                         @RequestBody List<Long> memberIds) {
        teamMemberService.removeFromProject(projectId, memberIds);
    }

    @PostMapping("/projects/{project-id}/members")
    @Operation(summary = "Get all team members for a given project", description = "Returns all team members contributing to a project with provided ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned members for provided project ID"),
            @ApiResponse(responseCode = "404", description = "Couldn't find a project for provided ID")})
    public List<TeamMemberDto> getTeamMembersForProject(@Parameter(description = "Unique project ID")
                                                        @PathVariable("project-id") Long projectId,
                                                        @Parameter(description = "Team member nickname pattern")
                                                        @RequestParam(required = false) String nickname,
                                                        @Parameter(description = "Team member role pattern")
                                                        @RequestParam(required = false) TeamRole role) {
        List<TeamMember> filteredMembers = teamMemberService.getProjectMembersFiltered(projectId, nickname, role);
        return memberMapper.toDtoList(filteredMembers);
    }

    @GetMapping("/members")
    @Operation(summary = "Get all team members for given user", description = "Returns all team members registered for the provided user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned members for provided user ID")})
    public List<TeamMemberDto> teamMembersForUser(@Parameter(description = "Unique user ID")
                                                  @RequestParam("user-id") @Valid @NotNull Long userId) {
        List<TeamMember> sameUserMembers = teamMemberService.getMembersForUser(userId);
        return memberMapper.toDtoList(sameUserMembers);
    }
}
