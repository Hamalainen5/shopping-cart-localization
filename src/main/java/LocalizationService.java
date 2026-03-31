import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {

    public Map<String, String> getMessages(String language) {
        Map<String, String> messages = new HashMap<>();

        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, language);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    messages.put(resultSet.getString("key"), resultSet.getString("value"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading localization strings: " + e.getMessage());
        }

        return messages;
    }
}