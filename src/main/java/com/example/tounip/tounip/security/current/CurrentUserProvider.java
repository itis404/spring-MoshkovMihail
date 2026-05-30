package com.example.tounip.tounip.security.current;

import java.util.UUID;

public interface CurrentUserProvider {

    UUID getCurrentUserId();
}