package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/* RUOLO DELLA CLASSE: Classe di utilità: raccoglie funzioni di supporto condivise dal progetto. */
public final class SecurityUtils {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final String PREFIX = "pbkdf2";

    private SecurityUtils() {
    }

    /* Genera un hash PBKDF2 con salt casuale per salvare la password in modo sicuro. */

    public static String hashPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La password non può essere vuota.");
        }

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS);

        return PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /* Verifica una password confrontandola in modo sicuro con l’hash memorizzato. */

    public static boolean verifyPassword(String password, String storedPassword) {
        if (password == null || storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (storedPassword.startsWith(PREFIX + "$")) {
            String[] parts = storedPassword.split("\\$");
            if (parts.length != 4) {
                return false;
            }

            try {
                int iterations = Integer.parseInt(parts[1]);
                byte[] salt = Base64.getDecoder().decode(parts[2]);
                byte[] expected = Base64.getDecoder().decode(parts[3]);
                byte[] actual = pbkdf2(password.toCharArray(), salt, iterations);
                return MessageDigest.isEqual(expected, actual);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // Compatibilità temporanea con eventuali vecchi hash SHA-256 a 64 caratteri.
        if (storedPassword.matches("(?i)[0-9a-f]{64}")) {
            return MessageDigest.isEqual(
                    storedPassword.toLowerCase().getBytes(StandardCharsets.UTF_8),
                    legacySha256(password).getBytes(StandardCharsets.UTF_8)
            );
        }

        // Le password in chiaro non vengono accettate.
        return false;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Impossibile calcolare l'hash della password.", e);
        } finally {
            spec.clearPassword();
        }
    }

    /* Calcola un vecchio hash SHA-256 mantenuto solo per compatibilità con dati preesistenti. */

    private static String legacySha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponibile.", e);
        }
    }
}
