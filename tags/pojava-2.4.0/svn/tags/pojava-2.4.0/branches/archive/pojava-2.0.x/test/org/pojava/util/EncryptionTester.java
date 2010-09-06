package org.pojava.util;

import javax.crypto.SecretKey;
import javax.naming.Context;

import junit.framework.TestCase;

import org.pojava.testing.JNDIRegistry;

public class EncryptionTester extends TestCase {

    private static final String TEST_KEY = "AES EgNhNcv0E/QYrWso04LreA==";

    protected void setUp() throws Exception {
        super.setUp();
        Context ctx = JNDIRegistry.getInitialContext();
        // Store a generated key wherever you like.
        // In this case, it is managed using JNDI
        ctx.rebind("java:comp/env/testkey", TEST_KEY);
    }

    /**
     * This dataset returns a comma-separated list of numbers and characters.
     * 
     * @return
     */
    private String generateTestString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1000; i++) {
            sb.append(new Integer(i).toString());
            sb.append(", ");
            sb.append(Character.forDigit(i % 36, 36));
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public void testGenerateAES128Key() {
        SecretKey key = Encryption.generateAES128Key();
        assertEquals("AES", key.getAlgorithm());
        String strKey = Encryption.exportKey(key);
        assertTrue(strKey.startsWith("AES "));
        assertTrue(strKey.endsWith("=="));
    }

    public void testEncryptionStatic() throws Exception {
        // Retrieve the key from JNDI.
        String strKey = (String) JNDIRegistry.lookupEnv("testkey");
        // Something to encrypt
        byte[] original = generateTestString().getBytes();
        // Reconstruct a SecretKey from its string rendition
        SecretKey key = Encryption.importKey(strKey);
        // Perform the encryption.
        byte[] encrypted = Encryption.encrypt(original, key);
        // Decrypt the encrypted data.
        byte[] decrypted = Encryption.decrypt(encrypted, key);
        // The decrypted value should be different from the original
        assertFalse(encrypted[0] == decrypted[0] && encrypted[1] == decrypted[1]);
        // The result should be identical to the original.
        assertEquals(new String(original), new String(decrypted));
    }

    public void testEncryptionObject() throws Exception {
        // Retrieve the key from JNDI.
        String strKey = (String) JNDIRegistry.lookupEnv("testkey");
        // Something to encrypt
        byte[] original = generateTestString().getBytes();
        // The Encryptor will internally rebuild a SecretKey from the given
        // string.
        Encryption encryptor = new Encryption(strKey);
        // Encrypt the payload
        byte[] encrypted = encryptor.encrypt(original);
        // Decrypt the encrypted payload.
        byte[] decrypted = encryptor.decrypt(encrypted);
        // The decrypted value should be different from the original
        assertFalse(encrypted[0] == decrypted[0] && encrypted[1] == decrypted[1]);
        // The result should be identical to the original.
        assertEquals(new String(original), new String(decrypted));
    }

    public void testOneLiner() throws Exception {
        String randomKey = Encryption.exportKey(Encryption.generateAES128Key());
        byte[] original = generateTestString().getBytes();
        byte[] encrypted = new Encryption(randomKey).encrypt(original);
        byte[] decrypted = new Encryption(randomKey).decrypt(encrypted);
        assertEquals(new String(original), new String(decrypted));
    }

}
