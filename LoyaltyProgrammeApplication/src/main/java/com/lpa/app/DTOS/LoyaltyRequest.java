package com.lpa.app.DTOS;

/**
 * DTO for loyalty points request.
 */
public class LoyaltyRequest {
	private String userEmail;
	private double purchaseAmount;
	private String shopOwnerPassword;

	// Getters and Setters

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public double getPurchaseAmount() {
		return purchaseAmount;
	}

	public void setPurchaseAmount(double purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}

	public String getShopOwnerPassword() {
		return shopOwnerPassword;
	}

	public void setShopOwnerPassword(String shopOwnerPassword) {
		this.shopOwnerPassword = shopOwnerPassword;
	}
}