import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {

    public Map<String, String> getLocalizedStrings(String language) {
        Map<String, String> strings = new HashMap<>();

        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, language);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    strings.put(rs.getString("key"), rs.getString("value"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (strings.isEmpty() && !"en".equals(language)) {
            return getLocalizedStrings("en");
        }

        return strings;
    }
}
