import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class CinemaMain12 {
    public static void main(String[] args) {
        System.out.println("=== ЛАБОРАТОРНА РОБОТА №12 (Варіант 4: Кінотеатр) ===\n");
        task1Composite();
        System.out.println("\n--------------------------------------------------\n");
        task2Flyweight();
        System.out.println("\n--------------------------------------------------\n");
        task3Bridge();
    }

    // =========================================================================
    // ЗАВДАННЯ 1: COMPOSITE (Програма кінофестивалю)
    // =========================================================================
    static void task1Composite() {
        System.out.println("--- Завдання 1: Composite (Програма кінофестивалю) ---");
        
        // Створюємо фільми (Leaf)
        Film film1 = new Film("Дюна", 150);
        Film film2 = new Film("Аватар", 160);
        Film short1 = new Film("Короткометражка 1", 15);
        Film short2 = new Film("Короткометражка 2", 20);
        Film film3 = new Film("Оппенгеймер", 180);

        // Створюємо збірку короткометражок (Composite)
        ProgramGroup shortCollection = new ProgramGroup("Збірка короткометражок");
        shortCollection.add(short1);
        shortCollection.add(short2);

        // Створюємо показ (Composite)
        ProgramGroup screening1 = new ProgramGroup("Показ 1 (Ранковий)");
        screening1.add(film1);
        screening1.add(shortCollection); // Вкладеність

        // Створюємо блок (Composite)
        ProgramGroup block1 = new ProgramGroup("Блок 'Фантастика'");
        block1.add(screening1);
        block1.add(film2);

        ProgramGroup block2 = new ProgramGroup("Блок 'Драма'");
        block2.add(film3);

        // Створюємо фестиваль (Composite)
        ProgramGroup festival = new ProgramGroup("Кінофестиваль 'Кадр 2026'");
        festival.add(block1);
        festival.add(block2);

        System.out.println("[10:00:00] [Фестиваль] Структура програми сформована:");
        festival.display("  ");

        System.out.println("\n[10:01:00] [Розклад] Загальна тривалість ФЕСТИВАЛЮ: " + festival.getDuration() + " хв.");
        System.out.println("[10:01:05] [Розклад] Кількість фільмів на ФЕСТИВАЛІ: " + festival.getFilmCount());
        
        System.out.println("\n[10:02:00] [Розклад] Тривалість БЛОКУ 'Фантастика': " + block1.getDuration() + " хв.");
    }

    // =========================================================================
    // ЗАВДАННЯ 2: FLYWEIGHT (Схема місць)
    // =========================================================================
    static void task2Flyweight() {
        System.out.println("--- Завдання 2: Flyweight (Схема місць) ---");
        
        SeatCategoryFactory factory = new SeatCategoryFactory();
        List<Seat> allSeats = new ArrayList<>();
        
        System.out.println("[11:00:00] [Система] Генерація квитків для 200 сеансів по 150 місць...");
        
        // Імітуємо створення 200 сеансів по 150 місць
        for (int session = 0; session < 200; session++) {
            for (int i = 0; i < 150; i++) {
                String categoryName;
                if (i < 100) categoryName = "Standard";
                else if (i < 130) categoryName = "Comfort";
                else categoryName = "VIP";
                
                // Отримуємо категорію з фабрики (вона створюється лише 1 раз)
                SeatCategory category = factory.getCategory(categoryName);
                allSeats.add(new Seat(1, i + 1, false, category));
            }
        }

        System.out.println("[11:00:05] [Система] Створено об'єктів місць (Seat): " + allSeats.size());
        System.out.println("[11:00:06] [Система] Створено об'єктів категорій (SeatCategory): " + factory.getCategoryCount() + " (має бути 3)");

        // Демонстрація, що два VIP-місця з різних сеансів посилаються на один об'єкт
        Seat vip1 = allSeats.stream().filter(s -> s.getCategory().getName().equals("VIP")).findFirst().get();
        Seat vip2 = allSeats.stream().filter(s -> s.getCategory().getName().equals("VIP")).skip(1).findFirst().get();

        System.out.println("\n[11:01:00] [Перевірка] VIP-місце з сеансу 1: " + vip1);
        System.out.println("[11:01:01] [Перевірка] VIP-місце з сеансу 2: " + vip2);
        System.out.println("[11:01:02] [Перевірка] Чи це один і той самий об'єкт категорії? " + (vip1.getCategory() == vip2.getCategory()));
    }

    // =========================================================================
    // ЗАВДАННЯ 3: BRIDGE (Реклама перед сеансом)
    // =========================================================================
    static void task3Bridge() {
        System.out.println("--- Завдання 3: Bridge (Реклама) ---");
        
        // Створюємо носії (Implementor)
        AdMedium hallScreen = new HallScreen();
        AdMedium lobbyScreens = new LobbyScreens();
        AdMedium mobileApp = new MobileApp();
        AdMedium ledFacade = new LedFacade(); // Додано новий носій (вимога)

        // Створюємо види реклами (Abstraction) і комбінуємо з носіями
        Ad trailerForHall = new TrailerAd("Трейлер 'Дюна 2'", hallScreen);
        Ad trailerForLobby = new TrailerAd("Трейлер 'Дюна 2'", lobbyScreens);
        Ad socialForMobile = new SocialAd("Соціальна реклама: Безпека на дорозі", mobileApp);
        Ad partnerForLed = new PartnerAd("Реклама партнера: Coca-Cola", ledFacade);

        System.out.println("[12:00:00] [Реклама] Показ трейлера на екрані зали:");
        trailerForHall.play();

        System.out.println("\n[12:01:00] [Реклама] Показ трейлера на екранах у фоє:");
        trailerForLobby.play();

        System.out.println("\n[12:02:00] [Реклама] Показ соціальної реклами в мобільному застосунку:");
        socialForMobile.play();

        System.out.println("\n[12:03:00] [Реклама] Показ реклами партнера на світлодіодному фасаді:");
        partnerForLed.play();
    }
}

// =========================================================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 1 (Composite)
// =========================================================================
@SuppressWarnings("all")
interface FestivalComponent {
    int getDuration();
    int getFilmCount();
    void display(String prefix);
}

@SuppressWarnings("all")
class Film implements FestivalComponent {
    private final String title;
    private final int duration;

    public Film(String title, int duration) {
        this.title = title;
        this.duration = duration;
    }

    @Override public int getDuration() { return duration; }
    @Override public int getFilmCount() { return 1; }
    @Override public void display(String prefix) {
        System.out.println(prefix + "- [Фільм] " + title + " (" + duration + " хв)");
    }
}

@SuppressWarnings("all")
class ProgramGroup implements FestivalComponent {
    private final String name;
    private final List<FestivalComponent> children = new ArrayList<>();

    public ProgramGroup(String name) {
        this.name = name;
    }

    public void add(FestivalComponent component) {
        children.add(component);
    }

    @Override
    public int getDuration() {
        int total = 0;
        for (FestivalComponent c : children) total += c.getDuration();
        return total;
    }

    @Override
    public int getFilmCount() {
        int total = 0;
        for (FestivalComponent c : children) total += c.getFilmCount();
        return total;
    }

    @Override
    public void display(String prefix) {
        System.out.println(prefix + "+ [Група] " + name + " (Всього: " + getDuration() + " хв)");
        for (FestivalComponent c : children) {
            c.display(prefix + "   ");
        }
    }
}

// =========================================================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 2 (Flyweight)
// =========================================================================
// Легковаговик: зберігає спільні дані
@SuppressWarnings("all")
class SeatCategory {
    private final String name;
    private final double price;

    public SeatCategory(String name, double price) {
        this.name = name;
        this.price = price;
        System.out.println("  [Factory] Створено НОВУ категорію місць: " + name);
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

// Фабрика легковаговиків з кешем
@SuppressWarnings("all")
class SeatCategoryFactory {
    private final Map<String, SeatCategory> cache = new HashMap<>();

    public SeatCategory getCategory(String name) {
        if (!cache.containsKey(name)) {
            double price = switch (name) {
                case "VIP" -> 300.0;
                case "Comfort" -> 200.0;
                default -> 120.0;
            };
            cache.put(name, new SeatCategory(name, price));
        }
        return cache.get(name);
    }

    public int getCategoryCount() {
        return cache.size();
    }
}

// Звичайний об'єкт, який містить лише унікальні дані та посилання на легковаговика
@SuppressWarnings("all")
class Seat {
    private final int row;
    private final int number;
    private final boolean isSold;
    private final SeatCategory category;

    public Seat(int row, int number, boolean isSold, SeatCategory category) {
        this.row = row;
        this.number = number;
        this.isSold = isSold;
        this.category = category;
    }

    public SeatCategory getCategory() { return category; }

    @Override
    public String toString() {
        return "Ряд " + row + ", Місце " + number + " (Категорія: " + category.getName() + ", " + category.getPrice() + " грн)";
    }
}

// =========================================================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 3 (Bridge)
// =========================================================================
// Implementor (Носій)
@SuppressWarnings("all")
interface AdMedium {
    void showAd(String adContent);
}

@SuppressWarnings("all") class HallScreen implements AdMedium {
    @Override public void showAd(String content) { System.out.println("  [Екран зали] Відтворення: " + content); }
}
@SuppressWarnings("all") class LobbyScreens implements AdMedium {
    @Override public void showAd(String content) { System.out.println("  [Екрани у фоє] Відтворення: " + content); }
}
@SuppressWarnings("all") class MobileApp implements AdMedium {
    @Override public void showAd(String content) { System.out.println("  [Мобільний застосунок] Показ банера: " + content); }
}
@SuppressWarnings("all") class LedFacade implements AdMedium {
    @Override public void showAd(String content) { System.out.println("  [LED-Фасад] Яскрава трансляція: " + content); }
}

// Abstraction (Вид реклами)
@SuppressWarnings("all")
abstract class Ad {
    protected final AdMedium medium;
    protected final String content;

    public Ad(String content, AdMedium medium) {
        this.content = content;
        this.medium = medium;
    }

    public abstract void play();
}

@SuppressWarnings("all") class TrailerAd extends Ad {
    public TrailerAd(String content, AdMedium medium) { super(content, medium); }
    @Override public void play() { medium.showAd("[Трейлер] " + content); }
}

@SuppressWarnings("all") class PartnerAd extends Ad {
    public PartnerAd(String content, AdMedium medium) { super(content, medium); }
    @Override public void play() { medium.showAd("[Реклама партнера] " + content); }
}

@SuppressWarnings("all") class SocialAd extends Ad {
    public SocialAd(String content, AdMedium medium) { super(content, medium); }
    @Override public void play() { medium.showAd("[Соціальна реклама] " + content); }
}
