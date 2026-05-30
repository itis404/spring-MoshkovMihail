package com.example.tounip.tounip.channel.presentation.controller;

import com.example.tounip.tounip.channel.application.dto.ChannelDto;
import com.example.tounip.tounip.channel.application.service.ChannelService;
import com.example.tounip.tounip.live.application.dto.LiveRoomJoinDto;
import com.example.tounip.tounip.live.application.service.LiveKitService;
import com.example.tounip.tounip.message.application.dto.CreateMessageCommand;
import com.example.tounip.tounip.message.application.dto.MessageDto;
import com.example.tounip.tounip.message.application.service.MessageService;
import com.example.tounip.tounip.message.presentation.request.CreateMessageRequest;
import com.example.tounip.tounip.security.current.CurrentUserProvider;
import com.example.tounip.tounip.translation.application.dto.TranslationDto;
import com.example.tounip.tounip.translation.application.service.TranslationService;
import com.example.tounip.tounip.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChannelPageController {

    private final CurrentUserProvider currentUserProvider;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final TranslationService translationService;
    private final LiveKitService liveKitService;
    private final UserService userService;

    @GetMapping("/channels/{channelId}")
    public String channelPage(@PathVariable UUID channelId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        ChannelDto channel = channelService.findById(channelId, currentUserId);

        return "redirect:/spaces/" + channel.getSpaceId() + "?channelId=" + channel.getId();
    }

    @PostMapping("/channels/{channelId}/messages")
    public String createMessage(
            @PathVariable UUID channelId,
            @Valid CreateMessageRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Message cannot be empty");
            return "redirect:/channels/" + channelId;
        }

        messageService.createMessage(
                CreateMessageCommand.builder()
                        .channelId(channelId)
                        .authorId(currentUserId)
                        .content(request.getContent())
                        .build()
        );

        return "redirect:/channels/" + channelId;
    }

    @PostMapping("/web/channels/{channelId}/messages")
    @ResponseBody
    public MessageDto createMessageAjax(
            @PathVariable UUID channelId,
            @Valid CreateMessageRequest request
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return messageService.createMessage(
                CreateMessageCommand.builder()
                        .channelId(channelId)
                        .authorId(currentUserId)
                        .content(request.getContent())
                        .build()
        );
    }

    @PostMapping("/web/messages/{messageId}/translate")
    @ResponseBody
    public TranslationDto translateMessage(
            @PathVariable UUID messageId,
            @RequestParam(required = false) String targetLang
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        String language = resolveTargetLanguage(currentUserId, targetLang);

        return translationService.translateMessage(messageId, currentUserId, language);
    }

    @PostMapping("/web/channels/{channelId}/live/join")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LiveRoomJoinDto joinLiveRoom(@PathVariable UUID channelId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return liveKitService.joinChannelLiveRoom(channelId, currentUserId);
    }

    private String resolveTargetLanguage(UUID currentUserId, String targetLang) {
        if (targetLang != null && !targetLang.isBlank()) {
            return targetLang.trim().toLowerCase();
        }

        return userService.findPreferredLanguage(currentUserId);
    }
}