package org.alexanderr193.barrelTrade.data.model;

import org.alexanderr193.barrelTrade.data.model.enums.Currency;

public class Slot {
    protected int slotId;
    protected int amount;
    protected Currency currency;
    protected String base64Product;

    public Slot(int slotId, int amount, Currency currency, String base64Product) {
        this.slotId = slotId;
        this.amount = amount;
        this.currency = currency;
        this.base64Product = base64Product;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getBase64Product() {
        return base64Product;
    }

    public void setBase64Product(String base64Product) {
        this.base64Product = base64Product;
    }
}
