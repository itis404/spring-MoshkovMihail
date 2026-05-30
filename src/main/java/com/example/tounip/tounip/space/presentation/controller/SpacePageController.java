package com.example.tounip.tounip.space.presentation.controller;

import com.example.tounip.tounip.channel.application.dto.ChannelDto;
import com.example.tounip.tounip.channel.application.dto.CreateChannelCommand;
import com.example.tounip.tounip.channel.application.service.ChannelService;
import com.example.tounip.tounip.channel.presentation.request.CreateChannelRequest;
import com.example.tounip.tounip.membership.application.service.MembershipService;
import com.example.tounip.tounip.message.application.dto.MessageDto;
import com.example.tounip.tounip.message.application.service.MessageService;
import com.example.tounip.tounip.security.current.CurrentUserProvider;
import com.example.tounip.tounip.space.application.dto.CreateSpaceCommand;
import com.example.tounip.tounip.space.application.dto.SpaceDto;
import com.example.tounip.tounip.space.application.service.SpaceService;
import com.example.tounip.tounip.space.presentation.form.CreateSpaceForm;
import com.example.tounip.tounip.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SpacePageController {

    private final CurrentUserProvider currentUserProvider;

    private final UserService userService;
    private final SpaceService spaceService;
    private final MembershipService membershipService;
    private final ChannelService channelService;
    private final MessageService messageService;

    @GetMapping("/spaces")
    public String spacesPage(Model model) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        model.addAttribute("profile", userService.findProfileById(currentUserId));
        model.addAttribute("mySpaces", spaceService.findMySpaces(currentUserId));
        model.addAttribute("publicSpaces", spaceService.findAllPublicSpaces());
        model.addAttribute("createSpaceForm", new CreateSpaceForm());

        return "spaces";
    }

    @PostMapping("/spaces")
    public String createSpace(
            @Valid CreateSpaceForm createSpaceForm,
            BindingResult bindingResult,
            Model model
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", userService.findProfileById(currentUserId));
            model.addAttribute("mySpaces", spaceService.findMySpaces(currentUserId));
            model.addAttribute("publicSpaces", spaceService.findAllPublicSpaces());
            model.addAttribute("createSpaceForm", createSpaceForm);

            return "spaces";
        }

        SpaceDto createdSpace = spaceService.createSpace(
                CreateSpaceCommand.builder()
                        .name(createSpaceForm.getName())
                        .description(createSpaceForm.getDescription())
                        .isPublic(createSpaceForm.getIsPublic())
                        .ownerId(currentUserId)
                        .build()
        );

        return "redirect:/spaces/" + createdSpace.getId();
    }

    @GetMapping("/spaces/{spaceId}")
    public String spacePage(
            @PathVariable UUID spaceId,
            @RequestParam(required = false) UUID channelId,
            Model model
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        SpaceDto space = spaceService.findByIdForUser(spaceId, currentUserId);
        boolean member = membershipService.isMember(spaceId, currentUserId);
        boolean canManageChannels = canManageChannels(spaceId, currentUserId);

        List<ChannelDto> channels = member
                ? channelService.findChannelsBySpace(spaceId, currentUserId)
                : List.of();

        ChannelDto selectedChannel = findSelectedChannel(channels, channelId);

        List<MessageDto> messages = selectedChannel == null
                ? List.of()
                : messageService.findMessagesByChannel(selectedChannel.getId(), currentUserId);

        model.addAttribute("space", space);
        model.addAttribute("member", member);
        model.addAttribute("channels", channels);
        model.addAttribute("selectedChannel", selectedChannel);
        model.addAttribute("messages", messages);
        model.addAttribute("members", member ? membershipService.findSpaceMembers(spaceId, currentUserId) : List.of());
        model.addAttribute("canManageChannels", canManageChannels);
        model.addAttribute("createChannelRequest", new CreateChannelRequest());

        return "space";
    }

    @PostMapping("/spaces/{spaceId}/join")
    public String joinSpace(
            @PathVariable UUID spaceId,
            RedirectAttributes redirectAttributes
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        try {
            membershipService.joinPublicSpace(spaceId, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage", "You joined the space");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/spaces/" + spaceId;
    }

    @PostMapping("/spaces/{spaceId}/channels")
    public String createChannel(
            @PathVariable UUID spaceId,
            @Valid CreateChannelRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Channel name is required");
            return "redirect:/spaces/" + spaceId;
        }

        try {
            ChannelDto channel = channelService.createChannel(
                    CreateChannelCommand.builder()
                            .spaceId(spaceId)
                            .creatorId(currentUserId)
                            .name(request.getName())
                            .description(request.getDescription())
                            .build()
            );

            return "redirect:/spaces/" + spaceId + "?channelId=" + channel.getId();
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/spaces/" + spaceId;
        }
    }

    private ChannelDto findSelectedChannel(List<ChannelDto> channels, UUID channelId) {
        if (channels.isEmpty()) {
            return null;
        }

        if (channelId == null) {
            return channels.get(0);
        }

        return channels.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElse(channels.get(0));
    }

    private boolean canManageChannels(UUID spaceId, UUID currentUserId) {
        try {
            membershipService.requireOwnerOrAdmin(spaceId, currentUserId);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }
}