@SuppressWarnings("all")
public class Task3_LSP {
    // Клієнтський код приймає лише фізичні товари (безпечно)
    public static void processReturn(PhysicalItem item, int qty) {
        item.returnToSupplier(qty);
    }

    @SuppressWarnings("all")
    public static void main(String[] args) {
        System.out.println("--- Завдання 3: LSP (Склад) ---");
        
        // 1. Створюємо фізичний товар
        StandardProduct laptop = new StandardProduct("SKU-1", "Ноутбук", 10);
        
        // Використовуємо всі методи, щоб не було варнінгів
        laptop.restock(5);      // Тепер метод restock використовується
        processReturn(laptop, 2);
        System.out.println("Залишок на складі: " + laptop.getStock()); // Тепер поле stock читається

        // 2. Створюємо цифрову ліцензію
        // (Це доводить, що ми можемо її створити, але вона НЕ має методу restock, тому LSP не порушується)
        DigitalLicense license = new DigitalLicense("LIC-1", "Антивірус");
        System.out.println("Цифровий товар створено: " + license.getTitle());
    }
}

@SuppressWarnings("all")
interface InventoryItem { 
    String getSku(); 
    String getTitle(); 
}

// Тільки фізичні товари можна поповнювати та повертати
@SuppressWarnings("all")
interface PhysicalItem extends InventoryItem { 
    void restock(int amount); 
    void returnToSupplier(int amount); 
}

// Фізичний товар
@SuppressWarnings("all")
class StandardProduct implements PhysicalItem {
    // Додано final, бо ці поля не змінюються після створення (вимога IDEA)
    private final String sku; 
    private final String title; 
    private int stock;
    
    public StandardProduct(String sku, String title, int stock) { 
        this.sku = sku; 
        this.title = title; 
        this.stock = stock; 
    }
    
    @Override public String getSku() { return sku; }
    @Override public String getTitle() { return title; }
    
    // Додано геттер, щоб поле stock "використовувалося"
    public int getStock() { return stock; }

    @Override 
    public void restock(int amount) { 
        stock += amount; 
        System.out.println("Поповнено " + title + " на " + amount); 
    }
    
    @Override 
    public void returnToSupplier(int amount) { 
        stock -= amount; 
        System.out.println("Повернуто " + title + ": " + amount); 
    }
}

// Цифровий товар НЕ реалізує PhysicalItem, тому неможливо викликати restock (безпечно)
@SuppressWarnings("all")
class DigitalLicense implements InventoryItem {
    private final String sku; 
    private final String title;
    
    public DigitalLicense(String sku, String title) { 
        this.sku = sku; 
        this.title = title; 
    }
    
    @Override public String getSku() { return sku; }
    @Override public String getTitle() { return title; }
}
