package org.pojava.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.pojava.exception.InconceivableException;

/**
 * This utility provides a simple interface for encrypting and decrypting data using
 * high-quality encryption algorithms such as AES-128.
 * 
 * It also provides a means of importing and exporting the cipher keys in a portable format, a
 * Base64 encoded string that can be stored anywhere you like.
 * 
 * Example usage: <code>
 * String doc="This is the document to be encrypted.";
 * String key=Encryption.exportKey(Encryption.generateAES128Key());
 * (This key could be retrieved from JNDI, for example)
 * Encryption encryptor=new Encryption(key);
 * byte[] encrypted=encryptor.encrypt(doc.getBytes());
 * ...
 * byte[] decrypted=encryptor.decrypt(doc.getBytes());
 * String originalDoc=new String(decrypted);
 * </code>
 * 
 * @author John Pile
 * 
 */
public class Encryption {

    /**
     * Cipher used for encrypting the payload.
     */
    private Cipher encryptCipher;

    /**
     * Cipher used for decrypting the payload
     */
    private Cipher decryptCipher;

    /**
     * Initialize Encryption using a specified key. See methods generateAES128Key, importKey and
     * exportKey.
     * 
     * @param key
     */
    public Encryption(SecretKey key) {
        createCiphers(key);
    }

    /**
     * Create the Cipher pair from a SecretKey
     * 
     * @param key
     */
    private void createCiphers(SecretKey key) {
        SecretKeySpec skeySpec = new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
        try {
            encryptCipher = Cipher.getInstance(key.getAlgorithm());
            encryptCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            decryptCipher = Cipher.getInstance(key.getAlgorithm());
            decryptCipher.init(Cipher.DECRYPT_MODE, skeySpec);
        } catch (NoSuchPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (InvalidKeyException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Create a cipher pair from an exported key.
     * 
     * @param strKey
     *            A key exported from a SecretKey object.
     * @throws InvalidKeyException
     */
    public Encryption(String strKey) throws InvalidKeyException {
        SecretKey key = importKey(strKey);
        createCiphers(key);
    }

    /**
     * This will generate a unique 128bit AES key each time it is called.
     * 
     * @return a newly generated 128 bit AES key
     */
    public static SecretKey generateAES128Key() {
        SecretKey skey;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128); // 128 bit AES encryption
            skey = kgen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            throw new InconceivableException("AES really should be available... "
                    + ex.getMessage(), ex);
        }
        return skey;
    }

    /**
     * Export a key as a string (useful in a properties file)
     * 
     * @param key
     * @return a portable text version of a SecretKey.
     */
    public static String exportKey(SecretKey key) {
        StringBuffer export = new StringBuffer(key.getAlgorithm());
        export.append(" ");
        export.append(EncodingTool.base64Encode(key.getEncoded()));
        return export.toString();
    }

    /**
     * Import a secret key from an exported key
     * 
     * @param base64key
     * @return A SecretKey from an exported key.
     * @throws InvalidKeyException
     */
    public static SecretKey importKey(String base64key) throws InvalidKeyException {
        String[] pair = base64key.split("\\s+");
        if (pair.length != 2) {
            throw new InvalidKeyException(
                    "Key should be of format 'algorithm base64encodedKey'");
        }
        byte[] rawKey = EncodingTool.base64Decode(pair[1]);
        return new SecretKeySpec(rawKey, pair[0]);
    }

    /**
     * Encrypt a message using a secret key. This method is thread-safe.
     * 
     * @param message
     * @param key
     * @return An encrypted byte array from the source.
     */
    public static byte[] encrypt(byte[] message, SecretKey key) {
        byte[] encrypted;
        try {
            byte[] raw = key.getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, key.getAlgorithm());
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(message);
        } catch (BadPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (InvalidKeyException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        return encrypted;
    }

    /**
     * Decrypt a message using a secret key. This method is thread-safe.
     * 
     * @param encrypted
     * @param key
     * @return A decrypted byte array from an encrypted source.
     */
    public static byte[] decrypt(byte[] encrypted, SecretKey key) {
        byte[] decrypted;
        try {
            byte[] raw = key.getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, key.getAlgorithm());
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            decrypted = cipher.doFinal(encrypted);
            return decrypted;
        } catch (BadPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (InvalidKeyException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Encrypt data using a pre-defined Cipher. The thread-safety of this method may vary by
     * encryption algorithm.
     * 
     * @param message
     * @return An encrypted byte array from the given source.
     */
    public byte[] encrypt(byte[] message) {
        try {
            return encryptCipher.doFinal(message);
        } catch (BadPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Decrypt an encrypted byte array. The thread-safety of this method may vary by encryption
     * algorithm, so it's safest to create a unique object per thread.
     * 
     * @param encrypted
     * @return A decrypted byte array rebuilt from the encrypted source.
     */
    public byte[] decrypt(byte[] encrypted) {
        try {
            return decryptCipher.doFinal(encrypted);
        } catch (BadPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    }

}