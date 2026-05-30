package com.example.tounip.tounip.membership.application.model;

public enum MembershipRole {
    MEMBER(1),
    ADMIN(2),
    OWNER(3);

    private final int level;

    MembershipRole(int level) {
        this.level = level;
    }

    public boolean atLeast(MembershipRole requiredRole) {
        return this.level >= requiredRole.level;
    }
}