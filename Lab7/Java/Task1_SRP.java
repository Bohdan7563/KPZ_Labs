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

// 1. Відповідальність: Валідація вхідних даних
@SuppressWarnings("all")
class BookingValidator {
    public void validate(String name, String phone, LocalDate in, LocalDate out) {
        if (name == null || name.isBlank() || phone == null || phone.isBlank()) 
            throw new IllegalArgumentException("Некоректні дані гостя");
        if (in.isBefore(LocalDate.now()) || !out.isAfter(in)) 
            throw new IllegalArgumentException("Некоректні дати бронювання");
    }
}

// 2. Відповідальність: Розрахунок вартості (бізнес-логіка)
@SuppressWarnings("all")
class PriceCalculator {
    public double calculate(String roomType, int nights, boolean isLoyalty, List<String> services) {
        double basePrice = switch (roomType) {
            case "Standard" -> 1200.0;
            case "Deluxe" -> 2500.0;
            case "Suite" -> 4500.0;
            default -> throw new IllegalArgumentException("Невідомий тип номера");
        };
        double total = basePrice * nights;
        
        if (services != null) {
            for (String s : services) {
                if (s.equals("Breakfast")) total += 300.0 * nights;
                if (s.equals("Spa")) total += 800.0;
            }
        }
        if (isLoyalty) total *= 0.90; 
        return total;
    }
}

// 3. Відповідальність: Генерація HTML-ваучера
@SuppressWarnings("all")
class VoucherGenerator {
    public String generate(String name, double total) {
        return "<html><body><h2>Ваучер</h2><p>Гість: " + name + "</p><h3>Сума: " + total + " грн</h3></body></html>";
    }
}

// 4. Відповідальність: Збереження у базу даних (файл)
@SuppressWarnings("all")
class BookingRepository {
    public void save(String guestName, double total) {
        System.out.println("[FILE DB] Бронювання для " + guestName + " на суму " + total + " збережено.");
    }
}

// 5. Відповідальність: Відправка повідомлень
@SuppressWarnings("all")
class MessageSender {
    // ВИПРАВЛЕНО: Тепер ми реально використовуємо htmlVoucher у виводі, тому варнінгу не буде!
    public void sendEmail(String email, String htmlVoucher) {
        System.out.println("[SMTP] Лист з ваучером надіслано на " + email);
        System.out.println("Текст ваучера: " + htmlVoucher);
    }
}

// 6. Оркестратор
@SuppressWarnings("all")
class HotelBookingService {
    private final BookingValidator validator = new BookingValidator();
    private final PriceCalculator calculator = new PriceCalculator();
    private final VoucherGenerator generator = new VoucherGenerator();
    private final BookingRepository repository = new BookingRepository();
    private final MessageSender sender = new MessageSender();

    public void createBooking(String name, String phone, String roomType, LocalDate in, LocalDate out, boolean isLoyalty, List<String> services) {
        validator.validate(name, phone, in, out);
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(in, out);
        double total = calculator.calculate(roomType, nights, isLoyalty, services);
        
        String voucher = generator.generate(name, total);
        repository.save(name, total);
        sender.sendEmail("client@mail.com", voucher);
    }
}
