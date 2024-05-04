package dev.misei.einfachstonks.stonkservice.model;

public enum ETFType {
    //i.e: ETF Goldman
    INDIVIDUAL_POSITIVE,
    //i.e: ETF Mining Companies
    GROUP_POSITIVE,
    //i.e: ETF Bitcoin
    INDIVIDUAL_NEGATIVE,
    //i.e: ETF SP500, ETF Volatility Euro
    GROUP_NEGATIVE;
}
