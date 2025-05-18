package ru.senla.javacourse.mutovin.messenger.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.CommunityCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;

import java.nio.file.AccessDeniedException;

public interface CommunityController {
    ResponseEntity<?> createCommunity(@RequestBody CommunityCreateRequest request);
    ResponseEntity<?> getCommunity(@PathVariable Long id);
    ResponseEntity<?> updateCommunity(@PathVariable Long id,@RequestBody CommunityCreateRequest request);
    ResponseEntity<?> createPostInCommunity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long communityId,
            @RequestBody PostCreateRequest request) throws AccessDeniedException;

    ResponseEntity<?> deleteCommunity(Long id);
    ResponseEntity<?> joinCommunity(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long communityId);
    ResponseEntity<?> leaveCommunity(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long communityId);
    ResponseEntity<?> getCommunityMembers(Long communityId);
    ResponseEntity<?> getCommunitiesByUser(Long userId);
}
