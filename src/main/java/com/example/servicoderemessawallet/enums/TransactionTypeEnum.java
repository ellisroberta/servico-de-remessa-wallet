package com.example.servicoderemessawallet.enums;

public enum TransactionTypeEnum {
    DEPOSIT("DEPOSIT"),
    WITHDRAWAL("WITHDRAWAL"),
    TRANSFER("TRANSFER");

    private final String type;

    TransactionTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

