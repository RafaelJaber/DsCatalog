package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover, Long> {

    @Query("SELECT obj FROM PasswordRecover obj WHERE obj.token = :token AND obj.expiration > :now AND obj.usedAt IS NULL")
    List<PasswordRecover> searchValidTokens(String token, Instant now);

    @Query("SELECT obj FROM PasswordRecover obj WHERE obj.email = :email AND obj.expiration > :now AND obj.usedAt IS NULL")
    List<PasswordRecover> searchValidTokensFromEmail(String email, Instant now);
}
