package br.com.nu.capitalgain.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record OperationTax(BigDecimal tax) {

    public static OperationTax of(
        BigDecimal tax
    ){
        Objects.requireNonNull(tax, "Tax cannot be null");
        return new OperationTax(applyPattern(tax));
    }

    public static OperationTax ofZero(){
        return new OperationTax(applyPattern(BigDecimal.ZERO));
    }

    private static BigDecimal applyPattern(BigDecimal tax) {
        return tax.setScale(2, RoundingMode.UNNECESSARY);
    }

}
