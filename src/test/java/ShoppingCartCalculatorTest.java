
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartCalculatorTest {

    private final ShoppingCartCalculator calculator = new ShoppingCartCalculator();

    @Test
    void shouldCalculateItemTotalCorrectly() {
        BigDecimal result = calculator.calculateItemTotal(new BigDecimal("10.50"), 3);
        assertEquals(new BigDecimal("31.50"), result);
    }

    @Test
    void shouldCalculateCartTotalCorrectly() {
        BigDecimal result = calculator.calculateCartTotal(List.of(
                new BigDecimal("10.00"),
                new BigDecimal("20.50"),
                new BigDecimal("5.50")
        ));
        assertEquals(new BigDecimal("36.00"), result);
    }

    @Test
    void shouldAllowZeroPrice() {
        BigDecimal result = calculator.calculateItemTotal(new BigDecimal("0.00"), 5);
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void shouldAllowZeroQuantity() {
        BigDecimal result = calculator.calculateItemTotal(new BigDecimal("10.00"), 0);
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void shouldThrowExceptionForNegativePrice() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateItemTotal(new BigDecimal("-1.00"), 2)
        );
        assertEquals("Price must be zero or greater.", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeQuantity() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateItemTotal(new BigDecimal("1.00"), -2)
        );
        assertEquals("Quantity must be zero or greater.", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullTotalsList() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateCartTotal(null));
    }
}