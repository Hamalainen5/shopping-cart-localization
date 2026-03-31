import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            Locale locale = chooseLocale(reader);
            String languageCode = getLanguageCode(locale);

            LocalizationService localizationService = new LocalizationService();
            Map<String, String> messages = localizationService.getMessages(languageCode);

            ShoppingCartCalculator calculator = new ShoppingCartCalculator();
            CartService cartService = new CartService();
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

            System.out.print(messages.getOrDefault("prompt.item.count", "Enter the number of items to purchase:") + " ");
            int itemCount = Integer.parseInt(reader.readLine().trim());

            List<BigDecimal> itemTotals = new ArrayList<>();
            List<CartItem> cartItems = new ArrayList<>();

            for (int i = 1; i <= itemCount; i++) {
                System.out.print(messages.getOrDefault("prompt.item.price", "Enter the price for item") + " " + i + ": ");
                BigDecimal price = new BigDecimal(reader.readLine().trim());

                System.out.print(messages.getOrDefault("prompt.item.quantity", "Enter the quantity for item") + " " + i + ": ");
                int quantity = Integer.parseInt(reader.readLine().trim());

                BigDecimal itemTotal = calculator.calculateItemTotal(price, quantity);
                itemTotals.add(itemTotal);

                cartItems.add(new CartItem(i, price, quantity, itemTotal));

                System.out.println(messages.getOrDefault("label.item.total", "Item total") + " " + i + ": "
                        + currencyFormat.format(itemTotal));
            }

            BigDecimal cartTotal = calculator.calculateCartTotal(itemTotals);
            System.out.println(messages.getOrDefault("label.cart.total", "Total cost:") + " "
                    + currencyFormat.format(cartTotal));

            cartService.saveCartRecord(itemCount, cartTotal, languageCode, cartItems);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static Locale chooseLocale(BufferedReader reader) throws Exception {
        System.out.println("Select language / Valitse kieli / Välj språk / 言語を選択してください:");
        System.out.println("1. English");
        System.out.println("2. Suomi");
        System.out.println("3. Svenska");
        System.out.println("4. 日本語");
        System.out.print("> ");

        String choice = reader.readLine().trim();

        return switch (choice) {
            case "2" -> new Locale("fi", "FI");
            case "3" -> new Locale("sv", "SE");
            case "4" -> new Locale("ja", "JP");
            default -> Locale.US;
        };
    }

    private static String getLanguageCode(Locale locale) {
        return locale.getLanguage();
    }
}