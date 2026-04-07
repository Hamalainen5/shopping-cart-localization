import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingCartFxApp extends Application {

    private final LocalizationService localizationService = new LocalizationService();
    private final ShoppingCartCalculator calculator = new ShoppingCartCalculator();
    private final CartService cartService = new CartService();

    private Locale currentLocale = Locale.US;
    private String currentLanguageCode = "en";
    private Map<String, String> messages = localizationService.getMessages(currentLanguageCode);
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(currentLocale);

    private ComboBox<LocaleOption> languageCombo;
    private Spinner<Integer> itemCountSpinner;
    private Button generateRowsButton;
    private Button calculateButton;
    private Button saveButton;
    private Button clearButton;

    private TableView<CartRow> tableView;
    private Label totalLabel;
    private Label statusLabel;
    private Label itemCountLabel;

    @Override
    public void start(Stage stage) {
        languageCombo = new ComboBox<>();
        languageCombo.getItems().addAll(
                new LocaleOption("English", Locale.US),
                new LocaleOption("Suomi", new Locale("fi", "FI")),
                new LocaleOption("Svenska", new Locale("sv", "SE")),
                new LocaleOption("日本語", new Locale("ja", "JP"))
        );
        languageCombo.getSelectionModel().selectFirst();
        languageCombo.setOnAction(e -> switchLanguage(languageCombo.getValue().locale()));

        itemCountSpinner = new Spinner<>(1, 100, 1);
        itemCountSpinner.setEditable(true);

        generateRowsButton = new Button();
        generateRowsButton.setOnAction(e -> generateRows(itemCountSpinner.getValue()));

        calculateButton = new Button();
        calculateButton.setOnAction(e -> calculateCart());

        saveButton = new Button();
        saveButton.setOnAction(e -> saveCart());

        clearButton = new Button();
        clearButton.setOnAction(e -> clearCart());

        tableView = createTable();

        totalLabel = new Label();
        statusLabel = new Label();
        statusLabel.setWrapText(true);
        itemCountLabel = new Label();

        HBox topRow = new HBox(10,
                new Label("Language"),
                languageCombo,
                itemCountLabel,
                itemCountSpinner,
                generateRowsButton
        );
        topRow.setAlignment(Pos.CENTER_LEFT);

        HBox actionRow = new HBox(10, calculateButton, saveButton, clearButton);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(15, topRow, tableView, totalLabel, actionRow, statusLabel);
        root.setPadding(new Insets(16));
        VBox.setVgrow(tableView, Priority.ALWAYS);

        switchLanguage(Locale.US);
        generateRows(itemCountSpinner.getValue());

        stage.setTitle("Shopping Cart");
        stage.setScene(new Scene(root, 900, 550));
        stage.show();
    }

    private TableView<CartRow> createTable() {
        TableView<CartRow> table = new TableView<>();
        table.setEditable(true);

        TableColumn<CartRow, Integer> itemColumn = new TableColumn<>("Item");
        itemColumn.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getItemNumber()));

        TableColumn<CartRow, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getPriceText()));
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        priceColumn.setOnEditCommit(event -> {
            CartRow row = event.getRowValue();
            row.setPriceText(event.getNewValue());
            refreshRowSubtotal(row);
            refreshTotalsOnly();
        });

        TableColumn<CartRow, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverterSafe()));
        quantityColumn.setOnEditCommit(event -> {
            CartRow row = event.getRowValue();
            Integer newValue = event.getNewValue();
            row.setQuantity(newValue == null ? 0 : newValue);
            refreshRowSubtotal(row);
            refreshTotalsOnly();
        });

        TableColumn<CartRow, String> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getSubtotalText()));

        itemColumn.setPrefWidth(100);
        priceColumn.setPrefWidth(220);
        quantityColumn.setPrefWidth(180);
        subtotalColumn.setPrefWidth(220);

        table.getColumns().addAll(itemColumn, priceColumn, quantityColumn, subtotalColumn);
        return table;
    }

    private void switchLanguage(Locale locale) {
        currentLocale = locale;
        currentLanguageCode = locale.getLanguage();
        messages = localizationService.getMessages(currentLanguageCode);
        currencyFormat = NumberFormat.getCurrencyInstance(currentLocale);

        updateTexts();
        tableView.refresh();
        refreshTotalsOnly();
    }

    private void updateTexts() {
        String languageText = msg("label.language", "Language:");
        String itemCountText = msg("prompt.item.count", "Enter the number of items to purchase:");
        String itemText = msg("label.item", "Item");
        String priceText = msg("prompt.item.price", "Price");
        String quantityText = msg("prompt.item.quantity", "Quantity");
        String subtotalText = msg("label.item.total", "Item total");
        String cartTotalText = msg("label.cart.total", "Total cost:");
        String generateText = msg("button.generate", "Generate Rows");
        String calculateText = msg("button.calculate", "Calculate");
        String saveText = msg("button.save", "Save");
        String clearText = msg("button.clear", "Clear");
        String titleText = msg("title.app", "Shopping Cart");

        ((Label)((HBox)((VBox)tableView.getParent()).getChildren().get(0)).getChildren().get(0)).setText(languageText);
        itemCountLabel.setText(itemCountText);

        tableView.getColumns().get(0).setText(itemText);
        tableView.getColumns().get(1).setText(priceText);
        tableView.getColumns().get(2).setText(quantityText);
        tableView.getColumns().get(3).setText(subtotalText);

        generateRowsButton.setText(generateText);
        calculateButton.setText(calculateText);
        saveButton.setText(saveText);
        clearButton.setText(clearText);

        totalLabel.setText(cartTotalText + " " + currencyFormat.format(BigDecimal.ZERO));

        if (tableView.getScene() != null && tableView.getScene().getWindow() instanceof Stage stage) {
            stage.setTitle(titleText);
        }
    }

    private void generateRows(int count) {
        List<CartRow> rows = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            rows.add(new CartRow(i, "0.00", 0));
        }
        tableView.setItems(FXCollections.observableArrayList(rows));
        refreshTotalsOnly();
        statusLabel.setText("");
    }

    private void calculateCart() {
        try {
            List<BigDecimal> itemTotals = new ArrayList<>();

            for (CartRow row : tableView.getItems()) {
                BigDecimal price = parsePrice(row.getPriceText());
                int quantity = row.getQuantity();

                BigDecimal subtotal = calculator.calculateItemTotal(price, quantity);
                row.setSubtotal(subtotal);
                itemTotals.add(subtotal);
            }

            BigDecimal total = calculator.calculateCartTotal(itemTotals);
            totalLabel.setText(msg("label.cart.total", "Total cost:") + " " + currencyFormat.format(total));
            tableView.refresh();
            statusLabel.setText(msg("status.calculated", "Cart calculated successfully."));
        } catch (Exception e) {
            statusLabel.setText(msg("status.error", "Error:") + " " + e.getMessage());
        }
    }

    private void saveCart() {
        try {
            List<BigDecimal> itemTotals = new ArrayList<>();
            List<CartItem> cartItems = new ArrayList<>();

            for (CartRow row : tableView.getItems()) {
                BigDecimal price = parsePrice(row.getPriceText());
                int quantity = row.getQuantity();
                BigDecimal subtotal = calculator.calculateItemTotal(price, quantity);

                row.setSubtotal(subtotal);
                itemTotals.add(subtotal);
                cartItems.add(new CartItem(row.getItemNumber(), price, quantity, subtotal));
            }

            BigDecimal cartTotal = calculator.calculateCartTotal(itemTotals);
            cartService.saveCartRecord(tableView.getItems().size(), cartTotal, currentLanguageCode, cartItems);

            totalLabel.setText(msg("label.cart.total", "Total cost:") + " " + currencyFormat.format(cartTotal));
            tableView.refresh();
            statusLabel.setText(msg("status.saved", "Cart saved to database successfully."));
        } catch (Exception e) {
            statusLabel.setText(msg("status.error", "Error:") + " " + e.getMessage());
        }
    }

    private void clearCart() {
        for (CartRow row : tableView.getItems()) {
            row.setPriceText("0.00");
            row.setQuantity(0);
            row.setSubtotal(BigDecimal.ZERO);
        }
        tableView.refresh();
        refreshTotalsOnly();
        statusLabel.setText("");
    }

    private void refreshRowSubtotal(CartRow row) {
        try {
            BigDecimal price = parsePrice(row.getPriceText());
            BigDecimal subtotal = calculator.calculateItemTotal(price, row.getQuantity());
            row.setSubtotal(subtotal);
        } catch (Exception e) {
            row.setSubtotal(BigDecimal.ZERO);
        }
        tableView.refresh();
    }

    private void refreshTotalsOnly() {
        try {
            List<BigDecimal> itemTotals = new ArrayList<>();
            for (CartRow row : tableView.getItems()) {
                itemTotals.add(row.getSubtotal());
            }
            BigDecimal total = calculator.calculateCartTotal(itemTotals);
            totalLabel.setText(msg("label.cart.total", "Total cost:") + " " + currencyFormat.format(total));
        } catch (Exception e) {
            totalLabel.setText(msg("label.cart.total", "Total cost:") + " " + currencyFormat.format(BigDecimal.ZERO));
        }
    }

    private BigDecimal parsePrice(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim());
    }

    private String msg(String key, String fallback) {
        return messages.getOrDefault(key, fallback);
    }

    private record LocaleOption(String label, Locale locale) {
        @Override
        public String toString() {
            return label;
        }
    }

    public static class CartRow {
        private final int itemNumber;
        private String priceText;
        private int quantity;
        private BigDecimal subtotal = BigDecimal.ZERO;

        public CartRow(int itemNumber, String priceText, int quantity) {
            this.itemNumber = itemNumber;
            this.priceText = priceText;
            this.quantity = quantity;
        }

        public int getItemNumber() {
            return itemNumber;
        }

        public String getPriceText() {
            return priceText;
        }

        public void setPriceText(String priceText) {
            this.priceText = priceText;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getSubtotal() {
            return subtotal == null ? BigDecimal.ZERO : subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
        }

        public String getSubtotalText() {
            return subtotal == null ? "" : subtotal.toPlainString();
        }
    }

    public static class IntegerStringConverterSafe extends StringConverter<Integer> {
        @Override
        public String toString(Integer value) {
            return value == null ? "0" : value.toString();
        }

        @Override
        public Integer fromString(String value) {
            if (value == null || value.isBlank()) {
                return 0;
            }
            return Integer.parseInt(value.trim());
        }
    }
}