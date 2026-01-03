package com.teambind.springproject.auth;

/**
 * 사용자 역할 enum
 * PLACEOWNER_GATEWAY는 PLACE_OWNER, ADMIN만 접근 가능
 */
public enum Role {
    USER,
    PLACE_OWNER,
    ADMIN;

    /**
     * 점주 게이트웨이 접근 가능 여부
     */
    public boolean canAccessPlaceOwnerGateway() {
        return this == PLACE_OWNER || this == ADMIN;
    }

    public static Role fromString(String roleString) {
        if (roleString == null || roleString.isEmpty()) {
            return null;
        }
        try {
            return Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
