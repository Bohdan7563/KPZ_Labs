@SuppressWarnings("all")
public class CinemaMain13 {
    public static void main(String[] args) {
        System.out.println("=== ЛАБОРАТОРНА РОБОТА №13 (Behavioral Patterns) ===\n");
        task1Command();
        System.out.println("\n--------------------------------------------------\n");
        task2ChainOfResponsibility();
    }

    // =========================================================================
    // ЗАВДАННЯ 1: COMMAND (Архітектурний патерн)
    // =========================================================================
    static void task1Command() {
        System.out.println("--- Завдання 1: Command ---");
        
        // 1. Receiver (Отримувач): Інкапсулює бізнес-логіку. 
        // Він повністю ізольований і нічого не знає про існування команд чи інвокерів.
        CinemaHall receiver = new CinemaHall();
        
        // 2. Invoker (Ініціатор): Викликає команди. 
        // Він відповідає лише за зберігання історії (для Undo) та виклик execute().
        Operator invoker = new Operator();

        System.out.println("\n[Сценарій 1] Інкапсуляція запиту в об'єкт та Undo:");
        // Створюємо об'єкт команди. Параметризований отримувачем та новим значенням.
        Command volumeCmd = new VolumeCommand(receiver, 80);
        
        // Invoker викликає команду. Він не знає, що саме вона робить.
        invoker.executeCommand(volumeCmd);
        
        // Виклик Undo. Invoker дістає команду з історії і викликає її метод undo().
        invoker.undoLast();

        System.out.println("\n[Сценарій 2] Патерн Composite для макрокоманд:");
        // MacroCommand реалізує той самий інтерфейс Command, але всередині містить колекцію інших команд.
        // Це дозволяє будувати деревоподібні структури викликів.
        MacroCommand macroStart = new MacroCommand();
        macroStart.add(new LightCommand(receiver, false));
        macroStart.add(new VolumeCommand(receiver, 100));
        
        invoker.executeCommand(macroStart);
        
        // Скасування макрокоманди викликає undo() для всіх вкладених команд у зворотному порядку.
        invoker.undoLast();

        System.out.println("\n--- Історія транзакцій (Command Log) ---");
        invoker.printHistory();
    }

    // =========================================================================
    // ЗАВДАННЯ 2: CHAIN OF RESPONSIBILITY (Архітектурний патерн)
    // =========================================================================
    static void task2ChainOfResponsibility() {
        System.out.println("--- Завдання 2: Chain of Responsibility ---");
        
        // 1. Формування ланцюжка обробників (Handlers).
        // Зв'язок між обробниками реалізовано через посилання на наступний елемент (Linked List).
        DiscountHandler promo = new PromoCodeHandler("STUDENT10");
        DiscountHandler student = new StudentHandler();
        DiscountHandler pensioner = new PensionerHandler();

        // Динамічна конфігурація ланцюжка (Decoupling).
        // Відправник запиту не знає, який саме обробник його обробить.
        promo.setNext(student);
        student.setNext(pensioner);

        System.out.println("\n[Тест 1] Контекст: Студент без промокоду.");
        // Контекст (Request), який передається по ланцюжку.
        Order order1 = new Order(1000.0, false, true, false);
        promo.handle(order1); // Точка входу в ланцюжок
        System.out.println("  -> Результат: ціна " + order1.price + " грн. Прапорець застосування: " + order1.isDiscountApplied);

        System.out.println("\n[Тест 2] Контекст: Промокод (Блокування ланцюжка).");
        Order order2 = new Order(1000.0, true, true, false);
        promo.handle(order2);
        // PromoCodeHandler обробить запит і викличе return, зупинивши подальше просування.
        System.out.println("  -> Результат: ціна " + order2.price + " грн. Прапорець застосування: " + order2.isDiscountApplied);

        System.out.println("\n[Тест 3] Контекст: Пенсіонер (Обробка в кінці ланцюжка).");
        Order order3 = new Order(1000.0, false, false, true);
        promo.handle(order3); 
        // Запит пройде через PromoCodeHandler (не обробить), StudentHandler (не обробить), і дійде до PensionerHandler.
        System.out.println("  -> Результат: ціна " + order3.price + " грн. Прапорець застосування: " + order3.isDiscountApplied);
    }
}

// =========================================================================
// АРХІТЕКТУРА COMMAND PATTERN
// =========================================================================

// Інтерфейс Command: Контракт, що зобов'язує всі команди мати методи execute() та undo().
// Це дозволяє інвокеру працювати з будь-якою командою поліморфно.
@SuppressWarnings("all")
interface Command {
    void execute();
    void undo();
}

// Receiver (Отримувач): Містить реальну бізнес-логіку. 
// Ізольований від інтерфейсу Command (Low Coupling).
@SuppressWarnings("all")
class CinemaHall {
    private boolean isLightOn = true;
    private int volume = 50;

    public void setLight(boolean state) {
        this.isLightOn = state;
        System.out.println("  [Receiver] Змінено стан світла: " + state);
    }

    public void setVolume(int level) {
        this.volume = level;
        System.out.println("  [Receiver] Змінено рівень гучності: " + level);
    }
    public int getVolume() { return volume; }
    public boolean isLightOn() { return isLightOn; }
}

// ConcreteCommand: Зв'язує Receiver та параметри запиту. 
// Зберігає стан (oldVolume) для реалізації патерну Memento (Undo).
@SuppressWarnings("all")
class VolumeCommand implements Command {
    private final CinemaHall receiver;
    private final int newVolume;
    private int oldVolume; // Стан для відкату (Undo)

    public VolumeCommand(CinemaHall receiver, int newVolume) {
        this.receiver = receiver;
        this.newVolume = newVolume;
    }

    @Override
    public void execute() {
        oldVolume = receiver.getVolume(); // Збереження поточного стану
        receiver.setVolume(newVolume);    // Делегування логіки отримувачу
    }

    @Override
    public void undo() {
        receiver.setVolume(oldVolume);    // Відновлення стану
        System.out.println("  [Command Undo] Гучність відкочено до " + oldVolume);
    }
}

@SuppressWarnings("all")
class LightCommand implements Command {
    private final CinemaHall receiver;
    private final boolean newState;
    private boolean oldState;

    public LightCommand(CinemaHall receiver, boolean newState) {
        this.receiver = receiver;
        this.newState = newState;
    }

    @Override
    public void execute() {
        oldState = receiver.isLightOn();
        receiver.setLight(newState);
    }

    @Override
    public void undo() {
        receiver.setLight(oldState);
    }
}

// MacroCommand (Composite): Реалізує інтерфейс Command, але містить колекцію підкоманд.
// Делегує виконання всім елементам колекції. Undo викликається у зворотному порядку (LIFO).
@SuppressWarnings("all")
class MacroCommand implements Command {
    private final java.util.List<Command> commands = new java.util.ArrayList<>();

    public void add(Command command) { commands.add(command); }

    @Override
    public void execute() {
        System.out.println("  [MacroCommand] Виконання пакету команд: " + commands.size());
        for (Command c : commands) c.execute();
    }

    @Override
    public void undo() {
        System.out.println("  [MacroCommand] Скасування пакету команд...");
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
}

// Invoker (Ініціатор): Керує командами. 
// Не знає деталей реалізації execute(). Відповідає за транзакційність (збереження історії).
@SuppressWarnings("all")
class Operator {
    private final java.util.List<Command> history = new java.util.ArrayList<>();

    public void executeCommand(Command command) {
        command.execute();
        history.add(command); // Збереження транзакції в історії
    }

    public void undoLast() {
        if (!history.isEmpty()) {
            Command lastCommand = history.remove(history.size() - 1);
            lastCommand.undo(); // Поліморфний виклик відкату
        }
    }

    public void printHistory() {
        System.out.println("Кількість транзакцій в історії: " + history.size());
        for (Command c : history) System.out.println(" - " + c.getClass().getSimpleName());
    }
}


// =========================================================================
// АРХІТЕКТУРА CHAIN OF RESPONSIBILITY PATTERN
// =========================================================================

// Контекст (Request): Об'єкт, який передається по ланцюжку і модифікується обробниками.
@SuppressWarnings("all")
class Order {
    public double price;
    public boolean isDiscountApplied = false;
    public boolean hasPromoCode;
    public boolean isStudent;
    public boolean isPensioner;

    public Order(double price, boolean hasPromoCode, boolean isStudent, boolean isPensioner) {
        this.price = price;
        this.hasPromoCode = hasPromoCode;
        this.isStudent = isStudent;
        this.isPensioner = isPensioner;
    }
}

// Handler (Обробник): Абстрактний клас. 
// Містить посилання на наступний елемент (next) для побудови ланцюжка (Linked List).
@SuppressWarnings("all")
abstract class DiscountHandler {
    protected DiscountHandler next;

    // Метод для динамічного конфігурування ланцюжка.
    public void setNext(DiscountHandler next) {
        this.next = next;
    }

    // Абстрактний метод обробки запиту.
    public abstract void handle(Order request);

    // Делегування запиту наступному обробнику.
    protected void passToNext(Order request) {
        if (next != null) next.handle(request);
        else System.out.println("  [Chain] Кінець ланцюжка. Обробників більше немає.");
    }
}

// ConcreteHandler 1: Обробляє запит, якщо є промокод. 
// Перериває ланцюжок (return), щоб уникнути накладання знижок.
@SuppressWarnings("all")
class PromoCodeHandler extends DiscountHandler {
    private final String validCode;

    public PromoCodeHandler(String validCode) { this.validCode = validCode; }

    @Override
    public void handle(Order request) {
        System.out.println("  [Handler] PromoCodeHandler перевіряє контекст...");
        if (request.hasPromoCode && !request.isDiscountApplied) {
            System.out.println("    -> Застосовано знижку 10%. Термінація ланцюжка (return).");
            request.price *= 0.90;
            request.isDiscountApplied = true;
            return; // Термінація ланцюжка
        }
        passToNext(request); // Передача далі
    }
}

// ConcreteHandler 2: Обробляє студентів.
@SuppressWarnings("all")
class StudentHandler extends DiscountHandler {
    @Override
    public void handle(Order request) {
        System.out.println("  [Handler] StudentHandler перевіряє контекст...");
        if (request.isStudent && !request.isDiscountApplied) {
            System.out.println("    -> Застосовано знижку 15%.");
            request.price *= 0.85;
            request.isDiscountApplied = true;
            return;
        }
        passToNext(request);
    }
}

// ConcreteHandler 3: Обробляє пенсіонерів.
@SuppressWarnings("all")
class PensionerHandler extends DiscountHandler {
    @Override
    public void handle(Order request) {
        System.out.println("  [Handler] PensionerHandler перевіряє контекст...");
        if (request.isPensioner && !request.isDiscountApplied) {
            System.out.println("    -> Застосовано знижку 20%.");
            request.price *= 0.80;
            request.isDiscountApplied = true;
            return;
        }
        passToNext(request);
    }
}
