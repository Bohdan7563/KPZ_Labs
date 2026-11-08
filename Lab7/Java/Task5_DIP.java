
@SuppressWarnings("all")

// ПОГАНО: Клас сервісу сам створював базу даних через new (жорстка прив'язка).
// ДОБРЕ: Ми створили інтерфейси і передаємо їх через конструктор (DIP).

// Абстракції
interface Database { void saveRecord(String id); }
interface FileStorage { void saveEncrypted(String id, String data); }
interface EventBus { void publish(String event, String id); }

// Реалізації
class OracleDatabase implements Database { 
    @Override public void saveRecord(String id) { System.out.println("[Oracle] Запис " + id + " збережено."); } 
}
class LocalEncryptedStorage implements FileStorage { 
    @Override public void saveEncrypted(String id, String data) { System.out.println("[Storage] Картка " + id + " зашифрована."); } 
}
class KafkaEventBus implements EventBus { 
    @Override public void publish(String event, String id) { System.out.println("[Kafka] Подія '" + event + "' для " + id); } 
}

// Високорівневий клас (залежить від абстракцій)
class PatientRecordService {
    private final Database db;
    private final FileStorage storage;
    private final EventBus eventBus;

    // Dependency Injection через конструктор
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

@SuppressWarnings("all")
public class Task5_DIP {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        System.out.println("--- Завдання 5: DIP (Медицина) ---");
        // Точка збірки (Composition Root)
        PatientRecordService service = new PatientRecordService(
            new OracleDatabase(),
            new LocalEncryptedStorage(),
            new KafkaEventBus()
        );
        service.savePatientRecord("MED-001");
    }
}
