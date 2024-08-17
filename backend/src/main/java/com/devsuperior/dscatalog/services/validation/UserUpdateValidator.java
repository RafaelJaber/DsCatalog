package com.devsuperior.dscatalog.services.validation;

import com.devsuperior.dscatalog.dto.requests.UserUpdateRequest;
import com.devsuperior.dscatalog.dto.responses.errors.FieldMessageResponse;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateRequest> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserUpdateValid ann) {
    }

    @Override
    public boolean isValid(UserUpdateRequest dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        Map<String, String> requestAttributes = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        long userId = Long.parseLong(requestAttributes.get("id"));

        List<FieldMessageResponse> list = new ArrayList<>();

        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isPresent() && user.get().getId() != userId) {
            list.add(new FieldMessageResponse(
                    "email",
                    "This email address is already registered.")
            );
        }

        for (FieldMessageResponse e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}

