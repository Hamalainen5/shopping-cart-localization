import java.math.BigDecimal;

public class CartItem {
    private final int itemNumber;
    private final BigDecimal price;
    private final int quantity;
    private final BigDecimal subtotal;

    public CartItem(int itemNumber, BigDecimal price, int quantity, BigDecimal subtotal) {
        this.itemNumber = itemNumber;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}