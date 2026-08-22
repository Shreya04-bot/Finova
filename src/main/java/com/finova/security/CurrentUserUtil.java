package com.finova.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserUtil {

    public static UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}