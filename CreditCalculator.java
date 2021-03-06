import java.math.BigDecimal;
import java.util.Scanner;
public class CreditCalculator {
    //считает количество дней, которое понадобится для погашения кредита
    public static void annuityCalculator (Scanner scanner) {
        System.out.println("Enter loan sum: ");
        BigDecimal loanSum = scanner.nextBigDecimal();
        System.out.println("Enter percentage of the loan (0,00 - 1,00): ");
        double percentage = scanner.nextDouble();
        System.out.println("Enter credit period (months):");
        int period = scanner.nextInt();
        System.out.println();
        double i = percentage/12;
        BigDecimal monthSum = loanSum.multiply(new BigDecimal(i + i / ((Math.pow(1+i, period)) - 1)));
        System.out.println("Monthly payment: " + monthSum + " (annuity payment)");
        System.out.println("The differentiated payment come later");
    }
}
