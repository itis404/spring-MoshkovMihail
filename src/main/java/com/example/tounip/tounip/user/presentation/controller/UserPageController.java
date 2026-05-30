package com.example.tounip.tounip.user.presentation.controller;

import com.example.tounip.tounip.security.current.CurrentUserProvider;
import com.example.tounip.tounip.user.application.dto.UpdateUserProfileCommand;
import com.example.tounip.tounip.user.application.dto.UserProfileDto;
import com.example.tounip.tounip.user.application.service.UserService;
import com.example.tounip.tounip.user.presentation.form.UpdateProfileForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserPageController {

    private final CurrentUserProvider currentUserProvider;
    private final UserService userService;

    @GetMapping("/profile")
    public String profilePage(Model model) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        UserProfileDto profile = userService.findProfileById(currentUserId);

        UpdateProfileForm form = new UpdateProfileForm();
        form.setUsername(profile.getUsername());
        form.setPublicName(profile.getPublicName());
        form.setEmail(profile.getEmail());
        form.setPreferredLanguage(profile.getPreferredLanguage());

        model.addAttribute("profile", profile);
        model.addAttribute("profileForm", form);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @Valid UpdateProfileForm profileForm,
            BindingResult bindingResult,
            Model model
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", userService.findProfileById(currentUserId));
            model.addAttribute("profileForm", profileForm);
            return "profile";
        }

        try {
            UpdateUserProfileCommand command = UpdateUserProfileCommand.builder()
                    .username(profileForm.getUsername())
                    .publicName(profileForm.getPublicName())
                    .email(profileForm.getEmail())
                    .preferredLanguage(profileForm.getPreferredLanguage())
                    .build();

            userService.updateProfile(currentUserId, command);
            return "redirect:/profile?updated";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("profile", userService.findProfileById(currentUserId));
            model.addAttribute("profileForm", profileForm);
            model.addAttribute("profileError", exception.getMessage());
            return "profile";
        }
    }
}