package com.collabskill.collabxskill.repo;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.collabskill.collabxskill.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByIsAdminTrue();

    Page<User> findByIsBannedTrue(Pageable pageable);

    @Query("SELECT up FROM User up WHERE up.isVerified = false AND up.updatedAt < :cutoff")
    List<User> findUnverifiedUserOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.isVerified = false AND u.updatedAt < :cutoff")
    int deleteUnverifiedUsersOlderThan(@Param("cutoff") LocalDateTime cutoff);

}
