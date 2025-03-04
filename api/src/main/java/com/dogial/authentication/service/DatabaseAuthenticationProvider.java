package com.dogial.authentication.service;

import com.dogial.user.persistence.dao.UserDao;
import com.dogial.user.persistence.entities.UserEntity;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Collections;
import java.util.Optional;

@Singleton
public class DatabaseAuthenticationProvider implements AuthenticationProvider<HttpRequest<?>> {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public DatabaseAuthenticationProvider(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            String email = (String) authenticationRequest.getIdentity();
            String password = (String) authenticationRequest.getSecret();

            Optional<UserEntity> userOptional = userDao.findByEmail(email);

            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                if (passwordEncoder.matches(password, user.getPasswordHash())) {
                    emitter.next(AuthenticationResponse.success(
                            email,
                            Collections.singletonList("ROLE_USER")
                    ));
                    emitter.complete();
                } else {
                    emitter.error(AuthenticationResponse.exception("Invalid credentials"));
                }
            } else {
                emitter.error(AuthenticationResponse.exception("User not found"));
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
