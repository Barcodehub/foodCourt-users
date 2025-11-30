package com.pragma.powerup.infrastructure.security.util;

import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.infrastructure.security.userdetails.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUtil {

    public CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        throw new RuntimeException("Usuario no autenticado");
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUserRole() {
        RoleModel role = getCurrentUser().getRole();
        return role != null ? role.getName() : null;
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public boolean hasRole(String roleName) {
        try {
            String currentRole = getCurrentUserRole();
            return currentRole != null && currentRole.equalsIgnoreCase(roleName);
        } catch (Exception e) {
            return false;
        }
    }
}
