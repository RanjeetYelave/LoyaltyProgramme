package com.lpa.app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lpa.app.DTOS.LoyaltyRequest;
import com.lpa.app.Entity.UsePoints;
import com.lpa.app.ServiceImpl.LoyaltyService;

/**
 * Controller for managing loyalty points.
 */
@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {

	@Autowired
	private LoyaltyService loyaltyService;

	/**
	 * Adds loyalty points for a user based on purchase.
	 *
	 * @param request Loyalty points request
	 * @return Success or error message
	 */
	@PostMapping("/add")
	public ResponseEntity<String> addLoyaltyPoints(@RequestBody LoyaltyRequest request) {
		String response = loyaltyService.addLoyaltyPoints(request.getUserEmail(), request.getPurchaseAmount(),
				request.getShopOwnerPassword());
		if (response.startsWith("Loyalty points added successfully")) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().body(response);
		}
	}

	/**
	 * Retrieves the current wallet balance for a user.
	 *
	 * @param userEmail User's email
	 * @return Wallet balance or error message
	 */
	@GetMapping("/balance")
	public ResponseEntity<String> getWalletBalance(@RequestParam String userEmail) {
		String response = loyaltyService.getWalletBalance(userEmail);
		if (response.startsWith("Current wallet balance")) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().body(response);
		}
	}

	/**
	 * Optional: Endpoint to manually trigger expiration of loyalty points.
	 * Typically, expiration would be handled by a scheduled task.
	 *
	 * @return Expiration result
	 */
	@PostMapping("/expire")
	public ResponseEntity<String> expireLoyaltyPoints() {
		loyaltyService.expireLoyaltyPoints();
		return ResponseEntity.ok("Expired loyalty points checked and updated.");
	}

	@PostMapping("/use")
	public ResponseEntity<String> requestUsePoints(@RequestBody UsePoints usePointsRequest) {
		String response = loyaltyService.requestUsePoints(usePointsRequest);
		if (response.startsWith("Points used successfully")) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().body(response);
		}
	}

}
