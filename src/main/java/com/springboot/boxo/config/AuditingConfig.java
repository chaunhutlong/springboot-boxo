package com.springboot.boxo.config;

import com.springboot.boxo.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

class AuditorAwareImpl implements AuditorAware<User> {
    @Override
    public @NotNull Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of((User) authentication.getPrincipal());
    }
}
