package com.alysonsantos.aspect.models.upgrades;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BonusDrop {

    private final int level;
    private final double boost;
    private final double priceCoins;
    private final double priceTokens;

}
