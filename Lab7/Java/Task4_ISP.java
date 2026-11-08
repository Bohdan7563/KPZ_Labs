@SuppressWarnings("all")
public class Task4_ISP {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        System.out.println("--- Завдання 4: ISP (Хмара) ---");
        
        // 1. Публічний CDN вміє лише скачувати та ділитися посиланнями.
        // Він НЕ реалізує інтерфейс FileUploadable, тому ми не можемо випадково викликати в нього завантаження.
        Shareable cdn = new ReadOnlyPublicCDN();
        System.out.println("Посилання: " + cdn.generatePublicShareLink("photo.jpg"));
        
        // 2. Локальне сховище вміє завантажувати та архівувати.
        // Воно НЕ реалізує Shareable, тому не може генерувати публічні посилання.
        LocalFileSync local = new LocalFileSync();
        
        // ВИПРАВЛЕНО: Викликаємо метод uploadFile, щоб IDEA не сварилася, що він "never used"
        local.uploadFile("document.pdf", new byte[]{1, 2, 3}); 
        
        local.compressArchive("/docs");
    }
}

// Розбиваємо "жирний" інтерфейс на дрібні рольові (ISP)
@SuppressWarnings("all")
interface FileDownloadable { 
    byte[] downloadFile(String name); 
}

@SuppressWarnings("all")
interface FileUploadable { 
    void uploadFile(String name, byte[] data); 
}

@SuppressWarnings("all")
interface Shareable { 
    String generatePublicShareLink(String name); 
}

@SuppressWarnings("all")
interface Archivable { 
    void compressArchive(String folderPath); 
}

// Публічний CDN вміє лише скачувати та ділитися посиланнями
@SuppressWarnings("all")
class ReadOnlyPublicCDN implements FileDownloadable, Shareable {
    @Override 
    public byte[] downloadFile(String name) { 
        return new byte[0]; 
    }
    
    @Override 
    public String generatePublicShareLink(String name) { 
        return "https://cdn.com/" + name; 
    }
}

// Локальний диск вміє завантажувати та архівувати
@SuppressWarnings("all")
class LocalFileSync implements FileUploadable, Archivable {
    @Override 
    public void uploadFile(String name, byte[] data) { 
        System.out.println("Завантажено файл: " + name); 
    }
    
    @Override 
    public void compressArchive(String folderPath) { 
        System.out.println("Архівовано папку: " + folderPath); 
    }
}
