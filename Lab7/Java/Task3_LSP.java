@SuppressWarnings("all")

/* 
 * ПРИНЦИП LSP (Liskov Substitution Principle): Нащадки повинні поводитися так само, як і їхні батьки.
 * Раніше "Цифрова ліцензія" наслідувала "Фізичний товар", але не могла виконувати метод restock (поповнення) 
 * і викидала помилку. Це порушувало принцип. 
 * Ми розділили інтерфейси. Тепер Цифрова ліцензія взагалі не має методу restock, тому помилки неможливі.
 */

// Базовий інтерфейс для ВСІХ товарів (і фізичних, і цифрових)
interface InventoryItem { 
    String getSku(); 
    String getTitle(); 
}

// Додатковий інтерфейс ТІЛЬКИ для фізичних товарів, які можна поповнювати
interface PhysicalItem extends InventoryItem { 
    void restock(int amount); 
    void returnToSupplier(int amount); 
}

// Фізичний товар реалізує обидва інтерфейси
@SuppressWarnings("all")
class StandardProduct implements PhysicalItem {
    private final String sku; // final означає, що поле не можна змінити після створення
    private final String title; 
    private int stock;
    
    public StandardProduct(String sku, String title, int stock) { 
        this.sku = sku; 
        this.title = title; 
        this.stock = stock; 
    }
    
    @Override public String getSku() { return sku; }
    @Override public String getTitle() { return title; }
    
    public int getStock() { return stock; } // Геттер для отримання залишку

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

// Цифровий товар реалізує ТІЛЬКИ базовий інтерфейс (він не вміє поповнюватися на складі)
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

public class Task3_LSP {
    // Клієнтський код приймає лише ті об'єкти, які ТОЧНО вміють робити returnToSupplier
    public static void processReturn(PhysicalItem item, int qty) {
        item.returnToSupplier(qty);
    }

    @SuppressWarnings("all")
    public static void main(String[] args) {
        System.out.println("--- Завдання 3: LSP (Склад) ---");
        
        StandardProduct laptop = new StandardProduct("SKU-1", "Ноутбук", 10);
        laptop.restock(5); // Метод працює
        processReturn(laptop, 2);
        System.out.println("Залишок на складі: " + laptop.getStock());

        // Створюємо цифровий товар. Він не має методу restock, тому ми навіть не зможемо викликати його випадково.
        DigitalLicense license = new DigitalLicense("LIC-1", "Антивірус");
        System.out.println("Цифровий товар створено: " + license.getTitle());
    }
}
