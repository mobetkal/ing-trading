package pl.ing.trading.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommissionCalculatorTest {

    @Test
    void shouldCalculateGpwCommissionAboveMinimum() {
        BigDecimal result = CommissionCalculator.calculate("XWAR", new BigDecimal("3150"));
        assertEquals(new BigDecimal("9.45"), result);
    }

    @Test
    void shouldApplyGpwMinimumCommission() {
        BigDecimal result = CommissionCalculator.calculate("XWAR", new BigDecimal("100"));
        assertEquals(new BigDecimal("5.00"), result);
    }

    @Test
    void shouldApplyForeignMinimumCommission() {
        BigDecimal result = CommissionCalculator.calculate("XNYS", new BigDecimal("3150"));
        assertEquals(new BigDecimal("10.00"), result);
    }

    @Test
    void shouldCalculateForeignCommissionAboveMinimum() {
        BigDecimal result = CommissionCalculator.calculate("XNYS", new BigDecimal("50000"));
        assertEquals(new BigDecimal("100.00"), result);
    }
}

