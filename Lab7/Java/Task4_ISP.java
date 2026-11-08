@SuppressWarnings("all")

/* 
 * ПРИНЦИП ISP (Interface Segregation Principle): Клієнти не повинні залежати від методів, які вони не використовують.
 * Раніше був один "жирний" інтерфейс, який змушував усі класи реалізовувати всі методи.
 * Ми розбили його на 4 маленькі рольові інтерфейси. Тепер кожен клас бере лише те, що йому треба.
 */

// Маленькі інтерфейси під конкретні задачі
interface FileDownloadable { byte[] downloadFile(String name); }
interface FileUploadable { void uploadFile(String name, byte[] data); }
interface Shareable { String generatePublicShareLink(String name); }
interface Archivable { void compressArchive(String folderPath); }

// Публічний CDN вміє лише скачувати та ділитися посиланнями (він не вміє завантажувати на сервер)
@SuppressWarnings("all")
class ReadOnlyPublicCDN implements FileDownloadable, Shareable {
    @Override public byte[] downloadFile(String name) { return new byte[0]; }
    @Override public String generatePublicShareLink(String name) { return "https://cdn.com/" + name; }
}

// Локальний диск вміє завантажувати файли та архівувати папки (він не вміє генерувати хмарні посилання)
@SuppressWarnings("all")
class LocalFileSync implements FileUploadable, Archivable {
    @Override public void uploadFile(String name, byte[] data) { System.out.println("Завантажено файл: " + name); }
    @Override public void compressArchive(String folderPath) { System.out.println("Архівовано папку: " + folderPath); }
}

public class Task4_ISP {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        System.out.println("--- Завдання 4: ISP (Хмара) ---");
        
        // Створюємо CDN і викликаємо тільки його методи
        Shareable cdn = new ReadOnlyPublicCDN();
        System.out.println("Посилання: " + cdn.generatePublicShareLink("photo.jpg"));
        
        // Створюємо локальне сховище і викликаємо тільки його методи
        LocalFileSync local = new LocalFileSync();
        local.uploadFile("document.pdf", new byte[]{1, 2, 3}); 
        local.compressArchive("/docs");
    }
}
