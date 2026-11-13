import java.util.Scanner;

@SuppressWarnings("all")
public class LibrarySystem {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        // Pure Fabrication: Створюємо інфраструктурні класи
        LoanRepository repository = new FileLoanRepository();
        NotificationService notifier = new EmailNotificationService();
        
        // Controller: передаємо йому залежності
        LibraryController controller = new LibraryController(repository, notifier);
        
        // Thin Console UI: відповідає лише за ввід/вивід
        ConsoleUI ui = new ConsoleUI(controller);
        ui.start();
    }
}

// ==========================================
// 1. DOMAIN (Rich Domain Model + Polymorphism)
// ==========================================

// Polymorphism + Protected Variations: Інтерфейс для категорій книг
@SuppressWarnings("all")
interface BookCategory {
    double calculateDeposit(double bookValue);
    double calculateServiceFee();
}

class StandardCategory implements BookCategory {
    @Override public double calculateDeposit(double bookValue) { return 0.0; }
    @Override public double calculateServiceFee() { return 50.0; }
}

class AcademicCategory implements BookCategory {
    @Override public double calculateDeposit(double bookValue) { return bookValue * 0.2; }
    @Override public double calculateServiceFee() { return 100.0; }
}

class RareCategory implements BookCategory {
    @Override public double calculateDeposit(double bookValue) { return bookValue * 0.5; }
    @Override public double calculateServiceFee() { return 250.0; }
}

@SuppressWarnings("all")
class Book {
    private final int id;
    private final String title;
    private final BookCategory category; // Тепер категорія — це об'єкт, а не рядок
    private final double valueAmount;

    public Book(int id, String title, BookCategory category, double valueAmount) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.valueAmount = valueAmount;
    }
    public BookCategory getCategory() { return category; }
    public double getValueAmount() { return valueAmount; }
    public String getTitle() { return title; }
}

@SuppressWarnings("all")
class Reader {
    private final int id;
    private final String name;
    private final String email;
    
    public Reader(int id, String name, String email) {
        this.id = id; this.name = name; this.email = email;
    }
    public String getName() { return name; }
    public String getEmail() { return email; }
}

// Information Expert: Цей клас сам знає, як рахувати свою заставу та комісію
@SuppressWarnings("all")
class LoanRecord {
    private final Reader reader;
    private final Book book;
    private double depositFee;
    private double serviceFee;
    private double totalPayable;
    private String status;

    private LoanRecord(Reader reader, Book book) {
        this.reader = reader;
        this.book = book;
        this.status = "Draft";
    }

    // Creator: Статична фабрика гарантує, що об'єкт створиться у валідному стані
    public static LoanRecord createLoan(Reader reader, Book book) {
        if (reader == null || book == null) throw new IllegalArgumentException("Дані некоректні");
        LoanRecord loan = new LoanRecord(reader, book);
        loan.processIssuance();
        return loan;
    }

    private void processIssuance() {
        // Делегуємо розрахунок поліморфному об'єкту category
        this.depositFee = book.getCategory().calculateDeposit(book.getValueAmount());
        this.serviceFee = book.getCategory().calculateServiceFee();
        this.totalPayable = this.depositFee + this.serviceFee;
        this.status = "Issued";
    }

    public Reader getReader() { return reader; }
    public Book getBook() { return book; }
    public double getTotalPayable() { return totalPayable; }
    public String getStatus() { return status; }
}

// ==========================================
// 2. PURE FABRICATION & INDIRECTION (Infrastructure)
// ==========================================

// Indirection: Абстракція для збереження
@SuppressWarnings("all")
interface LoanRepository {
    void save(LoanRecord loan);
}

@SuppressWarnings("all")
class FileLoanRepository implements LoanRepository {
    @Override
    public void save(LoanRecord loan) {
        System.out.println("[FILE DB] Loan for " + loan.getBook().getTitle() + " saved to library_audit.log");
    }
}

// Indirection: Абстракція для сповіщень
@SuppressWarnings("all")
interface NotificationService {
    void sendNotification(Reader reader, String message);
}

@SuppressWarnings("all")
class EmailNotificationService implements NotificationService {
    @Override
    public void sendNotification(Reader reader, String message) {
        System.out.println("[EMAIL] Sent to " + reader.getEmail() + ": " + message);
    }
}

// ==========================================
// 3. CONTROLLER (Application Service)
// ==========================================

@SuppressWarnings("all")
class LibraryController {
    private final LoanRepository repository;
    private final NotificationService notifier;

    public LibraryController(LoanRepository repository, NotificationService notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    // Контролер приймає дані, створює доменний об'єкт і зберігає його
    public void issueBook(String readerName, String bookTitle, String categoryType) {
        Reader reader = new Reader(1, readerName, "reader@univ.edu");
        
        // Тут можна було б використати Factory для категорій, але для простоти робимо switch
        BookCategory category = switch (categoryType.toLowerCase()) {
            case "rare" -> new RareCategory();
            case "academic" -> new AcademicCategory();
            default -> new StandardCategory();
        };
        
        Book book = new Book(104, bookTitle, category, 1500.0);
        
        // Делегуємо створення LoanRecord фабриці
        LoanRecord loan = LoanRecord.createLoan(reader, book);
        
        // Зберігаємо через репозиторій
        repository.save(loan);
        
        // Відправляємо сповіщення
        notifier.sendNotification(reader, "Book issued. Total payable: " + loan.getTotalPayable());
    }
}

// ==========================================
// 4. THIN UI (Console)
// ==========================================
@SuppressWarnings("all")
class ConsoleUI {
    private final LibraryController controller;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(LibraryController controller) {
        this.controller = controller;
    }

    public void start() {
        System.out.println("=== Library System ===");
        System.out.print("Enter reader name: ");
        String name = scanner.nextLine();
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter category (Standard/Rare/Academic): ");
        String category = scanner.nextLine();

        // UI викликає контролер і передає йому дані
        controller.issueBook(name, title, category);
    }
}
