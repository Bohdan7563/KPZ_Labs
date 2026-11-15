import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class CinemaMain {
    public static void main(String[] args) {
        System.out.println("=== ЛАБОРАТОРНА РОБОТА №10 (Варіант 4: Кінотеатр) ===\n");
        task1Builder();
        System.out.println("\n--------------------------------------------------\n");
        task2FactoryMethod();
        System.out.println("\n--------------------------------------------------\n");
        task3AbstractFactory();
    }

    // =========================================================================
    // ЗАВДАННЯ 1: BUILDER (Замовлення глядача)
    // =========================================================================
    static void task1Builder() {
        System.out.println("--- Завдання 1: Builder (Замовлення глядача) ---");
        
        // Демонстрація: спроба створити замовлення без місць (має викликати помилку)
        try {
            System.out.println("[10:00:00] [Каса] Спроба сформувати замовлення без місць...");
            new TicketOrder.TicketOrderBuilder("Аватар 2").build();
        } catch (IllegalStateException e) {
            System.out.println("[10:00:01] [Каса] Помилка: " + e.getMessage());
        }

        // Демонстрація: повне замовлення з попкорном, напоями і промокодом
        System.out.println("\n[10:05:00] [Каса] Формування індивідуального замовлення...");
        TicketOrder customOrder = new TicketOrder.TicketOrderBuilder("Аватар 2 (3D)")
                .addSeat("12A").addSeat("12B")
                .addPopcorn("Карамельний")
                .addDrinks("2 Коли")
                .addPromoCode("STUDENT10")
                .build();
        System.out.println("[10:05:05] [Каса] Замовлення створено:\n" + customOrder);

        // Демонстрація: типове замовлення через Director
        System.out.println("\n[10:10:00] [Каса] Формування типового замовлення 'Комбо на двох'...");
        OrderDirector director = new OrderDirector();
        TicketOrder comboOrder = director.buildComboForTwo(new TicketOrder.TicketOrderBuilder("Дюна 2"));
        System.out.println("[10:10:02] [Каса] Замовлення створено:\n" + comboOrder);
    }

    // =========================================================================
    // ЗАВДАННЯ 2: FACTORY METHOD (Квитки для різних каналів)
    // =========================================================================
    static void task2FactoryMethod() {
        System.out.println("--- Завдання 2: Factory Method (Квитки) ---");
        
        SalesProcess process = new SalesProcess();

        System.out.println("[11:00:00] [Каса] Початок продажу квитка...");
        process.sellTicket(new BoxOfficeFactory(), "Місце 5A");

        System.out.println("\n[11:05:00] [Сайт] Початок продажу квитка...");
        process.sellTicket(new WebFactory(), "Місце 7C");

        System.out.println("\n[11:10:00] [Застосунок] Початок продажу квитка...");
        process.sellTicket(new AppFactory(), "Місце 10F");

        // Вимога: підключити термінал самообслуговування новими класами
        System.out.println("\n[11:15:00] [Термінал самообслуговування] Початок продажу квитка...");
        process.sellTicket(new KioskFactory(), "Місце 1A");
    }

    // =========================================================================
    // ЗАВДАННЯ 3: ABSTRACT FACTORY (Налаштування залу)
    // =========================================================================
    static void task3AbstractFactory() {
        System.out.println("--- Завдання 3: Abstract Factory (Налаштування залу) ---");

        System.out.println("[12:00:00] [Кіномеханік] Запуск сеансу 2D...");
        startSession(new Format2DFactory());

        System.out.println("\n[12:10:00] [Кіномеханік] Запуск сеансу 3D...");
        startSession(new Format3DFactory());

        System.out.println("\n[12:20:00] [Кіномеханік] Запуск сеансу Dolby Atmos...");
        startSession(new FormatDolbyFactory());

        // Вимога: додати формат IMAX без зміни коду запуску
        System.out.println("\n[12:30:00] [Кіномеханік] Запуск сеансу IMAX...");
        startSession(new FormatIMAXFactory());
    }

    // Допоміжний метод (клієнтський код, який не знає конкретних класів)
    static void startSession(HallFormatFactory factory) {
        FilmCopy film = factory.createFilm();
        ProjectorProfile projector = factory.createProjector();
        SoundProfile sound = factory.createSound();
        
        film.play();
        projector.setup();
        sound.configure();
        System.out.println("  -> Сеанс успішно налаштовано та розпочато!");
    }
}

// ==========================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 1 (Builder)
// ==========================================
@SuppressWarnings("all")
class TicketOrder {
    private final String session;
    private final List<String> seats;
    private final String popcorn;
    private final String drinks;
    private final String promoCode;

    private TicketOrder(TicketOrderBuilder builder) {
        this.session = builder.session;
        this.seats = builder.seats;
        this.popcorn = builder.popcorn;
        this.drinks = builder.drinks;
        this.promoCode = builder.promoCode;
    }

    @Override
    public String toString() {
        return "  Сеанс: " + session + "\n" +
               "  Місця: " + seats + "\n" +
               "  Попкорн: " + (popcorn != null ? popcorn : "ні") + "\n" +
               "  Напої: " + (drinks != null ? drinks : "ні") + "\n" +
               "  Промокод: " + (promoCode != null ? promoCode : "ні");
    }

    public static class TicketOrderBuilder {
        private final String session;
        private final List<String> seats = new ArrayList<>();
        private String popcorn;
        private String drinks;
        private String promoCode;

        public TicketOrderBuilder(String session) {
            this.session = session;
        }

        public TicketOrderBuilder addSeat(String seat) { seats.add(seat); return this; }
        public TicketOrderBuilder addPopcorn(String type) { this.popcorn = type; return this; }
        public TicketOrderBuilder addDrinks(String type) { this.drinks = type; return this; }
        public TicketOrderBuilder addPromoCode(String code) { this.promoCode = code; return this; }

        public TicketOrder build() {
            // Перевірка на завершальному кроці
            if (session == null || session.isBlank()) {
                throw new IllegalStateException("Неможливо сформувати замовлення: не вказано сеанс.");
            }
            if (seats.isEmpty()) {
                throw new IllegalStateException("Неможливо сформувати замовлення: не обрано жодного місця.");
            }
            return new TicketOrder(this);
        }
    }
}

@SuppressWarnings("all")
class OrderDirector {
    public TicketOrder buildComboForTwo(TicketOrder.TicketOrderBuilder builder) {
        return builder
                .addSeat("Ряд 5, Місце 1")
                .addSeat("Ряд 5, Місце 2")
                .addPopcorn("Великий")
                .addDrinks("2 Коли")
                .build();
    }
}

// ==========================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 2 (Factory Method)
// ==========================================
@SuppressWarnings("all")
interface Ticket { void printView(); }

class PaperTicket implements Ticket {
    @Override public void printView() { System.out.println("  [Друк] Паперовий квиток з QR-кодом."); }
}
class PdfTicket implements Ticket {
    @Override public void printView() { System.out.println("  [PDF] Електронний квиток надіслано на email."); }
}
class AppTicket implements Ticket {
    @Override public void printView() { System.out.println("  [App] Квиток з QR-кодом додано в гаманець."); }
}
class KioskTicket implements Ticket {
    @Override public void printView() { System.out.println("  [Кіоск] Термінал надрукував чек з QR-кодом."); }
}

@SuppressWarnings("all")
interface TicketFactory { Ticket createTicket(); }

class BoxOfficeFactory implements TicketFactory {
    @Override public Ticket createTicket() { return new PaperTicket(); }
}
class WebFactory implements TicketFactory {
    @Override public Ticket createTicket() { return new PdfTicket(); }
}
class AppFactory implements TicketFactory {
    @Override public Ticket createTicket() { return new AppTicket(); }
}
class KioskFactory implements TicketFactory {
    @Override public Ticket createTicket() { return new KioskTicket(); }
}

@SuppressWarnings("all")
class SalesProcess {
    public void sellTicket(TicketFactory factory, String seat) {
        System.out.println("  [Система] Резервування місця " + seat + "...");
        System.out.println("  [Система] Прийом оплати...");
        Ticket ticket = factory.createTicket(); // Делегуємо створення фабриці
        ticket.printView();
        System.out.println("  [Система] Запис продажу в базу.");
    }
}

// ==========================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 3 (Abstract Factory)
// ==========================================
@SuppressWarnings("all")
interface FilmCopy { void play(); }
interface ProjectorProfile { void setup(); }
interface SoundProfile { void configure(); }

// 2D
class Film2D implements FilmCopy { @Override public void play() { System.out.println("  [Фільм] Завантажено 2D-копію фільму."); } }
class Projector2D implements ProjectorProfile { @Override public void setup() { System.out.println("  [Проектор] Налаштовано 2D-профіль (24 fps)."); } }
class SoundStereo implements SoundProfile { @Override public void configure() { System.out.println("  [Звук] Увімкнено стерео 5.1."); } }

// 3D
class Film3D implements FilmCopy { @Override public void play() { System.out.println("  [Фільм] Завантажено 3D-копію фільму (SBS)."); } }
class Projector3D implements ProjectorProfile { @Override public void setup() { System.out.println("  [Проектор] Налаштовано 3D-профіль (48 fps, поляризація)."); } }
class SoundSurround implements SoundProfile { @Override public void configure() { System.out.println("  [Звук] Увімкнено 7.1 Surround."); } }

// Dolby Atmos
class FilmDolby implements FilmCopy { @Override public void play() { System.out.println("  [Фільм] Завантажено Dolby Vision копію."); } }
class ProjectorDolby implements ProjectorProfile { @Override public void setup() { System.out.println("  [Проектор] Налаштовано Dolby Cinema профіль (HDR)."); } }
class SoundDolby implements SoundProfile { @Override public void configure() { System.out.println("  [Звук] Увімкнено Dolby Atmos (64 канали)."); } }

// IMAX (Додано для вимоги)
class FilmIMAX implements FilmCopy { @Override public void play() { System.out.println("  [Фільм] Завантажено IMAX-копію фільму."); } }
class ProjectorIMAX implements ProjectorProfile { @Override public void setup() { System.out.println("  [Проектор] Налаштовано IMAX Laser профіль."); } }
class SoundIMAX implements SoundProfile { @Override public void configure() { System.out.println("  [Звук] Увімкнено IMAX 12-канальний звук."); } }

// Абстрактна фабрика
@SuppressWarnings("all")
interface HallFormatFactory {
    FilmCopy createFilm();
    ProjectorProfile createProjector();
    SoundProfile createSound();
}

class Format2DFactory implements HallFormatFactory {
    @Override public FilmCopy createFilm() { return new Film2D(); }
    @Override public ProjectorProfile createProjector() { return new Projector2D(); }
    @Override public SoundProfile createSound() { return new SoundStereo(); }
}

class Format3DFactory implements HallFormatFactory {
    @Override public FilmCopy createFilm() { return new Film3D(); }
    @Override public ProjectorProfile createProjector() { return new Projector3D(); }
    @Override public SoundProfile createSound() { return new SoundSurround(); }
}

class FormatDolbyFactory implements HallFormatFactory {
    @Override public FilmCopy createFilm() { return new FilmDolby(); }
    @Override public ProjectorProfile createProjector() { return new ProjectorDolby(); }
    @Override public SoundProfile createSound() { return new SoundDolby(); }
}

class FormatIMAXFactory implements HallFormatFactory {
    @Override public FilmCopy createFilm() { return new FilmIMAX(); }
    @Override public ProjectorProfile createProjector() { return new ProjectorIMAX(); }
    @Override public SoundProfile createSound() { return new SoundIMAX(); }
}
