public class CartCalculator {

    public double calculateItemTotal(double price, int quantity) {
        return price * quantity;
    }

    public double calculateCartTotal(double[] prices, int[] quantities) {
        if (prices == null || quantities == null) {
            throw new IllegalArgumentException("Prices and quantities cannot be null.");
        }

        if (prices.length != quantities.length) {
            throw new IllegalArgumentException("Prices and quantities must have the same length.");
        }

        double total = 0.0;
        for (int i = 0; i < prices.length; i++) {
            total += calculateItemTotal(prices[i], quantities[i]);
        }
        return total;
    }
}