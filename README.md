# Лабораторні роботи з КПЗ

Цей репозиторій містить виконані лабораторні роботи.

## Лабораторна робота №7 (Варіант 4, Java)
Шлях до коду: `Lab7/Java/`

### Завдання 6. Прагматичний аналіз (DRY, KISS, YAGNI)

**1. Аналіз DRY (Don't Repeat Yourself):**
У Завданні 1 (SRP) ми ліквідували дублювання бізнес-правил. Логіку розрахунку ціни винесено у `PriceCalculator`, а перевірку дат — у `BookingValidator`. Тепер зміна тарифів вимагає редагування лише одного класу (єдине джерело правди - SSOT).

**2. Аналіз KISS (Keep It Simple, Stupid):**
У Завданні 2 (OCP) ми позбулися великого `switch-case` для розрахунку митних зборів. Замість купи умов створено інтерфейс `TaxStrategy` та окремі класи для кожної категорії товару. Це зменшило складність коду.

**3. Аналіз YAGNI (You Aren't Gonna Need It):**
У Завданні 4 (ISP) ми розбили "жирний" інтерфейс хмарного сховища на дрібні рольові. Створено виключно ті методи, які реально використовуються конкретними класами. Відсутні заглушки типу `NotImplementedException`.

**4. Крос-огляд:**
Код Java-частини проаналізовано на предмет дотримання принципів SOLID. Всі 5 завдань успішно пройшли рефакторинг.

---

## Лабораторна робота №8 (Варіант 4, Java)
Шлях до коду: `Lab8/Java/`

### Завдання 5. Метричний аналіз (Low Coupling + High Cohesion)

**1. Coupling Between Objects (CBO):**
* **До рефакторингу (Legacy):** Клас `LegacyLibraryProcessor` мав CBO = 5 (залежав від Reader, Book, LoanRecord, Scanner, System). Він знав про всі деталі.
* **Після рефакторингу:** Клас `LibraryController` має CBO = 3 (LoanRecord, LoanRepository, NotificationService). Клас `LoanRecord` має CBO = 2 (Reader, Book). Це відповідає нормативу GRASP (CBO ≤ 4).

**2. Lack of Cohesion of Methods (LCOM4):**
* **До рефакторингу:** Клас `LegacyLibraryProcessor` мав LCOM4 = 3, оскільки його методи працювали з різними незалежними наборами даних (зчитування з консолі, розрахунок фінансів, відправка email).
* **Після рефакторингу:** Клас `LoanRecord` має LCOM4 = 1. Всі його методи (`createLoan`, `processIssuance`) працюють з єдиним станом (застава, комісія, статус). Клас став високозгуртованим (High Cohesion).

**3. Аналіз GRASP:**
* **Information Expert:** Розрахунок застави перенесено в `LoanRecord`, який володіє книгою.
* **Creator:** Використано статичну фабрику `LoanRecord.createLoan()`.
* **Pure Fabrication / Indirection:** Створено `LoanRepository` та `NotificationService` для розділення відповідальності.
* **Polymorphism / Protected Variations:** Замінено `if-else` на `BookCategory`.
