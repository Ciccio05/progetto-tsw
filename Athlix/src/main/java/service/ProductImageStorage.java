package service;

import javax.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/* RUOLO DELLA CLASSE: Servizio applicativo: contiene logica riutilizzabile indipendente dalla presentazione JSP. */
public final class ProductImageStorage {

    private static final String PUBLIC_PREFIX = "/product-image/";

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private static final Map<String, String> CONTENT_TYPE_TO_EXTENSION = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );

    private ProductImageStorage() {
    }

    public static Path getUploadDirectory() {
        String configured = System.getProperty("ATHLIX_UPLOAD_DIR");

        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }

        return Paths.get(
                System.getProperty("user.home"),
                "athlix-data",
                "uploads",
                "prodotti"
        ).toAbsolutePath().normalize();
    }

    /**
     * Salva un'immagine in una directory esterna al deploy Tomcat.
     * Restituisce un percorso logico relativo al context root, da salvare nel DB.
     */
    public static String save(Part part) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }

        String contentType = normalizeContentType(part.getContentType());
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Formato immagine non supportato. Usa JPG, PNG, WEBP o GIF."
            );
        }

        String extension = CONTENT_TYPE_TO_EXTENSION.get(contentType);
        String fileName = UUID.randomUUID() + extension;
        Path directory = getUploadDirectory();
        Files.createDirectories(directory);

        Path destination = directory.resolve(fileName).normalize();
        if (!destination.getParent().equals(directory)) {
            throw new IOException("Percorso di upload non valido.");
        }

        try (BufferedInputStream input = new BufferedInputStream(part.getInputStream())) {
            input.mark(32);
            byte[] header = input.readNBytes(16);
            input.reset();

            if (!hasValidSignature(contentType, header)) {
                throw new IllegalArgumentException("Il contenuto del file non corrisponde a un'immagine valida.");
            }

            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        return PUBLIC_PREFIX + fileName;
    }

    /* Elimina o scollega i record richiesti rispettando i vincoli di integrità. */

    public static void deleteIfUploaded(String imagePath) {
        if (!isUploadedPath(imagePath)) {
            return;
        }

        String fileName = imagePath.substring(PUBLIC_PREFIX.length());
        if (!isSafeFileName(fileName)) {
            return;
        }

        Path directory = getUploadDirectory();
        Path file = directory.resolve(fileName).normalize();

        if (!file.getParent().equals(directory)) {
            return;
        }

        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println(
                    "Impossibile eliminare l'immagine prodotto " + fileName + ": " + e.getMessage()
            );
        }
    }

    public static boolean isUploadedPath(String imagePath) {
        return imagePath != null && imagePath.startsWith(PUBLIC_PREFIX);
    }

    /* Risolva in sicurezza il percorso del file richiesto. */

    public static Path resolveUploadedFile(String fileName) {
        if (!isSafeFileName(fileName)) {
            return null;
        }

        Path directory = getUploadDirectory();
        Path file = directory.resolve(fileName).normalize();
        return file.getParent().equals(directory) ? file : null;
    }

    /* Determina il Content-Type corretto per il file richiesto. */

    public static String contentTypeFor(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }

    /* Verifica la firma reale del file prima di accettarne il contenuto. */

    private static boolean hasValidSignature(String contentType, byte[] header) {
        if (header == null || header.length < 4) {
            return false;
        }

        return switch (contentType) {
            case "image/jpeg" -> (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;
            case "image/png" -> header.length >= 8
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 0x50
                    && header[2] == 0x4E
                    && header[3] == 0x47
                    && header[4] == 0x0D
                    && header[5] == 0x0A
                    && header[6] == 0x1A
                    && header[7] == 0x0A;
            case "image/gif" -> header.length >= 6
                    && header[0] == 'G'
                    && header[1] == 'I'
                    && header[2] == 'F'
                    && header[3] == '8'
                    && (header[4] == '7' || header[4] == '9')
                    && header[5] == 'a';
            case "image/webp" -> header.length >= 12
                    && header[0] == 'R'
                    && header[1] == 'I'
                    && header[2] == 'F'
                    && header[3] == 'F'
                    && header[8] == 'W'
                    && header[9] == 'E'
                    && header[10] == 'B'
                    && header[11] == 'P';
            default -> false;
        };
    }

    /* Normalizza il valore ricevuto prima di usarlo nella logica applicativa. */

    private static String normalizeContentType(String contentType) {
        if (contentType == null) {
            return "";
        }
        int semicolon = contentType.indexOf(';');
        String value = semicolon >= 0 ? contentType.substring(0, semicolon) : contentType;
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isSafeFileName(String fileName) {
        return fileName != null
                && !fileName.isBlank()
                && fileName.matches("[A-Za-z0-9._-]+")
                && !fileName.contains("..")
                && !fileName.contains("/")
                && !fileName.contains("\\\\");
    }
}
