import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ЛАБОРАТОРНА РОБОТА №9: ПОРОДЖУВАЛЬНІ ШАБЛОНИ ===\n");
        task1Singleton();
        System.out.println("\n--------------------------------------------------\n");
        task2Prototype();
        System.out.println("\n--------------------------------------------------\n");
        task3ObjectPool();
    }

    // =========================================================================
    // ЗАВДАННЯ 1: SINGLETON (Система продажу квитків)
    // Проблема: Каси та сайт мають бачити однаковий стан проданих місць. Якщо
    // створити два екземпляри системи, вони продадуть одне й те саме місце двічі.
    // =========================================================================
    static void task1Singleton() {
        System.out.println("--- Завдання 1: Singleton (Система продажу квитків) ---");
        
        // Імітуємо касу 1 та касу 2. Вони звертаються до системи.
        TicketSalesSystem terminal1 = TicketSalesSystem.getInstance();
        TicketSalesSystem terminal2 = TicketSalesSystem.getInstance();

        System.out.println("[10:00:00] [Каса 1] Спроба продати квиток на місце 12A: " + terminal1.buyTicket("Session-1", "12A"));
        System.out.println("[10:00:05] [Каса 2] Спроба продати квиток на місце 12A: " + terminal2.buyTicket("Session-1", "12A"));
        
        System.out.println("Чи це один і той самий екземпляр? " + (terminal1 == terminal2));
    }

    // =========================================================================
    // ЗАВДАННЯ 2: PROTOTYPE (Клонування сеансу)
    // Проблема: Створення сеансу вимагає підвантаження реклами, налаштувань зали 
    // тощо. Це довго. Треба мати шаблон і робити його копії (клонування).
    // =========================================================================
    static void task2Prototype() {
        System.out.println("--- Завдання 2: Prototype (Клонування сеансу) ---");
        
        System.out.println("[11:00:00] [Система] Завантаження еталонного сеансу...");
        AdBlock adBlock = new AdBlock("Реклама Кока-Кола");
        CinemaSession template = new CinemaSession("Аватар 2", "Зал 1 (3D)", "10:00", adBlock);

        System.out.println("[11:00:02] [Система] Створення сеансу на 14:00 з еталона (швидке клонування)...");
        CinemaSession clonedSession = template.clone();
        clonedSession.dateTime = "14:00";

        System.out.println("[11:00:03] [Система] Зміна реклами тільки в новому сеансі...");
        clonedSession.adBlock.content = "Реклама Pepsi";

        System.out.println("Оригінальний сеанс (10:00): " + template);
        System.out.println("Клонований сеанс (14:00): " + clonedSession);
    }

    // =========================================================================
    // ЗАВДАННЯ 3: OBJECT POOL (Пул 3D-окулярів)
    // Проблема: У кінотеатрі є обмежена кількість окулярів (наприклад, 3). 
    // Видавати нові кожного разу дорого. Треба видавати, а потім повертати в пул.
    // =========================================================================
    static void task3ObjectPool() {
        System.out.println("--- Завдання 3: Object Pool (Пул 3D-окулярів) ---");
        
        GlassesPool pool = new GlassesPool(3); // Всього 3 окуляри в наявності

        System.out.println("[12:00:00] [Глядач 1] Бере окуляри: " + pool.acquire());
        System.out.println("[12:00:10] [Глядач 2] Бере окуляри: " + pool.acquire());
        System.out.println("[12:00:20] [Глядач 3] Бере окуляри: " + pool.acquire());
        
        // Намагаємось взяти 4-ті окуляри
        System.out.println("[12:00:30] [Глядач 4] Бере окуляри: " + pool.acquire());

        System.out.println("[12:30:00] [Глядач 1] Повертає окуляри...");
        pool.release(new Glasses3D(1));

        System.out.println("[12:30:05] [Глядач 4] Бере окуляри (ті, що повернули): " + pool.acquire());
    }
}

// ==========================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 1
// ==========================================
class TicketSalesSystem {
    private static TicketSalesSystem instance;
    private final Set<String> soldSeats;

    private TicketSalesSystem() {
        soldSeats = new HashSet<>();
        System.out.println("[09:59:59] [Система] Центральна система продажу квитків ініціалізована.");
    }

    public static TicketSalesSystem getInstance() {
        if (instance == null) {
            instance = new TicketSalesSystem();
        }
        return instance;
    }

    public boolean buyTicket(String session, String seat) {
        String key = session + "-" + seat;
        if (soldSeats.contains(key)) return false;
        soldSeats.add(key);
        return true;
    }
}

// ==========================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 2
// ==========================================
class AdBlock implements Cloneable {
    public String content;
    public AdBlock(String content) { this.content = content; }
    
    @Override
    public AdBlock clone() {
        try { return (AdBlock) super.clone(); } 
        catch (CloneNotSupportedException e) { return null; }
    }
}

class CinemaSession implements Cloneable {
    public String movieTitle;
    public String hall;
    public String dateTime;
    public AdBlock adBlock;

    public CinemaSession(String movieTitle, String hall, String dateTime, AdBlock adBlock) {
        this.movieTitle = movieTitle;
        this.hall = hall;
        this.dateTime = dateTime;
        this.adBlock = adBlock;
    }

    @Override
    public CinemaSession clone() {
        try {
            CinemaSession cloned = (CinemaSession) super.clone();
            // Глибоке клонування (deep copy), щоб зміна реклами не ламала оригінал
            cloned.adBlock = this.adBlock.clone(); 
            return cloned;
        } catch (CloneNotSupportedException e) { return null; }
    }

    @Override
    public String toString() {
        return "Сеанс [" + movieTitle + " о " + dateTime + ", Реклама: " + adBlock.content + "]";
    }
}

// ==========================================
// КЛАСИ ДЛЯ ЗАВДАННЯ 3
// ==========================================
class Glasses3D {
    public int id;
    public boolean isClean;

    public Glasses3D(int id) { this.id = id; this.isClean = true; }
    public void reset() { this.isClean = true; }
    @Override public String toString() { return "Окуляри №" + id; }
}

class GlassesPool {
    private final List<Glasses3D> available = new ArrayList<>();
    private final List<Glasses3D> inUse = new ArrayList<>();

    public GlassesPool(int initialSize) {
        for (int i = 1; i <= initialSize; i++) {
            available.add(new Glasses3D(i));
        }
    }

    public Glasses3D acquire() {
        if (available.isEmpty()) {
            System.out.println("[POOL] Немає вільних окуляpів! Всі в роботі.");
            return null;
        }
        Glasses3D glasses = available.removeFirst();
        inUse.add(glasses);
        return glasses;
    }

    public void release(Glasses3D glasses) {
        if (glasses != null && inUse.contains(glasses)) {
            glasses.reset(); // Дезінфекція/очищення
            inUse.remove(glasses);
            available.add(glasses);
        }
    }
}
