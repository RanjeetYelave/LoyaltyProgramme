package com.lpa.app.Repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lpa.app.Entity.LoyaltyPoint;
import com.lpa.app.Entity.User;

@Repository
public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {
	List<LoyaltyPoint> findByUserAndExpiryTimeAfter(User user, LocalDateTime currentTime);

	List<LoyaltyPoint> findByExpiryTimeBefore(LocalDateTime currentTime);
}
