import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartCalculatorTest {

    private final CartCalculator calculator = new CartCalculator();

    @Test
    void testCalculateItemTotal() {
        double result = calculator.calculateItemTotal(10.0, 3);
        assertEquals(30.0, result, 0.001);
    }

    @Test
    void testCalculateCartTotal() {
        double[] prices = {10.0, 5.5, 7.0};
        int[] quantities = {2, 4, 1};

        double result = calculator.calculateCartTotal(prices, quantities);
        assertEquals(49.0, result, 0.001);
    }

    @Test
    void testEmptyCartTotal() {
        double[] prices = {};
        int[] quantities = {};
        double result = calculator.calculateCartTotal(prices, quantities);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testDifferentArrayLengths() {
        double[] prices = {10.0, 20.0};
        int[] quantities = {1};

        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateCartTotal(prices, quantities));
    }

    @Test
    void testNullArrays() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateCartTotal(null, null));
    }
}