package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repositories.PasswordRecoverRepository;
import com.devsuperior.dscatalog.entities.PasswordRecover;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordRecoverRepository passwordRecoverRepository;
    private final EmailServices emailServices;

    @Value("${dscatalog.email.password-recover.expiration-token-seconds}")
    private Long tokenExpirationSeconds;

    @Value("${dscatalog.email.password-recover.uri}")
    private String redirectRecoverUri;

    public AuthService(
            UserRepository userRepository,
            PasswordRecoverRepository passwordRecoverRepository,
            EmailServices emailServices
    ) {
        this.userRepository = userRepository;
        this.passwordRecoverRepository = passwordRecoverRepository;
        this.emailServices = emailServices;
    }


    public void createRecoveryToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User", "email", email)
        );

        PasswordRecover entity = PasswordRecover.builder()
                .email(user.getEmail())
                .token(UUID.randomUUID().toString())
                .expiration(Instant.now().plusSeconds(tokenExpirationSeconds))
                .build();
        PasswordRecover inserted = passwordRecoverRepository.save(entity);

        String subject = "Password recovery";
        String recoveryLink = redirectRecoverUri + "/" + inserted.getToken();
        String body = buildPasswordResetEmail(
                user.getFirstName(),
                inserted.getEmail(),
                recoveryLink,
                tokenExpirationSeconds / 60L
        );

        emailServices.sendEmail(inserted.getEmail(), subject, body);
    }


    private String buildPasswordResetEmail(String userName, String userEmail, String resetLink, Long tokenValidityMinutes) {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Password Reset</title>" +
                "    <style>" +
                "        body {" +
                "            font-family: Arial, sans-serif;" +
                "            background-color: #f4f4f4;" +
                "            color: #333;" +
                "            margin: 0;" +
                "            padding: 0;" +
                "            -webkit-font-smoothing: antialiased;" +
                "        }" +
                "        .container {" +
                "            max-width: 600px;" +
                "            margin: 50px auto;" +
                "            background-color: #ffffff;" +
                "            padding: 20px;" +
                "            border-radius: 8px;" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);" +
                "        }" +
                "        h1 {" +
                "            font-size: 24px;" +
                "            color: #333;" +
                "        }" +
                "        p {" +
                "            font-size: 16px;" +
                "            line-height: 1.6;" +
                "        }" +
                "        .button {" +
                "            display: block;" +
                "            width: 100%;" +
                "            max-width: 200px;" +
                "            margin: 20px auto;" +
                "            padding: 15px 25px;" +
                "            font-size: 16px;" +
                "            font-weight: bold;" +
                "            color: #ffffff;" +
                "            background-color: #007bff;" +
                "            text-align: center;" +
                "            text-decoration: none;" +
                "            border-radius: 5px;" +
                "        }" +
                "        .button:hover {" +
                "            background-color: #0056b3;" +
                "        }" +
                "        .link-container {" +
                "            margin-top: 20px;" +
                "            text-align: center;" +
                "            font-size: 14px;" +
                "        }" +
                "        .link-container p {" +
                "            margin: 5px 0;" +
                "        }" +
                "        .link-container a {" +
                "            color: #007bff;" +
                "            word-break: break-all;" +
                "        }" +
                "        .footer {" +
                "            margin-top: 30px;" +
                "            text-align: center;" +
                "            font-size: 12px;" +
                "            color: #999999;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <h1>Password Reset Request</h1>" +
                "        <p>Hello, " + userName + ",</p>" +
                "        <p>We received a request to reset the password for your account associated with this email: <strong>" + userEmail + "</strong>.</p>" +
                "        <p>Your password reset token is valid for <strong>" + tokenValidityMinutes + " minutes</strong>.</p>" +
                "        <p>Click the button below to proceed with resetting your password:</p>" +
                "        <a href=\"" + resetLink + "\" class=\"button\">Reset Your Password</a>" +
                "        <div class=\"link-container\">" +
                "            <p>If the button above doesn't work, copy and paste the following link into your browser:</p>" +
                "            <a href=\"" + resetLink + "\">" + resetLink + "</a>" +
                "        </div>" +
                "        <p>If you didn't request a password reset, please ignore this email.</p>" +
                "        <div class=\"footer\">" +
                "            <p>Thank you,<br>DsCatalog</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
