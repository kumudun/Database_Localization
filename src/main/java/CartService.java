import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartService {

    public void saveCart(double[] prices, int[] quantities, String language) {
        String insertCartRecord = """
                INSERT INTO cart_records (total_items, total_cost, language)
                VALUES (?, ?, ?)
                """;

        String insertCartItem = """
                INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;

        int totalItems = 0;
        double totalCost = 0.0;
        CartCalculator calculator = new CartCalculator();

        for (int i = 0; i < quantities.length; i++) {
            totalItems += quantities[i];
            totalCost += calculator.calculateItemTotal(prices[i], quantities[i]);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int cartRecordId;

            try (PreparedStatement cartStmt = conn.prepareStatement(insertCartRecord, PreparedStatement.RETURN_GENERATED_KEYS)) {
                cartStmt.setInt(1, totalItems);
                cartStmt.setDouble(2, totalCost);
                cartStmt.setString(3, language);
                cartStmt.executeUpdate();

                try (ResultSet keys = cartStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        cartRecordId = keys.getInt(1);
                    } else {
                        throw new SQLException("Failed to get generated cart record ID.");
                    }
                }
            }

            try (PreparedStatement itemStmt = conn.prepareStatement(insertCartItem)) {
                for (int i = 0; i < prices.length; i++) {
                    double subtotal = calculator.calculateItemTotal(prices[i], quantities[i]);

                    itemStmt.setInt(1, cartRecordId);
                    itemStmt.setInt(2, i + 1);
                    itemStmt.setDouble(3, prices[i]);
                    itemStmt.setInt(4, quantities[i]);
                    itemStmt.setDouble(5, subtotal);
                    itemStmt.addBatch();
                }

                itemStmt.executeBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
