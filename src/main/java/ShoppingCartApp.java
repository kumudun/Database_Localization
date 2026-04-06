import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartApp extends Application {

    private static Stage primaryStage;
    private static final String APP_TITLE = "Kumudu Nallaperuma / Shopping Cart App";

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        loadScene("English");
        primaryStage.show();
    }

    public static void loadScene(String selectedLanguage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ShoppingCartApp.class.getResource("/main-view.fxml")
        );

        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setSelectedLanguage(selectedLanguage);
        controller.loadLocalization();
        controller.applyLanguageStyle(root);

        Scene scene = new Scene(root, 900, 650);
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Controller {

        @FXML
        private Label selectLanguageLabel;

        @FXML
        private ComboBox<String> languageComboBox;

        @FXML
        private Label numberOfItemsLabel;

        @FXML
        private TextField itemCountField;

        @FXML
        private Button enterItemsButton;

        @FXML
        private Label instructionLabel;

        @FXML
        private VBox itemsContainer;

        @FXML
        private Button calculateTotalButton;

        @FXML
        private Label totalCostLabel;

        @FXML
        private Label totalValueLabel;

        private final List<TextField> priceFields = new ArrayList<>();
        private final List<TextField> quantityFields = new ArrayList<>();
        private final List<Label> itemTotalLabels = new ArrayList<>();

        private final CartCalculator calculator = new CartCalculator();
        private final CartService cartService = new CartService();
        private final LocalizationService localizationService = new LocalizationService();
        private final DecimalFormat df = new DecimalFormat("0.00");

        private Map<String, String> localizedTexts = new HashMap<>();

        @FXML
        public void initialize() {
            languageComboBox.setItems(FXCollections.observableArrayList(
                    "English", "Finnish", "Swedish", "Japanese", "Arabic"
            ));
        }

        public void setSelectedLanguage(String language) {
            languageComboBox.setValue(language);
        }

        public void loadLocalization() {
            String languageCode = mapLanguageToCode(languageComboBox.getValue());
            localizedTexts = localizationService.getLocalizedStrings(languageCode);
            applyLocalizedTexts();
        }

        private void applyLocalizedTexts() {
            selectLanguageLabel.setText(getText("select.language"));
            numberOfItemsLabel.setText(getText("enter.number.of.items"));
            itemCountField.setPromptText(getText("number.of.items.prompt"));
            enterItemsButton.setText(getText("enter.items"));
            instructionLabel.setText(getText("instruction"));
            calculateTotalButton.setText(getText("calculate.total"));
            totalCostLabel.setText(getText("total.cost"));
        }

        public void applyLanguageStyle(Parent root) {
            String code = mapLanguageToCode(languageComboBox.getValue());

            if ("ja".equals(code)) {
                root.setStyle("-fx-font-family: 'Yu Gothic UI', 'Meiryo', 'MS Gothic';");
                root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            } else if ("ar".equals(code)) {
                root.setStyle("-fx-font-family: 'Segoe UI', 'Tahoma', 'Arial Unicode MS';");
                root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                root.setStyle("-fx-font-family: 'Segoe UI', 'Arial', 'SansSerif';");
                root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
        }

        @FXML
        private void handleLanguageChange() {
            String selected = languageComboBox.getValue();

            try {
                ShoppingCartApp.loadScene(selected);
            } catch (IOException e) {
                showError("Could not change language.");
            }
        }

        @FXML
        private void handleEnterItems() {
            itemsContainer.getChildren().clear();
            priceFields.clear();
            quantityFields.clear();
            itemTotalLabels.clear();
            totalValueLabel.setText("0.00");

            int itemCount;

            try {
                itemCount = Integer.parseInt(itemCountField.getText().trim());
                if (itemCount <= 0) {
                    showError(getText("error.positive.items"));
                    return;
                }
            } catch (NumberFormatException e) {
                showError(getText("error.invalid.items"));
                return;
            }

            for (int i = 0; i < itemCount; i++) {
                Label itemLabel = new Label(getText("item") + " " + (i + 1) + ":");

                TextField priceField = new TextField();
                priceField.setPromptText(getText("enter.price.for.item"));
                priceField.setPrefWidth(180);

                TextField quantityField = new TextField();
                quantityField.setPromptText(getText("enter.quantity.for.item"));
                quantityField.setPrefWidth(180);

                Label itemTotalTextLabel = new Label(getText("item.total"));
                Label itemTotalValueLabel = new Label("0.00");

                HBox row = new HBox(10);
                row.getChildren().addAll(
                        itemLabel,
                        priceField,
                        quantityField,
                        itemTotalTextLabel,
                        itemTotalValueLabel
                );

                if (isArabicSelected()) {
                    row.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                } else {
                    row.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                }

                itemsContainer.getChildren().add(row);
                priceFields.add(priceField);
                quantityFields.add(quantityField);
                itemTotalLabels.add(itemTotalValueLabel);
            }

            if (isArabicSelected()) {
                itemsContainer.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                itemsContainer.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
        }

        @FXML
        private void handleCalculateTotal() {
            if (priceFields.isEmpty()) {
                showError(getText("error.enter.items.first"));
                return;
            }

            double[] prices = new double[priceFields.size()];
            int[] quantities = new int[quantityFields.size()];

            for (int i = 0; i < priceFields.size(); i++) {
                try {
                    double price = Double.parseDouble(priceFields.get(i).getText().trim());
                    int quantity = Integer.parseInt(quantityFields.get(i).getText().trim());

                    if (price < 0 || quantity < 0) {
                        showError(getText("error.nonnegative.values"));
                        return;
                    }

                    prices[i] = price;
                    quantities[i] = quantity;

                    double itemTotal = calculator.calculateItemTotal(price, quantity);
                    itemTotalLabels.get(i).setText(df.format(itemTotal));

                } catch (NumberFormatException e) {
                    showError(getText("error.invalid.price.quantity") + " " + (i + 1));
                    return;
                }
            }

            double total = calculator.calculateCartTotal(prices, quantities);
            totalValueLabel.setText(df.format(total));

            String languageCode = mapLanguageToCode(languageComboBox.getValue());
            cartService.saveCart(prices, quantities, languageCode);
        }

        private String mapLanguageToCode(String language) {
            return switch (language) {
                case "Finnish" -> "fi";
                case "Swedish" -> "sv";
                case "Japanese" -> "ja";
                case "Arabic" -> "ar";
                default -> "en";
            };
        }

        private String getText(String key) {
            return localizedTexts.getOrDefault(key, key);
        }

        private boolean isArabicSelected() {
            return "Arabic".equals(languageComboBox.getValue());
        }

        private void showError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(getText("error.title"));
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}