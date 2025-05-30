package com.henrikacadej.urlshortener.service;

import com.henrikacadej.urlshortener.assembler.OAuth2UserToUserAssembler;
import com.henrikacadej.urlshortener.entity.User;
import com.henrikacadej.urlshortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Does not Exist"));
    }

    public void handleUserLogin(OAuth2User oAuth2User) {

        User user = OAuth2UserToUserAssembler.toUser(oAuth2User);

        if(userRepository.findById(user.getEmail()).isEmpty()){
            log.info("User Does Not Exist , adding new user");
            userRepository.save(user);
        }

    }
}
