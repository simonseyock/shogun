package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends BaseService<UserRepository, User> {

    @Autowired
    KeycloakUtil keycloakUtil;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        List<User> users = (List<User>) repository.findAll();

        for (User user : users) {
            UserResource userResource = keycloakUtil.getUserResource(user);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            user.setKeycloakRepresentation(userRepresentation);
        }

        return users;
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<User> findAllBy(Specification specification) {
        List<User> users = (List<User>) repository.findAll(specification);

        for (User user : users) {
            UserResource userResource = keycloakUtil.getUserResource(user);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            user.setKeycloakRepresentation(userRepresentation);
        }

        return users;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    @Override
    public Optional<User> findOne(Long id) {
        Optional<User> user = repository.findById(id);

        if (user.isPresent()) {
            UserResource userResource = keycloakUtil.getUserResource(user.get());
            UserRepresentation userRepresentation = userResource.toRepresentation();
            user.get().setKeycloakRepresentation(userRepresentation);
        }

        return user;
    }

}
