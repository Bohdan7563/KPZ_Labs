@SuppressWarnings("all")
public class CinemaMain11 {
    public static void main(String[] args) {
        System.out.println("=== ЛАБОРАТОРНА РОБОТА №11 (Варіант 4: Кінотеатр) ===\n");
        task1Adapter();
        System.out.println("\n--------------------------------------------------\n");
        task2Proxy();
        System.out.println("\n--------------------------------------------------\n");
        task3Decorator();
        System.out.println("\n--------------------------------------------------\n");
        task4Facade();
    }

    // =========================================================================
    // ЗАВДАННЯ 1: ADAPTER (Платіжний термінал)
    // =========================================================================
    static void task1Adapter() {
        System.out.println("--- Завдання 1: Adapter (Платіжний термінал) ---");
        BankTerminal weirdTerminal = new BankTerminal();
        PaymentProcessor cashRegister = new BankTerminalAdapter(weirdTerminal);

        System.out.println("[10:00:00] [Каса] Спроба оплати квитка на суму 180 грн...");
        boolean success = cashRegister.pay(180.0);
        System.out.println("[10:00:01] [Каса] Результат оплати: " + (success ? "УСПІХ" : "ВІДМОВА"));

        System.out.println("\n[10:05:00] [Каса] Спроба оплати квитка на суму 250 грн (емуляція збою)...");
        boolean failed = cashRegister.pay(250.0);
        System.out.println("[10:05:01] [Каса] Результат оплати: " + (failed ? "УСПІХ" : "ВІДМОВА (Причина: Недостатньо коштів)"));
    }

    // =========================================================================
    // ЗАВДАННЯ 2: PROXY (Трейлери фільмів)
    // =========================================================================
    static void task2Proxy() {
        System.out.println("--- Завдання 2: Proxy (Трейлери фільмів) ---");
        
        System.out.println("[11:00:00] [Афіша] Відкриття афіші з 6 фільмами. Завантаження трейлерів...");
        Trailer trailer1 = new TrailerProxy("Дюна 2", false);
        Trailer trailer2 = new TrailerProxy("Хаскі", true); // 18+

        User adultUser = new User("Іван", 25, true);
        User minorUser = new User("Петро", 16, true);
        User guestUser = new User("Гість", 20, false);

        System.out.println("\n[11:01:00] [Глядач Іван] Запускає трейлер 'Дюна 2'...");
        trailer1.play(adultUser);

        System.out.println("\n[11:02:00] [Глядач Іван] Запускає трейлер 'Дюна 2' ще раз...");
        trailer1.play(adultUser);

        System.out.println("\n[11:03:00] [Глядач Петро (16)] Запускає трейлер 'Хаскі'...");
        trailer2.play(minorUser);

        System.out.println("\n[11:04:00] [Глядач Гість (не авторизований)] Запускає трейлер 'Хаскі'...");
        trailer2.play(guestUser);
    }

    // =========================================================================
    // ЗАВДАННЯ 3: DECORATOR (Модифікації сеансу)
    // =========================================================================
    static void task3Decorator() {
        System.out.println("--- Завдання 3: Decorator (Модифікації сеансу) ---");
        
        System.out.println("[12:00:00] [Афіша] Базовий сеанс:");
        MovieSession session = new BaseSession("Аватар 2");
        System.out.println("  " + session.getDescription() + " | Ціна: " + session.getPrice() + " грн");

        System.out.println("\n[12:05:00] [Афіша] Сеанс 3D із субтитрами:");
        session = new BaseSession("Аватар 2");
        session = new Session3DDecorator(session);
        session = new SubtitlesDecorator(session);
        System.out.println("  " + session.getDescription() + " | Ціна: " + session.getPrice() + " грн");

        System.out.println("\n[12:10:00] [Афіша] 'Тихий сеанс' зі знижкою вихідного дня:");
        session = new BaseSession("Аватар 2");
        session = new QuietSessionDecorator(session);
        session = new WeekendDiscountDecorator(session);
        System.out.println("  " + session.getDescription() + " | Ціна: " + session.getPrice() + " грн");
    }

    // =========================================================================
    // ЗАВДАННЯ 4: FACADE (Підготовка сеансу)
    // =========================================================================
    static void task4Facade() {
        System.out.println("--- Завдання 4: Facade (Підготовка сеансу) ---");
        
        SessionPreparationFacade facade = new SessionPreparationFacade();

        System.out.println("[13:00:00] [Адміністратор] Готує сеанс однією командою...");
        facade.prepareSession();

        System.out.println("\n[13:10:00] [Автоматичний розклад] Готує наступний сеанс...");
        facade.prepareSession();

        System.out.println("\n[13:15:00] [Аварійна ситуація] Треба лише увімкнути світло!");
        Lighting emergencyLight = new Lighting();
        emergencyLight.turnOnLight();
    }
}

// =========================================================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 1 (Adapter)
// =========================================================================
@SuppressWarnings("all")
interface PaymentProcessor {
    boolean pay(double amountUah);
}

@SuppressWarnings("all")
class BankTerminal {
    // 0 - успіх, 1 - недостатньо коштів, 2 - збій мережі
    public int processPayment(int amountKopecks) {
        System.out.println("  [Термінал] Отримано запит на " + amountKopecks + " копійок.");
        if (amountKopecks == 18000) return 0;
        return 1;
    }
}

@SuppressWarnings("all")
class BankTerminalAdapter implements PaymentProcessor {
    private final BankTerminal terminal;
    public BankTerminalAdapter(BankTerminal terminal) { this.terminal = terminal; }

    @Override
    public boolean pay(double amountUah) {
        int kopecks = (int) (amountUah * 100);
        int resultCode = terminal.processPayment(kopecks);
        System.out.println("  [Адаптер] Термінал повернув код: " + resultCode);
        return resultCode == 0; // Перетворення коду у boolean
    }
}

// =========================================================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 2 (Proxy)
// =========================================================================
@SuppressWarnings("all")
class User {
    String name; int age; boolean isAuthorized;
    public User(String name, int age, boolean isAuthorized) {
        this.name = name; this.age = age; this.isAuthorized = isAuthorized;
    }
}

@SuppressWarnings("all")
interface Trailer {
    void play(User user);
}

@SuppressWarnings("all")
class RealTrailer implements Trailer {
    private final String movieName;
    public RealTrailer(String movieName) { this.movieName = movieName; }
    
    public void loadVideo() {
        System.out.println("  [Сервер] Дороге завантаження відеофайлу '" + movieName + "'... (зайняло 2 сек)");
    }
    
    @Override
    public void play(User user) {
        System.out.println("  [Плеєр] Відтворення трейлера '" + movieName + "' для " + user.name);
    }
}

@SuppressWarnings("all")
class TrailerProxy implements Trailer {
    private final String movieName;
    private final boolean isAgeRestricted;
    private final RealTrailer realTrailer;
    private boolean isLoaded = false;

    public TrailerProxy(String movieName, boolean isAgeRestricted) {
        this.movieName = movieName;
        this.isAgeRestricted = isAgeRestricted;
        this.realTrailer = new RealTrailer(movieName);
    }

    @Override
    public void play(User user) {
        if (isAgeRestricted && (!user.isAuthorized || user.age < 18)) {
            System.out.println("  [Проксі] ВІДМОВА: Перегляд доступний лише після підтвердження віку (18+).");
            return;
        }
        if (!isLoaded) {
            realTrailer.loadVideo();
            isLoaded = true;
        } else {
            System.out.println("  [Проксі] Відео вже в кеші, завантаження не потрібне.");
        }
        realTrailer.play(user);
    }
}

// =========================================================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 3 (Decorator)
// =========================================================================
@SuppressWarnings("all")
interface MovieSession {
    String getDescription();
    double getPrice();
}

@SuppressWarnings("all")
class BaseSession implements MovieSession {
    private final String movieName;
    public BaseSession(String movieName) { this.movieName = movieName; }
    @Override public String getDescription() { return "Сеанс '" + movieName + "'"; }
    @Override public double getPrice() { return 150.0; }
}

@SuppressWarnings("all")
abstract class SessionDecorator implements MovieSession {
    protected final MovieSession wrappedSession;
    public SessionDecorator(MovieSession session) { this.wrappedSession = session; }
    @Override public String getDescription() { return wrappedSession.getDescription(); }
    @Override public double getPrice() { return wrappedSession.getPrice(); }
}

@SuppressWarnings("all") class Session3DDecorator extends SessionDecorator {
    public Session3DDecorator(MovieSession s) { super(s); }
    @Override public String getDescription() { return wrappedSession.getDescription() + " [3D]"; }
    @Override public double getPrice() { return wrappedSession.getPrice() + 50.0; }
}

@SuppressWarnings("all") class SubtitlesDecorator extends SessionDecorator {
    public SubtitlesDecorator(MovieSession s) { super(s); }
    @Override public String getDescription() { return wrappedSession.getDescription() + " [Субтитри]"; }
    @Override public double getPrice() { return wrappedSession.getPrice() + 0.0; }
}

@SuppressWarnings("all") class QuietSessionDecorator extends SessionDecorator {
    public QuietSessionDecorator(MovieSession s) { super(s); }
    @Override public String getDescription() { return wrappedSession.getDescription() + " [Тихий сеанс]"; }
    @Override public double getPrice() { return wrappedSession.getPrice() - 20.0; }
}

@SuppressWarnings("all") class WeekendDiscountDecorator extends SessionDecorator {
    public WeekendDiscountDecorator(MovieSession s) { super(s); }
    @Override public String getDescription() { return wrappedSession.getDescription() + " [Знижка вихідного дня]"; }
    @Override public double getPrice() { return wrappedSession.getPrice() * 0.8; } // -20%
}

// =========================================================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 4 (Facade)
// =========================================================================
// Підсистеми
@SuppressWarnings("all") class MovieLoader { public void loadCopy() { System.out.println("  [Підсистема] Завантаження копії фільму..."); } }
@SuppressWarnings("all") class Projector { public void setup() { System.out.println("  [Підсистема] Налаштування проектора..."); } }
@SuppressWarnings("all") class SoundSystem { public void configure() { System.out.println("  [Підсистема] Налаштування звукової системи..."); } }
@SuppressWarnings("all") class Lighting { 
    public void dimLights() { System.out.println("  [Підсистема] Світло приглушено."); }
    public void turnOnLight() { System.out.println("  [Підсистема] Світло увімкнено (аварійний режим)."); }
}
@SuppressWarnings("all") class TicketingSystem { public void openSales() { System.out.println("  [Підсистема] Відкрито продаж квитків."); } }
@SuppressWarnings("all") class AdSystem { public void playAds() { System.out.println("  [Підсистема] Увімкнено рекламу."); } }

// Фасад
@SuppressWarnings("all")
class SessionPreparationFacade {
    private final MovieLoader loader = new MovieLoader();
    private final Projector projector = new Projector();
    private final SoundSystem sound = new SoundSystem();
    private final Lighting lights = new Lighting();
    private final TicketingSystem ticketing = new TicketingSystem();
    private final AdSystem ads = new AdSystem();

    public void prepareSession() {
        loader.loadCopy();
        projector.setup();
        sound.configure();
        lights.dimLights();
        ticketing.openSales();
        ads.playAds();
        System.out.println("  [Фасад] Сеанс готовий до показу!");
    }
}
