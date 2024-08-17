package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.RoleRequest;
import com.devsuperior.dscatalog.dto.requests.UserInsertRequest;
import com.devsuperior.dscatalog.dto.requests.UserRequest;
import com.devsuperior.dscatalog.dto.requests.UserUpdateRequest;
import com.devsuperior.dscatalog.dto.responses.UserResponse;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.projections.UserDetailsProjection;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import com.devsuperior.dscatalog.services.exceptions.UniqueKeyDatabaseException;
import com.devsuperior.dscatalog.services.exceptions.UserNotLoggedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllPaged(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User", "id", id.toString())
        );
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse insert(UserInsertRequest request) {
        try {
            User user = new User();
            copyDtoToEntity(request, user);
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            User inserted = userRepository.save(user);
            return new UserResponse(inserted);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueKeyDatabaseException();
        }
    }

    @Transactional
    public UserResponse update(UserUpdateRequest request, Long id) {
        try {
            User user = userRepository.getReferenceById(id);
            copyDtoToEntity(request, user);

            User updated = userRepository.save(user);
            return new UserResponse(updated);
        } catch (jakarta.persistence.EntityNotFoundException ex) {
            throw new EntityNotFoundException("User", "id", id.toString());
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User", "id", id.toString());
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseIntegrityException();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.getFirst().getPassword());
        for (UserDetailsProjection userDetails : result) {
            user.addRole(new Role(
                    userDetails.getRoleId(),
                    userDetails.getAuthority()
            ));
        }

        return user;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = this.authenticated();
        return new UserResponse(user);
    }

    protected User authenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
        String username = jwtPrincipal.getClaim("username");

        return userRepository.findByEmail(username).orElseThrow(
                UserNotLoggedException::new
        );
    }

    private void copyDtoToEntity(UserRequest source, User target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setEmail(source.getEmail());

        target.getRoles().clear();
        for (RoleRequest roleRequest : source.getRoles()) {
            Role role = roleRepository.getReferenceById(roleRequest.getId());
            target.getRoles().add(role);
        }
    }
}
