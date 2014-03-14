package org.pojava.persistence.util;

import org.pojava.persistence.serial.XmlParser;
import org.pojava.persistence.serial.XmlSerializer;
import org.pojava.util.Compression;
import org.pojava.util.EncodingTool;
import org.pojava.util.EncryptionTool;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.zip.DataFormatException;

/**
 * WebSafe is a utility for transforming objects into web-safe strings that
 * can be natively used in XML, HTML, URL's or Cookies.  The objects are
 * encrypted so sensitive data cannot be obtained from them.
 *
 * @author John Pile
 */
public class WebSafe {

    private Compression zipper;
    private XmlParser<Object> parser;
    private XmlSerializer serial;
    private String key;

    /**
     * Construct a web-safe with a keyString (see EncryptionTool)
     *
     * @param keyString base64-encrypted key used to encrypt contents
     * @throws java.security.InvalidKeyException
     */
    public WebSafe(String keyString) throws InvalidKeyException {
        this.key = keyString;
        zipper = new Compression();
        parser = new XmlParser<Object>();
        serial = new XmlSerializer();
    }

    /**
     * Use an externally constructed SecretKey
     *
     * @param key secretKey
     * @throws java.security.InvalidKeyException
     * @deprecated in favor of the String parameter
     */
    public WebSafe(SecretKey key) throws InvalidKeyException {
        this.key = key.getAlgorithm() + " " + EncodingTool.base64Encode(key.getEncoded());
        zipper = new Compression();
        parser = new XmlParser<Object>();
        serial = new XmlSerializer();
    }

    /**
     * Convert a web-safe string back to its original object
     *
     * @param webSafeString String encoded from an object
     * @return Object deserialized from String
     * @throws java.util.zip.DataFormatException
     * @throws java.security.GeneralSecurityException
     */
    Object stringToObject(String webSafeString) throws DataFormatException, GeneralSecurityException {
        byte[] encrypted = EncodingTool.base64Decode(webSafeString.toCharArray());
        byte[] zipped = EncryptionTool.decrypt(encrypted, key);
        String xml = new String(zipper.decompress(zipped));
        return parser.parse(xml);
    }

    /**
     * Convert a web-safe string back to its original object
     *
     * @param webSafeString String encoded from an object
     * @return String with illegal characters substituted or removed
     * @throws java.util.zip.DataFormatException
     * @throws java.security.GeneralSecurityException
     */
    Object stringToObject(String webSafeString, IvParameterSpec ivps) throws DataFormatException, GeneralSecurityException {
        byte[] encrypted = EncodingTool.base64Decode(webSafeString.toCharArray());
        byte[] zipped = EncryptionTool.decrypt(encrypted, key, ivps);
        String xml = new String(zipper.decompress(zipped));
        return parser.parse(xml);
    }

    /**
     * Convert an object to a web-safe string
     *
     * @param obj POJO to convert to a String
     * @return Web safe string
     * @throws java.security.GeneralSecurityException
     */
    String objectToString(Object obj) throws GeneralSecurityException {
        String xml = serial.toXml(obj);
        byte[] zipped = zipper.compress(xml.getBytes());
        byte[] encrypted = EncryptionTool.encrypt(zipped, key);
        StringBuilder encoded = new StringBuilder(EncodingTool.base64Encode(encrypted));
        int len = encoded.length();
        // Base64 pads with '=', but Tomcat 6.0.14+ doesn't like '=' in cookie values.
        // I haven't figured out what paragraph of rfc2109 or rfc2965 is driving that interpretation.
        // Since they are easily derived, we'll just drop the '=' characters.
        while (--len > 0 && encoded.charAt(len) == '=') {
            encoded.setLength(len);
        }
        return encoded.toString();
    }

    /**
     * Convert an object to a web-safe string
     *
     * @param obj POJO to convert to a String
     * @param ivps IV or salt to obfuscate repeated instances of encrypted data
     * @return web-safe string
     * @throws java.security.GeneralSecurityException
     */
    String objectToString(Object obj, IvParameterSpec ivps) throws GeneralSecurityException {
        String xml = serial.toXml(obj);
        byte[] zipped = zipper.compress(xml.getBytes());
        byte[] encrypted = EncryptionTool.encrypt(zipped, key, ivps);
        StringBuilder encoded = new StringBuilder(EncodingTool.base64Encode(encrypted));
        int len = encoded.length();
        // Base64 pads with '=', but Tomcat 6.0.14+ doesn't like '=' in cookie values.
        // I haven't figured out what paragraph of rfc2109 or rfc2965 is driving that interpretation.
        // Since they are easily derived, we'll just drop the '=' characters.
        while (--len > 0 && encoded.charAt(len) == '=') {
            encoded.setLength(len);
        }
        return encoded.toString();
    }
}
