package com.example.saive.models;

public class PaymentCard {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private String cardType; // e.g., "Visa", "Mastercard"

    public PaymentCard(String cardNumber, String cardHolderName, String expiryDate, String cvv, String cardType) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
    }

    public String getCardNumber() { return cardNumber; }
    public String getCardHolderName() { return cardHolderName; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }
    public String getCardType() { return cardType; }

    public String getMaskedNumber() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }
}
