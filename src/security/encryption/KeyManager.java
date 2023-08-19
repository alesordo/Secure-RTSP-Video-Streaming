package security.encryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.KeyStore;
import java.util.Properties;

public class KeyManager {
    private static KeyStore createKeyStore(String fileName, String pw, String algorithm) throws Exception {
        File file = new File(fileName);
        final KeyStore keyStore = KeyStore.getInstance("JCEKS");
        if (file.exists()) {
            // .keystore file already exists => load it
            keyStore.load(new FileInputStream(file), pw.toCharArray());
        } else {
            // .keystore file not created yet => create it and save the keys
            keyStore.load(null, null);

            //Generate a secret key for encryption - the same for all the blocks
            String[] fields= algorithm.split("/");
            SecretKey secretKey = KeyGenerator.getInstance(fields[0]).generateKey();

            //Generate a secret key for mac
            SecretKey hMacKey =new SecretKeySpec(secretKey.getEncoded(), "HmacSHA512");

            //Store the secret key
            KeyStore.SecretKeyEntry keyStoreEntry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection("pw-secret".toCharArray());
            keyStore.setEntry("mySecretKey", keyStoreEntry, keyPassword);
            keyStore.store(new FileOutputStream(fileName), pw.toCharArray());

            //Store the hmac key
            KeyStore.SecretKeyEntry keyStoreHMacEntry = new KeyStore.SecretKeyEntry(hMacKey);
            keyStore.setEntry("hMacSecretKey",keyStoreHMacEntry,keyPassword);
            keyStore.store(new FileOutputStream(fileName),pw.toCharArray());
        }
        return keyStore;
    }
    public static SecretKey[] getKeys() throws Exception{
        SecretKey[] secretKeys = new SecretKey[2];
        String[] parameters = getParameters();

        String fileName = parameters[1];
        String pw = parameters[2];
        String algorithm = parameters[0];

        //Retrieve/create KeyStore file
        KeyStore keyStore = createKeyStore(fileName,pw,algorithm);

        //Retrieve keys
        KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection("pw-secret".toCharArray());

        KeyStore.Entry keyEntry = keyStore.getEntry("mySecretKey", keyPassword);
        secretKeys[0] = ((KeyStore.SecretKeyEntry) keyEntry).getSecretKey();

        KeyStore.Entry hMacKeyEntry = keyStore.getEntry("hMacSecretKey", keyPassword);
        secretKeys[1] = ((KeyStore.SecretKeyEntry) hMacKeyEntry).getSecretKey();
        return secretKeys;
    }
    public static String[] getParameters() throws Exception{
        try{
            InputStream inputStream = new FileInputStream("security/configSecurity.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            String algorithm = properties.getProperty("algorithm");
            String keyStorePath = properties.getProperty("keyStorePath");
            String keyStorePass = properties.getProperty("keyStorePass");

            return new String[]{algorithm,keyStorePath,keyStorePass};
        }
        catch(Exception e){
            System.err.println("Security configuration file not found!");
            throw e;
        }
    }
}
