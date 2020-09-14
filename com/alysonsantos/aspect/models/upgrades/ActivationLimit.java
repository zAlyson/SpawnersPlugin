package com.alysonsantos.aspect.models.upgrades;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ActivationLimit {

    private final int level;
    private final int quantity;
    private final double priceCoins;
    private final double priceTokens;

}
