package org.pojava.persistence.util;

import java.security.InvalidKeyException;
import java.util.zip.DataFormatException;

import javax.crypto.SecretKey;

import org.pojava.persistence.serial.XmlParser;
import org.pojava.persistence.serial.XmlSerializer;
import org.pojava.util.Compression;
import org.pojava.util.EncodingTool;
import org.pojava.util.Encryption;

/**
 * WebSafe is a utility for transforming objects into web-safe strings that
 * can be natively used in XML, HTML, URL's or Cookies.  The objects are
 * encrypted so sensitive data cannot be obtained from them.
 * 
 * @author John Pile
 *
 */
public class WebSafe {

    private Encryption crypto;
    private Compression zipper;
    private XmlParser<Object> parser;
    private XmlSerializer serial;

    /**
     * Construct a WebSafe instance that encrypts/decrypts using the given key.
     * @param key
     * @throws InvalidKeyException
     */
    public WebSafe(String key) throws InvalidKeyException {
        crypto=new Encryption(key);
        zipper=new Compression();
        parser=new XmlParser<Object>();
        serial=new XmlSerializer();
    }

    /**
     * Construct a WebSafe instance that encrypts/decrypts using the given key.
     * @param key
     * @throws InvalidKeyException
     */
    public WebSafe(SecretKey key) throws InvalidKeyException {
        crypto=new Encryption(key);
        zipper=new Compression();
        parser=new XmlParser<Object>();
        serial=new XmlSerializer();
    }
    
    /**
     * Reconstitute a WebSafe string back into its original object form.
     * @param webSafeString
     * @return
     * @throws DataFormatException
     */
    Object stringToObject(String webSafeString) throws DataFormatException {
        byte[] encrypted=EncodingTool.base64Decode(webSafeString.toCharArray());
        byte[] zipped=crypto.decrypt(encrypted);
        String xml=new String(zipper.decompress(zipped));
        return parser.parse(xml);
    }
    
    /**
     * Convert an Object to a string of base64 characters (dropping trailing '=' chars).
     * @param obj
     * @return
     */
    String objectToString(Object obj) {
        String xml=serial.toXml(obj);
        byte[] zipped=zipper.compress(xml.getBytes());
        byte[] encrypted=crypto.encrypt(zipped);
        StringBuilder encoded=new StringBuilder(new String(EncodingTool.base64Encode(encrypted)));
        int len=encoded.length();
        // Base64 pads with '=', but Tomcat 6.0.14+ doesn't like '=' in cookie values.
        // I haven't figured out what paragraph of rfc2109 or rfc2965 is driving that interpretation,
        // bit since they are easily derived, we'll just drop the '=' characters.
        while (--len>0 && encoded.charAt(len)=='=') {
            encoded.setLength(len);
        }
        return encoded.toString();
    }
    
}
