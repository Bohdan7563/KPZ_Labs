@SuppressWarnings("all")

/* 
 * ПРИНЦИП DIP (Dependency Inversion Principle): Модулі високого рівня не повинні залежати від модулів низького рівня. 
 * Вони мають залежати від абстракцій.
 * Раніше клас сам створював базу через new OracleDatabase(). 
 * Тепер ми створили інтерфейси, і передаємо конкретні реалізації через конструктор (Dependency Injection).
 */

// Абстракції (правила, яким мають відповідати бази даних, сховища та шини подій)
interface Database { void saveRecord(String id); }
interface FileStorage { void saveEncrypted(String id, String data); }
interface EventBus { void publish(String event, String id); }

// Конкретні реалізації (можна легко замінити на MySQL, Amazon S3 або RabbitMQ)
class OracleDatabase implements Database { 
    @Override public void saveRecord(String id) { System.out.println("[Oracle] Запис " + id + " збережено."); } 
}
class LocalEncryptedStorage implements FileStorage { 
    @Override public void saveEncrypted(String id, String data) { System.out.println("[Storage] Картка " + id + " зашифрована."); } 
}
class KafkaEventBus implements EventBus { 
    @Override public void publish(String event, String id) { System.out.println("[Kafka] Подія '" + event + "' для " + id); } 
}

// Високорівневий клас
class PatientRecordService {
    // Він залежить від абстракцій (інтерфейсів), а не від конкретних класів
    private final Database db;
    private final FileStorage storage;
    private final EventBus eventBus;

    // Впровадження залежностей через конструктор (Constructor Injection)
    public PatientRecordService(Database db, FileStorage storage, EventBus eventBus) {
        this.db = db;
        this.storage = storage;
        this.eventBus = eventBus;
    }

    public void savePatientRecord(String recordId) {
        db.saveRecord(recordId);
        storage.saveEncrypted(recordId, "Діагноз: Грип");
        eventBus.publish("PATIENT_UPDATED", recordId);
    }
}

public class Task5_DIP {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        System.out.println("--- Завдання 5: DIP (Медицина) ---");
        
        // Точка збірки (Composition Root): тут ми вирішуємо, які саме реалізації передати.
        PatientRecordService service = new PatientRecordService(
            new OracleDatabase(),
            new LocalEncryptedStorage(),
            new KafkaEventBus()
        );
        service.savePatientRecord("MED-001");
    }
}
