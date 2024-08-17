package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.requests.RoleRequest;
import com.devsuperior.dscatalog.dto.requests.UserRequest;
import com.devsuperior.dscatalog.dto.responses.UserResponse;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
    public UserResponse insert(UserRequest request) {
        User user = new User();
        copyDtoToEntity(request, user);

        User inserted = userRepository.save(user);
        return new UserResponse(inserted);
    }

    @Transactional
    public UserResponse update(UserRequest request, Long id) {
        try {
            User user = new User();
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
