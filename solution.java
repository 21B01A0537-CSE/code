import org.json.JSONObject;
import java.util.*;

public class ShamirSecretSharing {

    public static int generateSecret(int[] x, int[] y, int k) {
        Fraction result = new Fraction(0, 1);

        for (int i = 0; i < k; i++) {
            Fraction term = new Fraction(y[i], 1);

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term = term.multiply(new Fraction(-x[j], x[i] - x[j]));
                }
            }

            result = result.add(term);
        }

        return result.numerator / result.denominator;
    }

    public static int decodeValue(String value, int base) {
        return Integer.parseInt(value, base);
    }

    public static void operation(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject keys = jsonObject.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        int[] x = new int[k];
        int[] y = new int[k];

        for (int i = 1; i <= k; i++) {
            JSONObject point = jsonObject.getJSONObject(String.valueOf(i));
            int xi = i;
            int yi = decodeValue(point.getString("value"), point.getInt("base"));
            x[i - 1] = xi;
            y[i - 1] = yi;
        }

        int secret = generateSecret(x, y, k);
        System.out.println("The secret is: " + secret);
    }

    public static void main(String[] args) {
        String jsonString = "{" +
                "\"keys\": {\"n\": 4, \"k\": 3}," +
                "\"1\": {\"base\": \"10\", \"value\": \"4\"}," +
                "\"2\": {\"base\": \"2\", \"value\": \"111\"}," +
                "\"3\": {\"base\": \"10\", \"value\": \"12\"}," +
                "\"4\": {\"base\": \"6\", \"value\": \"20\"}" +
                "}";

        operation(jsonString);
    }

    static class Fraction {
        int numerator, denominator;

        Fraction(int numerator, int denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
            reduce();
        }

        private void reduce() {
            int gcd = gcd(numerator, denominator);
            numerator /= gcd;
            denominator /= gcd;
        }

        private int gcd(int a, int b) {
            return b == 0 ? a : gcd(b, a % b);
        }

        Fraction multiply(Fraction other) {
            return new Fraction(this.numerator * other.numerator, this.denominator * other.denominator);
        }

        Fraction add(Fraction other) {
            int newNumerator = this.numerator * other.denominator + other.numerator * this.denominator;
            int newDenominator = this.denominator * other.denominator;
            return new Fraction(newNumerator, newDenominator);
        }
    }
}
