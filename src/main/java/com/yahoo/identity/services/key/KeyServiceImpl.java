package com.yahoo.identity.services.key;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.Nonnull;
import javax.ws.rs.InternalServerErrorException;

public class KeyServiceImpl implements KeyService {

    private final KeyServiceUtils keyServiceUtils = new KeyServiceUtils();

    private static String readFileAsString(String fileName) throws Exception {
        String data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    @Override
    @Nonnull
    public String getSecret(@Nonnull String secretKeyName) {
        try {
            return readFileAsString(".secret/" + secretKeyName);
        } catch (FileNotFoundException e) {
            throw new InternalServerErrorException("Secret key file doesn't exists: " + e.toString());
        } catch (Exception e) {
            throw new InternalServerErrorException("Unknown error occurs when reading file: " + e.toString());
        }
    }


    @Override
    @Nonnull
    public PublicKey getPublicKey(@Nonnull String publicKeyName, @Nonnull String crpytoScheme) {
        try {
            return keyServiceUtils.readPublicKeyFromFile(".secret/" + publicKeyName, crpytoScheme);
        } catch (Exception e) {
            throw new InternalServerErrorException("Unknown error occurs when reading file: " + e.toString());
        }
    }

    @Override
    @Nonnull
    public PrivateKey getPrivateKey(@Nonnull String privateKeyName, @Nonnull String crpytoScheme) {
        try {
            return keyServiceUtils.readPrivateKeyFromFile(".secret/" + privateKeyName, crpytoScheme);
        } catch (Exception e) {
            throw new InternalServerErrorException("Unknown error occurs when reading file: " + e.toString());
        }
    }
}
