package com.example.tounip.tounip.space.application.service;

import java.util.UUID;

public interface SpaceLookupService {

    void requireExistingSpace(UUID spaceId);

    void requirePublicSpace(UUID spaceId);
}