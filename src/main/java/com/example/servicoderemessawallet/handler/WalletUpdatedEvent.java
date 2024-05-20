package com.example.servicoderemessawallet.handler;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletUpdatedEvent {

    private UUID walletId;
    private UUID fromUserId;
    private UUID toUserId;
    private BigDecimal amountBrl;
    private BigDecimal amountUsd;


    public WalletUpdatedEvent(UUID walletId, UUID fromUserId, UUID toUserId, BigDecimal amountBrl, BigDecimal amountUsd) {
        this.walletId = walletId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amountBrl = amountBrl;
        this.amountUsd = amountUsd;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmountBrl() {
        return amountBrl;
    }

    public BigDecimal getAmountUsd() {
        return amountUsd;
    }

    public void setAmountBrl(BigDecimal amountBrl) {
        this.amountBrl = amountBrl;
    }

    public void setAmountUsd(BigDecimal amountUsd) {
        this.amountUsd = amountUsd;
    }
}

