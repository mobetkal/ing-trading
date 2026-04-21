package pl.ing.trading.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommissionCalculator {

    public static BigDecimal calculate(String mic, BigDecimal transactionValue) {
        if ("XWAR".equals(mic)) {
            BigDecimal commission = transactionValue.multiply(new BigDecimal("0.003"));
            BigDecimal minimum = new BigDecimal("5.00");
            return commission.compareTo(minimum) < 0 ? minimum : commission.setScale(2, RoundingMode.HALF_UP);
        } else {
            BigDecimal commission = transactionValue.multiply(new BigDecimal("0.002"));
            BigDecimal minimum = new BigDecimal("10.00");
            return commission.compareTo(minimum) < 0 ? minimum : commission.setScale(2, RoundingMode.HALF_UP);
        }
    }
}

