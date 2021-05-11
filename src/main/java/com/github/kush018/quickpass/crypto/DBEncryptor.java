package com.github.kush018.quickpass.crypto;

import com.github.kush018.quickpass.database.Database;

import java.io.*;

public class DBEncryptor {
    public static byte[] getBytes(Database db) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(db);
        oos.flush();
        return bos.toByteArray();
    }

    public static Database getDatabase(byte[] bytes) throws ClassNotFoundException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = new ObjectInputStream(bis);
        Object o = in.readObject();
        return (Database) o;
    }

    public static byte[] encryptBytes(byte[] pBytes, String password) throws Exception {
        return EncryptorAesGcmPasswordFile.encrypt(pBytes, password);
    }

    public static byte[] decryptBytes(byte[] cBytes, String password) throws Exception {
        return EncryptorAesGcmPasswordFile.decrypt(cBytes, password);
    }
}
