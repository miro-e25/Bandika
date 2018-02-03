/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.base.crypto;

import de.bandika.base.crypto.shiro.AesCipherService;
import de.bandika.base.crypto.shiro.CipherService;
import de.bandika.base.crypto.shirocodec.Base64;
import de.bandika.base.crypto.shirocodec.CodecSupport;
import de.bandika.base.log.Log;

import java.time.LocalDateTime;
import java.util.Arrays;

public class AESEncryption {

    private static final byte[] NULLBYTES = CodecSupport.toBytes("NULL.0000.NULL");

    public static byte[] generateKey() {
        AesCipherService service = new AesCipherService();
        return service.generateNewKey(128).getEncoded();
    }

    public static String generateKeyBase64() {
        return Base64.encodeToString(generateKey());
    }

    public static String adjustKey(String key) {
        char[] chars = new char[24];
        System.arraycopy(key.toCharArray(), 0, chars, 0, key.length());
        for (int i = key.length(); i < 24; i++) {
            chars[i] = '=';
        }
        return new String(chars);
    }

    public static byte[] encrypt(byte[] data, byte[] key) {
        CipherService service = new AesCipherService();
        byte[] original = (data == null) ? NULLBYTES : data;
        return service.encrypt(original, key).getBytes();
    }

    public static byte[] encrypt(byte[] data, String keyBase64) {
        byte[] key = Base64.decode(keyBase64);
        return encrypt(data, key);
    }

    public static String encryptBytes(byte[] data, String keyBase64) {
        CipherService service = new AesCipherService();
        byte[] original = (data == null) ? NULLBYTES : data;
        byte[] key = Base64.decode(keyBase64);
        return service.encrypt(original, key).toBase64();
    }

    public static String encryptString(String source, String keyBase64) {
        byte[] original = (source == null) ? null : CodecSupport.toBytes(source);
        return encryptBytes(original, keyBase64);
    }

    public static String encryptDate(LocalDateTime date, String keyBase64) {
        String original = (date == null) ? null : date.toString();
        return encryptString(original, keyBase64);
    }

    public static String encryptNumber(Number number, String keyBase64) {
        String original = (number == null) ? null : number.toString();
        return encryptString(original, keyBase64);
    }

    public static byte[] decrypt(byte[] cipherText, byte[] key) {
        CipherService service = new AesCipherService();
        byte[] original = service.decrypt(cipherText, key).getBytes();
        return Arrays.equals(original, NULLBYTES) ? null : original;
    }

    public static byte[] decrypt(byte[] cipherText, String keyBase64) {
        byte[] key = Base64.decode(keyBase64);
        return decrypt(cipherText, key);
    }

    public static byte[] decryptBytes(String cipherTextBase64, String keyBase64) {
        if (cipherTextBase64 == null) {
            Log.info("getting null as cipherTextBase64");
            return null;
        }
        CipherService service = new AesCipherService();
        byte[] cipherText = Base64.decode(cipherTextBase64);
        byte[] key = Base64.decode(keyBase64);
        byte[] original = service.decrypt(cipherText, key).getBytes();
        return Arrays.equals(original, NULLBYTES) ? null : original;
    }

    public static String decryptString(String cipherTextBase64, String keyBase64) {
        byte[] original = decryptBytes(cipherTextBase64, keyBase64);
        return (original == null) ? null : CodecSupport.toString(original);
    }

    public static LocalDateTime decryptDate(String cipherTextBase64, String keyBase64) {
        String dateString = decryptString(cipherTextBase64, keyBase64);
        return (dateString == null) ? null : LocalDateTime.parse(dateString);
    }

    public static Number decryptNumber(String cipherTextBase64, String keyBase64) {
        String numString = decryptString(cipherTextBase64, keyBase64);
        return (numString == null) ? null : Double.valueOf(numString);
    }
}
