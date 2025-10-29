package com.econova.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "address_id", nullable = false)
	private Address address;

	private Double amount;
	private String currency;

	// Status: PENDING, CONFIRMED (payment successful), FAILED (payment failed)
	private String status;

	@Column(name = "razorpay_order_id")
	private String razorpayOrderId;

	@Column(name = "razorpay_payment_id")
	private String razorpayPaymentId;

	// ✅ FIXED: Changed to LocalDateTime and removed insertable = false
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "payment_method")
	private String paymentMethod; // COD or RAZORPAY

	@Column(name = "delivery_status")
	private String deliveryStatus;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<OrderItem> orderItems = new ArrayList<>();

	public List<OrderItem> getOrderItems() {
	    return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
	    this.orderItems = orderItems;
	}
	
	
	
	// Constructor
	public Order() {
		this.status = "PENDING";
		this.paymentMethod = "COD";
		this.deliveryStatus = "Pending";
	}

	// ✅ Auto-set createdAt when entity is first persisted
	@PrePersist
	protected void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	// ===== Getters & Setters =====
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if ("PENDING".equals(status) || "CONFIRMED".equals(status) || "FAILED".equals(status)) {
			this.status = status;
		} else {
			throw new IllegalArgumentException("Invalid status: " + status + ". Must be PENDING, CONFIRMED, or FAILED");
		}
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getRazorpayPaymentId() {
		return razorpayPaymentId;
	}

	public void setRazorpayPaymentId(String razorpayPaymentId) {
		this.razorpayPaymentId = razorpayPaymentId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		if ("COD".equals(paymentMethod) || "RAZORPAY".equals(paymentMethod)) {
			this.paymentMethod = paymentMethod;
		} else {
			throw new IllegalArgumentException(
					"Invalid payment method: " + paymentMethod + ". Must be COD or RAZORPAY");
		}
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	@Override
	public String toString() {
		return "Order{" + "id=" + id + ", userId=" + (user != null ? user.getId() : null) + ", amount=" + amount
				+ ", status='" + status + '\'' + ", paymentMethod='" + paymentMethod + '\'' + ", deliveryStatus='"
				+ deliveryStatus + '\'' + ", razorpayOrderId='" + razorpayOrderId + '\'' + ", createdAt=" + createdAt
				+ '}';
	}
}