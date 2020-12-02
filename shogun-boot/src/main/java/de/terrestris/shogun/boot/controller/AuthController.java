package de.terrestris.shogun.boot.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@ConditionalOnExpression("${controller.auth.enabled:true}")
public class AuthController {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @GetMapping("/isSessionValid")
    public ResponseEntity<?> isSessionValid(Authentication authentication) {
        LOG.debug("Checking if user is logged in.");

        if (authentication != null && authentication.isAuthenticated()) {
            LOG.debug("User is logged in!");

            return ResponseEntity.ok().build();
        }

        LOG.debug("User is NOT logged in!");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
