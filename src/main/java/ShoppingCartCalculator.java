import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ShoppingCartCalculator {

    public BigDecimal calculateItemTotal(BigDecimal price, int quantity) {
        validatePrice(price);
        validateQuantity(quantity);
        return price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateCartTotal(List<BigDecimal> itemTotals) {
        if (itemTotals == null) {
            throw new IllegalArgumentException("Item totals list cannot be null.");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal itemTotal : itemTotals) {
            if (itemTotal == null) {
                throw new IllegalArgumentException("Item total cannot be null.");
            }
            total = total.add(itemTotal);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be zero or greater.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be zero or greater.");
        }
    }
}