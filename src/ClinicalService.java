import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class ClinicalService {

    // AES encryption service for medical records
    private static final String AES_KEY = "0123456789abcdef0123456789abcdef";

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public static String encrypt(String plainText) {
        try {
            byte[] keyBytes = AES_KEY.getBytes("UTF-8");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // generate random IV using SecureRandom
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

            // combine IV + ciphertext and encode as base64
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            System.out.println("Encryption error: " + ex.getMessage());
            return null;
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            byte[] keyBytes = AES_KEY.getBytes("UTF-8");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // extract IV from first 12 bytes
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, "UTF-8");
        } catch (Exception ex) {
            System.out.println("Decryption error: " + ex.getMessage());
            return null;
        }
    }
}
