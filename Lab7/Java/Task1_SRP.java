import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("all")
public class Task1_SRP {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        System.out.println("--- Завдання 1: SRP (Готель) ---");
        new HotelBookingService().createBooking("Іван", "+380991234567", "Suite", LocalDate.now(), LocalDate.now().plusDays(2), true, List.of("Spa"));
    }
}

/* 
 * ПРИНЦИП SRP (Single Responsibility Principle): Клас повинен мати лише одну причину для зміни.
 * Раніше весь код був в одному великому класі. Ми розбили його на 5 маленьких, 
 * де кожен відповідає ТІЛЬКИ за свою частину роботи.
 */

// 1. Цей клас відповідає ТІЛЬКИ за перевірку правильності введених даних (валідацію)
@SuppressWarnings("all")
class BookingValidator {
    public void validate(String name, String phone, LocalDate in, LocalDate out) {
        // Перевіряємо, чи не порожнє ім'я або телефон
        if (name == null || name.isBlank() || phone == null || phone.isBlank()) 
            throw new IllegalArgumentException("Некоректні дані гостя");
        // Перевіряємо, чи дата заїзду не в минулому, і чи виїзд пізніше заїзду
        if (in.isBefore(LocalDate.now()) || !out.isAfter(in)) 
            throw new IllegalArgumentException("Некоректні дати бронювання");
    }
}

// 2. Цей клас відповідає ТІЛЬКИ за математичні розрахунки вартості
@SuppressWarnings("all")
class PriceCalculator {
    public double calculate(String roomType, int nights, boolean isLoyalty, List<String> services) {
        // Визначаємо базову ціну залежно від типу номера
        double basePrice = switch (roomType) {
            case "Standard" -> 1200.0;
            case "Deluxe" -> 2500.0;
            case "Suite" -> 4500.0;
            default -> throw new IllegalArgumentException("Невідомий тип номера");
        };
        double total = basePrice * nights; // Множимо ціну на кількість ночей
        
        // Додаємо вартість додаткових послуг
        if (services != null) {
            for (String s : services) {
                if (s.equals("Breakfast")) total += 300.0 * nights; // Сніданок за кожну ніч
                if (s.equals("Spa")) total += 800.0; // Спа-процедури фіксовано
            }
        }
        // Якщо клієнт постійний, робимо знижку 10% (множимо на 0.90)
        if (isLoyalty) total *= 0.90; 
        return total;
    }
}

// 3. Цей клас відповідає ТІЛЬКИ за створення HTML-тексту ваучера
@SuppressWarnings("all")
class VoucherGenerator {
    public String generate(String name, double total) {
        return "<html><body><h2>Ваучер</h2><p>Гість: " + name + "</p><h3>Сума: " + total + " грн</h3></body></html>";
    }
}

// 4. Цей клас відповідає ТІЛЬКИ за збереження даних (імітація бази даних)
@SuppressWarnings("all")
class BookingRepository {
    public void save(String guestName, double total) {
        System.out.println("[FILE DB] Бронювання для " + guestName + " на суму " + total + " збережено.");
    }
}

// 5. Цей клас відповідає ТІЛЬКИ за відправку повідомлень
@SuppressWarnings("all")
class MessageSender {
    public void sendEmail(String email, String htmlVoucher) {
        System.out.println("[SMTP] Лист з ваучером надіслано на " + email);
        System.out.println("Текст ваучера: " + htmlVoucher); // Використовуємо змінну, щоб не було помилок
    }
}

// 6. ГОЛОВНИЙ КЛАС (Оркестратор). Він не робить роботу сам, він просто каже іншим класам, що робити.
@SuppressWarnings("all")
class HotelBookingService {
    // Створюємо об'єкти всіх наших класів
    private final BookingValidator validator = new BookingValidator();
    private final PriceCalculator calculator = new PriceCalculator();
    private final VoucherGenerator generator = new VoucherGenerator();
    private final BookingRepository repository = new BookingRepository();
    private final MessageSender sender = new MessageSender();

    public void createBooking(String name, String phone, String roomType, LocalDate in, LocalDate out, boolean isLoyalty, List<String> services) {
        validator.validate(name, phone, in, out); // 1. Перевіряємо дані
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(in, out); // Рахуємо ночі
        double total = calculator.calculate(roomType, nights, isLoyalty, services); // 2. Рахуємо ціну
        
        String voucher = generator.generate(name, total); // 3. Генеруємо текст ваучера
        repository.save(name, total); // 4. Зберігаємо в базу
        sender.sendEmail("client@mail.com", voucher); // 5. Відправляємо лист
    }
}
