package com.henrikacadej.urlshortener.assembler;

import com.henrikacadej.urlshortener.entity.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2UserToUserAssembler {

    public static User toUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = new User();
        user.setEmail(email);
        user.setName(name != null ? name : email);

        user.setRoles("ROLE_USER");

        return user;
    }
}

