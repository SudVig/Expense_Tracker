import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class User implements Serializable {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}

class Expense implements Serializable {
    private LocalDate date;
    private String category;
    private double amount;

    public Expense(LocalDate date, String category, double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "date=" + date +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                '}';
    }
}

class ExpenseManager {
    private List<Expense> expenses;

    public ExpenseManager() {
        this.expenses = new ArrayList<>();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public List<Expense> listExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Expense> filterExpensesByCategory(String category) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public double sumExpensesByCategory(String category) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public void saveExpenses(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(expenses);
        }
    }

    public void loadExpenses(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            expenses = (List<Expense>) ois.readObject();
        }
    }
}

public class expense_tracker {
    private static List<User> users = new ArrayList<>();
    private static User currentUser = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseManager expenseManager = new ExpenseManager();

        while (true) {
            if (currentUser == null) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1:
                        System.out.println("Enter username:");
                        String username = scanner.nextLine();
                        System.out.println("Enter password:");
                        String password = scanner.nextLine();
                        users.add(new User(username, password));
                        System.out.println("User registered successfully.");
                        break;
                    case 2:
                        System.out.println("Enter username:");
                        username = scanner.nextLine();
                        System.out.println("Enter password:");
                        password = scanner.nextLine();
                        currentUser = users.stream()
                                .filter(user -> user.getUsername().equals(username) && user.checkPassword(password))
                                .findFirst()
                                .orElse(null);
                        if (currentUser == null) {
                            System.out.println("Invalid username or password.");
                        } else {
                            System.out.println("Login successful.");
                        }
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("1. Add Expense");
                System.out.println("2. List Expenses");
                System.out.println("3. Filter Expenses by Category");
                System.out.println("4. Sum Expenses by Category");
                System.out.println("5. Save Expenses");
                System.out.println("6. Load Expenses");
                System.out.println("7. Logout");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1:
                        System.out.println("Enter date (yyyy-mm-dd):");
                        LocalDate date = LocalDate.parse(scanner.nextLine());
                        System.out.println("Enter category:");
                        String category = scanner.nextLine();
                        System.out.println("Enter amount:");
                        double amount = scanner.nextDouble();
                        scanner.nextLine();  // Consume newline
                        expenseManager.addExpense(new Expense(date, category, amount));
                        break;
                    case 2:
                        expenseManager.listExpenses().forEach(System.out::println);
                        break;
                    case 3:
                        System.out.println("Enter category:");
                        String filterCategory = scanner.nextLine();
                        expenseManager.filterExpensesByCategory(filterCategory).forEach(System.out::println);
                        break;
                    case 4:
                        System.out.println("Enter category:");
                        String sumCategory = scanner.nextLine();
                        double total = expenseManager.sumExpensesByCategory(sumCategory);
                        System.out.println("Total expenses for " + sumCategory + ": " + total);
                        break;
                    case 5:
                        System.out.println("Enter filename to save:");
                        String saveFilename = scanner.nextLine();
                        try {
                            expenseManager.saveExpenses(saveFilename);
                            System.out.println("Expenses saved.");
                        } catch (IOException e) {
                            System.err.println("Error saving expenses: " + e.getMessage());
                        }
                        break;
                    case 6:
                        System.out.println("Enter filename to load:");
                        String loadFilename = scanner.nextLine();
                        try {
                            expenseManager.loadExpenses(loadFilename);
                            System.out.println("Expenses loaded.");
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Error loading expenses: " + e.getMessage());
                        }
                        break;
                    case 7:
                        currentUser = null;
                        System.out.println("Logged out.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }
}
