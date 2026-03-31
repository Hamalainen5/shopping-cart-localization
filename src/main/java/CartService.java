import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class CartService {

    public void saveCartRecord(int totalItems, BigDecimal totalCost, String language, List<CartItem> items) {
        String cartSql = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
        String itemSql = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            int cartRecordId;

            try (PreparedStatement cartStatement =
                         connection.prepareStatement(cartSql, Statement.RETURN_GENERATED_KEYS)) {

                cartStatement.setInt(1, totalItems);
                cartStatement.setBigDecimal(2, totalCost);
                cartStatement.setString(3, language);
                cartStatement.executeUpdate();

                try (ResultSet generatedKeys = cartStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cartRecordId = generatedKeys.getInt(1);
                    } else {
                        throw new RuntimeException("Failed to create cart record.");
                    }
                }
            }

            try (PreparedStatement itemStatement = connection.prepareStatement(itemSql)) {
                for (CartItem item : items) {
                    itemStatement.setInt(1, cartRecordId);
                    itemStatement.setInt(2, item.getItemNumber());
                    itemStatement.setBigDecimal(3, item.getPrice());
                    itemStatement.setInt(4, item.getQuantity());
                    itemStatement.setBigDecimal(5, item.getSubtotal());
                    itemStatement.addBatch();
                }
                itemStatement.executeBatch();
            }

            connection.commit();
            System.out.println("Cart saved to database successfully.");

        } catch (Exception e) {
            System.err.println("Error saving cart: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception rollbackException) {
                System.err.println("Rollback failed: " + rollbackException.getMessage());
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception closeException) {
                System.err.println("Connection close failed: " + closeException.getMessage());
            }
        }
    }
}