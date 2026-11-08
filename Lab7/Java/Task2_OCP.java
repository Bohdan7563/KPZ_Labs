
@SuppressWarnings("all")

// ПОГАНО: Був великий switch-case. Якщо додати новий товар, треба лізти в цей клас і змінювати його.
// ДОБРЕ: Ми створили інтерфейс. Тепер для нового товару ми просто створюємо новий клас, не змінюючи старий код (OCP).

// Інтерфейс стратегії (абстракція)
interface TaxStrategy {
    double calculate(double value, double weight);
}

// Реалізації для кожної категорії
class ElectronicsTax implements TaxStrategy {
    @Override public double calculate(double value, double weight) { return value * 0.05; }
}
class FoodTax implements TaxStrategy {
    @Override public double calculate(double value, double weight) { 
        double duty = (value * 0.10) + (weight * 0.50); 
        double vat = (value + duty) * 0.07; 
        return duty + vat; 
    }
}
class LuxuryTax implements TaxStrategy {
    @Override public double calculate(double value, double weight) { 
        double duty = value * 0.25; 
        double excise = value * 0.15; 
        double vat = (value + duty + excise) * 0.20; 
        return duty + excise + vat; 
    }
}

// Контекст (йому байдуже, який саме податок, він просто викликає метод calculate)
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
        System.out.print("Електроніка: "); p.process(1000, 5, new ElectronicsTax());
        System.out.print("Продукти: "); p.process(500, 10, new FoodTax());
        System.out.print("Розкіш: "); p.process(2000, 2, new LuxuryTax());
    }
}
