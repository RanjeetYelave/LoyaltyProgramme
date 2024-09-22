package com.lpa.app.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lpa.app.Entity.LoyaltyPoint;
import com.lpa.app.Entity.UsePoints;
import com.lpa.app.Entity.User;
import com.lpa.app.Repo.LoyaltyPointRepository;
import com.lpa.app.Repo.UserRepo;

@Service
public class LoyaltyService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private LoyaltyPointRepository loyaltyPointRepository;

	private static final String SHOP_OWNER_PASSWORD = "securePassword"; // Should be stored securely
	private static final long EXPIRATION_DAYS = 30; // Points expiration in days

	/**
	 * Adds loyalty points to a user based on purchase amount.
	 *
	 * @param userEmail         User's email
	 * @param purchaseAmount    Amount spent
	 * @param shopOwnerPassword Password for authentication
	 * @return Success or error message
	 */
	public String addLoyaltyPoints(String userEmail, double purchaseAmount, String shopOwnerPassword) {
		// Authenticate shop owner's password
		if (!SHOP_OWNER_PASSWORD.equals(shopOwnerPassword)) {
			return "Authentication failed: Invalid Shop Owner Password.";
		}

		// Get user by email
		User user = userRepo.findByEmail(userEmail);
		if (user == null) {
			return "User not found.";
		}

		// Loyalty points are equal to the purchase amount
		int pointsEarned = (int) purchaseAmount;

		// Current time as purchase time
		LocalDateTime purchaseTime = LocalDateTime.now();
		LocalDateTime expiryTime = purchaseTime.plusDays(EXPIRATION_DAYS);

		// Create LoyaltyPoint entry
		LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
		loyaltyPoint.setPoints(pointsEarned);
		loyaltyPoint.setPurchaseTime(purchaseTime);
		loyaltyPoint.setExpiryTime(expiryTime);
		loyaltyPoint.setUser(user);

		// Save to repository
		loyaltyPointRepository.save(loyaltyPoint);

		return "Loyalty points added successfully: " + pointsEarned + " points.";
	}

	/**
	 * Checks and expires loyalty points that have passed their expiry date.
	 */
	public void expireLoyaltyPoints() {
		LocalDateTime now = LocalDateTime.now();
		// Efficiently fetch expired points using repository method
		List<LoyaltyPoint> expiredPoints = loyaltyPointRepository.findByExpiryTimeBefore(now);

		if (!expiredPoints.isEmpty()) {
			loyaltyPointRepository.deleteAll(expiredPoints);
			System.out.println("Expired " + expiredPoints.size() + " loyalty points.");
		}
	}

	/**
	 * Retrieves the current wallet balance (sum of usable points).
	 *
	 * @param userEmail User's email
	 * @return Total usable points or error message
	 */
	public String getWalletBalance(String userEmail) {
		// Get user by email
		User user = userRepo.findByEmail(userEmail);
		if (user == null) {
			return "User not found.";
		}

		// Get current time
		LocalDateTime now = LocalDateTime.now();

		// Fetch all non-expired points
		List<LoyaltyPoint> usablePoints = loyaltyPointRepository.findByUserAndExpiryTimeAfter(user, now);

		// Calculate total points
		int totalPoints = usablePoints.stream().mapToInt(LoyaltyPoint::getPoints).sum();

		return "Current wallet balance: " + totalPoints + " points.";
	}

	public String requestUsePoints(UsePoints usePointsRequest) {
		// Authenticate shop owner's password
		if (!SHOP_OWNER_PASSWORD.equals(usePointsRequest.getShopOwnerPassword())) {
			return "Authentication failed: Invalid Shop Owner Password.";
		}

		// Fetch the user's current balance
		int currentBalance = getCurrentBalance(usePointsRequest.getUserEmail());

		// Validate points to use
		if (usePointsRequest.getPointsUsed() <= 0) {
			return "Points used must be greater than zero.";
		}
		if (usePointsRequest.getPointsUsed() > currentBalance) {
			return "Insufficient points to use.";
		}

		// Deduct points
		updateBalance(usePointsRequest.getUserEmail(), currentBalance - usePointsRequest.getPointsUsed());

		// Record the usage request (optional)
		usePointsRequest.setRequestTime(LocalDateTime.now());
		// Save UsePoints to the database if needed (you can create a repository for it)

		return "Points used successfully. Remaining balance: " + (currentBalance - usePointsRequest.getPointsUsed());
	}

	private int getCurrentBalance(String userEmail) {
		User user = userRepo.findByEmail(userEmail);
		if (user == null) {
			return 0; // User not found, assume 0 balance
		}

		LocalDateTime now = LocalDateTime.now();
		List<LoyaltyPoint> usablePoints = loyaltyPointRepository.findByUserAndExpiryTimeAfter(user, now);
		return usablePoints.stream().mapToInt(LoyaltyPoint::getPoints).sum();
	}

	private void updateBalance(String userEmail, int newBalance) {
		User user = userRepo.findByEmail(userEmail);
		if (user == null) {
			throw new IllegalArgumentException("User not found: " + userEmail);
		}

		// Get current time for checking non-expired points
		LocalDateTime now = LocalDateTime.now();

		// Fetch all usable points for the user
		List<LoyaltyPoint> usablePoints = loyaltyPointRepository.findByUserAndExpiryTimeAfter(user, now);

		// Deduct points from the available LoyaltyPoint entries
		int pointsToDeduct = newBalance;
		for (LoyaltyPoint loyaltyPoint : usablePoints) {
			if (pointsToDeduct <= 0)
				break;

			int pointsAvailable = loyaltyPoint.getPoints();
			if (pointsAvailable <= pointsToDeduct) {
				// If the entire entry can be deducted
				pointsToDeduct -= pointsAvailable;
				loyaltyPointRepository.delete(loyaltyPoint); // Remove entry if all points are used
			} else {
				// If only part of the entry is deducted
				loyaltyPoint.setPoints(pointsAvailable - pointsToDeduct);
				loyaltyPointRepository.save(loyaltyPoint);
				pointsToDeduct = 0; // All points deducted
			}
		}

		if (pointsToDeduct > 0) {
			throw new IllegalStateException("Error in deducting points. Not enough points available.");
		}
	}

}
