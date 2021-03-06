import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class SQLHelper {
    //метод, показывающий полную финансовую таблицу (с затратами и расходами)
    public static void fullReport(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM base");
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String description = resultSet.getString("Description");
                    String type = resultSet.getString("Type");
                    BigDecimal money = resultSet.getBigDecimal("Money");
                    Timestamp date = resultSet.getTimestamp("Date");
                    System.out.println(id + "." + description + " - " + type + " - " + money + " - " + date);
                }
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong...");
            System.out.println(ex);
        }
    }
    
    //метод, добавляющий в таблицу затраты
    public static void addRevenue(String url, String user, String password, Scanner scanner) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                Statement statement = conn.createStatement();
                System.out.println("Enter Description: ");
                String description = "";
                int i = 0;
                while (description == null || description.equals("") == true) {
                    description = scanner.nextLine();
                }
                System.out.println("Enter sum of revenue: ");
                BigDecimal sum = scanner.nextBigDecimal();
                statement.executeUpdate("INSERT base(Description, Type, Money) VALUES ('" + description + "', " + "'Revenue', " + sum + ")");
                System.out.println("DB updated");
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong...");
            System.out.println(ex);
        }
    }
    
    //метод, добавляющий в таблицу расходы
    public static void addExpense(String url, String user, String password, Scanner scanner) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                Statement statement = conn.createStatement();
                System.out.println("Enter Description: ");
                String description = "";
                int i = 0;
                while (description == null || description.equals("") == true) {
                    description = scanner.nextLine();
                }
                System.out.println("Enter sum of expense: ");
                BigDecimal sum = scanner.nextBigDecimal();
                statement.executeUpdate("INSERT base(Description, Type, Money) VALUES ('" + description + "', " + "'Expense', " + sum + ")");
                System.out.println("DB updated");
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong...");
            System.out.println(ex);
        }
    }
    
    //проверка подключения к бд
    public static void checkConnection(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Connection to DB succesfull");
            }
        } catch (Exception ex) {
            System.out.println("Connection failed");
            System.out.println(ex);
            System.exit(0);
        }
    }
    
    //краткий отчет (метод показывает суммму всех затрат, доходов и общую прибыль)
    public static void shortReport(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement("select sum(Money) from base where Type='Revenue'");
                ResultSet result = statement.executeQuery();
                result.next();
                String sum = result.getString(1);
                System.out.println("Revenue sum: " + sum);
                BigDecimal revenue = new BigDecimal(sum);
                statement = connection.prepareStatement("select sum(Money) from base where Type='Expense'");
                result = statement.executeQuery();
                result.next();
                sum = result.getString(1);
                System.out.println("Expense sum: " + sum);
                BigDecimal expense = new BigDecimal(sum);
                BigDecimal profit = revenue.subtract(expense);
                System.out.println("Profit: " + profit);
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong...");
            System.out.println(ex);
        }
    }
    
    //статистика расходов (метод показывает, на что тратились деньги, сколько их тратилось и процент от общей суммы у каждой статьи расходов) 
    public static void expenseStats(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT DISTINCT Description FROM base WHERE Type='Expense'");
                ArrayList<String> help = new ArrayList<>();
                ArrayList<BigDecimal> sum = new ArrayList<>();
                while (resultSet.next()) {
                    help.add(resultSet.getString(1));
                }
                for (int j = 0; j < help.size(); j++) {
                    resultSet = statement.executeQuery("SELECT SUM(Money) from base WHERE Description='" + help.get(j) + "'");
                    resultSet.next();
                    sum.add(resultSet.getBigDecimal(1));
                }
                PreparedStatement statement1 = connection.prepareStatement("select sum(Money) from base where Type='Expense'");
                ResultSet result = statement1.executeQuery();
                result.next();
                BigDecimal sum1 = result.getBigDecimal(1);
                System.out.println("Expense sum: " + sum1);
                BigDecimal decimalProcent = new BigDecimal("100");
                for (int j = 0; j < help.size(); j++) {
                    System.out.println(help.get(j) + ": " + decimalProcent.multiply((sum.get(j)).divide(sum1, 2, RoundingMode.HALF_UP)) + "%");
                }
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong...");
            System.out.println(ex);
        }
    }
    
    //статистика расходов (метод показывает, откуда брались деньги, сколько money было заработано) 
    public static void revenueStats(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT DISTINCT Description FROM base WHERE Type='Revenue'");
                ArrayList<String> help = new ArrayList<>();
                ArrayList<BigDecimal> sum = new ArrayList<>();
                while (resultSet.next()) {
                    help.add(resultSet.getString(1));
                }
                for (int j = 0; j < help.size(); j++) {
                    resultSet = statement.executeQuery("SELECT SUM(Money) from base WHERE Description='" + help.get(j) + "'");
                    resultSet.next();
                    sum.add(resultSet.getBigDecimal(1));
                }
                PreparedStatement statement1 = connection.prepareStatement("select sum(Money) from base where Type='Revenue'");
                ResultSet result = statement1.executeQuery();
                result.next();
                BigDecimal sum1 = result.getBigDecimal(1);
                System.out.println("Revenue sum: " + sum1);
                BigDecimal decimalProcent = new BigDecimal("100");
                for (int j = 0; j < help.size(); j++) {
                    System.out.println(help.get(j) + ": " + decimalProcent.multiply((sum.get(j)).divide(sum1, 2, RoundingMode.HALF_UP)) + "%");
                }
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong...");
            System.out.println(ex);
        }
    }
    
    //показывает отфильтрованную пользователем бд
    public static void search(String url, String user, String password, Scanner scanner) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                Statement statement = connection.createStatement();
                System.out.println("Select: search with description (1), date (2), type(3), description and date(4), type and date(5)");
                int action = scanner.nextInt();
                ResultSet resultSet;
                switch (action) {
                    case 1:
                        System.out.println("Enter description: ");
                        String desc = "";
                        while (desc == null || desc.equals("") == true) {
                            desc = scanner.nextLine();
                        }
                        resultSet = statement.executeQuery("SELECT * from base where Description LIKE'" + desc + "%'");
                        while (resultSet.next()) {
                            int id = resultSet.getInt("ID");
                            String description = resultSet.getString("Description");
                            String type = resultSet.getString("Type");
                            BigDecimal money = resultSet.getBigDecimal("Money");
                            Timestamp date = resultSet.getTimestamp("Date");
                            System.out.println(id + "." + description + " - " + type + " - " + money + " - " + date);
                        }
                        break;
                    case 2:
                        System.out.println("Enter first date (yyyy-mm-dd): ");
                        String dateStart = "";
                        String dateFinish = "";
                        while (dateStart == null || dateStart.equals("") == true) {
                            dateStart = scanner.nextLine();
                        }
                        System.out.println("Enter second date (yyyy-mm-dd): ");
                        while (dateFinish == null || dateFinish.equals("") == true) {
                            dateFinish = scanner.nextLine();
                        }
                        resultSet = statement.executeQuery("SELECT * from base where date between '" + dateStart + " 00:00:00' AND '" + dateFinish + " 23:59:59'");
                        while (resultSet.next()) {
                            int id = resultSet.getInt("ID");
                            String description = resultSet.getString("Description");
                            String type = resultSet.getString("Type");
                            BigDecimal money = resultSet.getBigDecimal("Money");
                            Timestamp date = resultSet.getTimestamp("Date");
                            System.out.println(id + "." + description + " - " + type + " - " + money + " - " + date);
                        }
                        break;
                    case 3:
                        System.out.println("Select type: revenue(1) or expense(2):");
                        int choice = scanner.nextInt();
                        switch (choice) {
                            case 1:
                                resultSet = statement.executeQuery("SELECT * from base where Type='revenue'");
                                while (resultSet.next()) {
                                    int id = resultSet.getInt("ID");
                                    String description = resultSet.getString("Description");
                                    String type = resultSet.getString("Type");
                                    BigDecimal money = resultSet.getBigDecimal("Money");
                                    Timestamp date = resultSet.getTimestamp("Date");
                                    System.out.println(id + "." + description + " - " + type + " - " + money + " - " + date);
                                }
                                break;
                            case 2:
                                resultSet = statement.executeQuery("SELECT * from base where Type='Expense'");
                                while (resultSet.next()) {
                                    int id = resultSet.getInt("ID");
                                    String description = resultSet.getString("Description");
                                    String type = resultSet.getString("Type");
                                    BigDecimal money = resultSet.getBigDecimal("Money");
                                    Timestamp date = resultSet.getTimestamp("Date");
                                    System.out.println(id + "." + description + " - " + type + " - " + money + " - " + date);
                                }
                                break;
                        }
                        break;
                    case 4:
                        System.out.println("Enter description: ");
                        String desc1 = "";
                        while (desc1 == null || desc1.equals("") == true) {
                            desc1 = scanner.nextLine();
                        }
                        System.out.println("Enter first date(yyyy-mm-dd): ");
                        String dateStart1 = "";
                        String dateFinish1 = "";
                        while (dateStart1 == null || dateStart1.equals("") == true) {
                            dateStart1 = scanner.nextLine();
                        }
                        System.out.println("Enter second date: ");
                        while (dateFinish1 == null || dateFinish1.equals("") == true) {
                            dateFinish1 = scanner.nextLine();
                        }
                        resultSet = statement.executeQuery("SELECT * from base where  Description LIKE'" + desc1 + "%' and date between '" + dateStart1 + " 00:00:00' AND '" + dateFinish1 + " 23:59:59'");
                        while (resultSet.next()) {
                            int id = resultSet.getInt("ID");
                            String description = resultSet.getString("Description");
                            String type = resultSet.getString("Type");
                            BigDecimal money = resultSet.getBigDecimal("Money");
                            Timestamp date = resultSet.getTimestamp("Date");
                            System.out.println(id + "." + description + " - " + type + " - " + money + " - " + date);
                        }
                        break;
                    case 5:
                        System.out.println("Enter first date:");
                        String dateStart2 = "";
                        String dateFinish2 = "";
                        while (dateStart2 == null || dateStart2.equals("") == true) {
                            dateStart2 = scanner.nextLine();
                        }
                        System.out.println("Enter second date (yyyy-mm-dd): ");
                        while (dateFinish2 == null || dateFinish2.equals("") == true) {
                            dateFinish2 = scanner.nextLine();
                        }
                        System.out.println("Select type: revenue(1) or expense(2):");
                        choice = scanner.nextInt();
                        switch (choice) {
                            case 1:
                                resultSet = statement.executeQuery("SELECT * from base where Type='Revenue' and date between '" + dateStart2 + " 00:00:00' AND '" + dateFinish2 + " 23:59:59'");
                                while (resultSet.next()) {
                                    int id = resultSet.getInt("ID");
                                    String description = resultSet.getString("Description");
                                    String type = resultSet.getString("Type");
                                    BigDecimal money = resultSet.getBigDecimal("Money");
                                    Timestamp date = resultSet.getTimestamp("Date");
                                    System.out.println(id + "." + description + " - " + type + " - " + money + " - " + date);
                                }
                                break;
                            case 2:
                                resultSet = statement.executeQuery("SELECT * from base where Type='Expense' and date between '" + dateStart2 + " 00:00:00' AND '" + dateFinish2 + " 23:59:59'");
                                while (resultSet.next()) {
                                    int id = resultSet.getInt("ID");
                                    String description = resultSet.getString("Description");
                                    String type = resultSet.getString("Type");
                                    BigDecimal money = resultSet.getBigDecimal("Money");
                                    Timestamp date = resultSet.getTimestamp("Date");
                                    System.out.println(id + "." + description + " - " + type + " - " + money + " - " + date);
                                }
                                break;
                        }
                }
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong...");
            System.out.println(ex);
        }
    }
    
    //привет, Женя
    public static void readMe() {
        System.out.println("Привет, Женечка. Как дела?!1!!1!?!?!?!?!?1!!!");
    }
    
    //метод, реализующий задания (пока не работает)
    public static void tasksManager(String url, String user, String password, Scanner scanner) {
        System.out.println("Doesn't work right now");
    }
    
    //показывает денежные операции за последний день, неделю, месяц
    public static void analytics(String url, String user, String password) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT SUM(Money) from base where type='Expense' AND Date>=current_date()");
                resultSet.next();
                BigDecimal result = resultSet.getBigDecimal("SUM(Money)");
                System.out.println("Today's expenses: " + result);
                resultSet = statement.executeQuery("SELECT SUM(Money) from base where type='Expense' AND Date>=DATE_ADD(current_date(), INTERVAL -7 DAY)");
                resultSet.next();
                result = resultSet.getBigDecimal("SUM(Money)");
                System.out.println("This week's expenses: " + result);
                resultSet = statement.executeQuery("SELECT SUM(Money) from base where type='Expense' AND Date>=DATE_ADD(current_date(), INTERVAL -1 MONTH)");
                resultSet.next();
                result = resultSet.getBigDecimal("SUM(Money)");
                System.out.println("This month's expenses: " + result);
                resultSet = statement.executeQuery("SELECT Date from base where ID=1");
                resultSet.next();
                Timestamp firstTime = resultSet.getTimestamp("Date");
                resultSet = statement.executeQuery("SELECT DATEDIFF(current_date(), '" +  firstTime + "')");
                resultSet.next();
                BigDecimal diff = resultSet.getBigDecimal("DATEDIFF(current_date(), '" +  firstTime + "')");
                diff = (diff.divide(new BigDecimal("30"), 0, BigDecimal.ROUND_HALF_DOWN)).add(new BigDecimal("1"));
                resultSet = statement.executeQuery("SELECT SUM(Money) from base where Type='Revenue'");
                resultSet.next();
                result = resultSet.getBigDecimal("SUM(Money)");
                System.out.println("Average monthly revenue: "+ result.divide(diff, 2, BigDecimal.ROUND_HALF_UP));
                resultSet = statement.executeQuery("SELECT SUM(Money) from base where Type='Expense'");
                resultSet.next();
                result = resultSet.getBigDecimal("SUM(Money)");
                System.out.println("Average monthly expense: "+ result.divide(diff, 2, BigDecimal.ROUND_HALF_UP));
            }
        }
        catch (Exception ex) {
            System.out.println("Something went wrong...");
            System.out.println(ex);
        }
    }
}
