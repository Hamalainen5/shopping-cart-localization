import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartItemTest {

    @Test
    void shouldReturnValuesFromGetters() {
        CartItem item = new CartItem(
                1,
                new BigDecimal("12.50"),
                3,
                new BigDecimal("37.50")
        );

        assertEquals(1, item.getItemNumber());
        assertEquals(new BigDecimal("12.50"), item.getPrice());
        assertEquals(3, item.getQuantity());
        assertEquals(new BigDecimal("37.50"), item.getSubtotal());
    }
}