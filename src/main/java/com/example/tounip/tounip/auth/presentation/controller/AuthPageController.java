package com.example.tounip.tounip.auth.presentation.controller;

import com.example.tounip.tounip.auth.application.dto.RegisterCommand;
import com.example.tounip.tounip.auth.application.service.AuthService;
import com.example.tounip.tounip.auth.presentation.dto.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthPageController {

    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/spaces";
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/spaces";
        }

        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid RegistrationRequest request,
            BindingResult bindingResult,
            HttpServletRequest servletRequest,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationRequest", request);
            return "register";
        }

        try {
            RegisterCommand command = RegisterCommand.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .email(request.getEmail())
                    .preferredLanguage(request.getPreferredLanguage())
                    .password(request.getPassword())
                    .build();

            authService.register(
                    command,
                    extractUserAgent(servletRequest),
                    extractIpAddress(servletRequest)
            );

            return "redirect:/login?registered";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("registrationRequest", request);
            model.addAttribute("registrationError", exception.getMessage());
            return "register";
        }
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    private String extractUserAgent(HttpServletRequest request) {
        return request.getHeader(USER_AGENT_HEADER);
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
