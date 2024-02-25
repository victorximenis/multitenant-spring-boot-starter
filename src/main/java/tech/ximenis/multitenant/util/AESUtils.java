package tech.ximenis.multitenant.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtils {
    private static final String ALGORITHM = "AES";
    private static final String HEX_DIGITS = "0123456789ABCDEF";

    public static String decrypt(String key, String encryptText) {
        try {
            SecretKeySpec KeySpec = new SecretKeySpec(asByte(key), ALGORITHM);
            byte[] decoded = Base64.getDecoder().decode(encryptText);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, KeySpec);
            return new String(cipher.doFinal(decoded));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String encrypt(String key, String text) {
        try {
            SecretKeySpec KeySpec = new SecretKeySpec(asByte(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, KeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    private static byte[] asByte(String hexa) throws IllegalArgumentException {
        if (hexa.length() % 2 != 0)
            throw new IllegalArgumentException();

        byte[] b = new byte[hexa.length() / 2];
        for (int i = 0; i < hexa.length(); i += 2)
            b[i / 2] = (byte) ((HEX_DIGITS.indexOf(hexa.charAt(i)) << 4) | (HEX_DIGITS.indexOf(hexa.charAt(i + 1))));

        return b;
    }
}
