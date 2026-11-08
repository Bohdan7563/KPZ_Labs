@SuppressWarnings("all")

/* 
 * ПРИНЦИП OCP (Open/Closed Principle): Класи мають бути відкриті для розширення, але закриті для зміни.
 * Раніше був великий switch-case. Якби ми додали новий товар, нам довелося б лізти в цей код і переписувати його.
 * Тепер ми створюємо інтерфейс. Для нового товару ми просто створюємо новий клас, не змінюючи старий код.
 */

// Інтерфейс (абстракція) — це "правило", яке зобов'язує всіх мати метод calculate
interface TaxStrategy {
    double calculate(double value, double weight);
}

// Клас для Електроніки
class ElectronicsTax implements TaxStrategy {
    @Override 
    public double calculate(double value, double weight) { return value * 0.05; } // 5% мита
}

// Клас для Продуктів
class FoodTax implements TaxStrategy {
    @Override 
    public double calculate(double value, double weight) { 
        double duty = (value * 0.10) + (weight * 0.50); // 10% + 0.5 за кг
        double vat = (value + duty) * 0.07; // ПДВ 7%
        return duty + vat; 
    }
}

// Клас для Розкоші
class LuxuryTax implements TaxStrategy {
    @Override 
    public double calculate(double value, double weight) { 
        double duty = value * 0.25; 
        double excise = value * 0.15; 
        double vat = (value + duty + excise) * 0.20; 
        return duty + excise + vat; 
    }
}

// Контекст. Йому байдуже, який саме податок. Він просто приймає стратегію і викликає її метод.
class TaxProcessor {
    public void process(double value, double weight, TaxStrategy strategy) {
        double tax = strategy.calculate(value, weight);
        System.out.println("Сума мита та податків: $" + String.format("%.2f", tax));
    }
}

@SuppressWarnings("all")
public class Task2_OCP {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        System.out.println("--- Завдання 2: OCP (Митниця) ---");
        TaxProcessor p = new TaxProcessor();
        // Передаємо різні стратегії в один і той самий процесор
        System.out.print("Електроніка: "); p.process(1000, 5, new ElectronicsTax());
        System.out.print("Продукти: "); p.process(500, 10, new FoodTax());
        System.out.print("Розкіш: "); p.process(2000, 2, new LuxuryTax());
    }
}
