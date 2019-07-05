package com.yahoo.identity.services.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yahoo.identity.services.key.KeyService;
import com.yahoo.identity.services.key.KeyServiceImpl;
import org.openapitools.model.Token.TypeEnum;
import sun.security.ec.ECPrivateKeyImpl;
import sun.security.ec.ECPublicKeyImpl;

import java.security.InvalidKeyException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import javax.annotation.Nonnull;
import javax.ws.rs.BadRequestException;

public class TokenCreateImpl implements TokenCreate {

    private Token token = new TokenImpl();
    private KeyService keyService = new KeyServiceImpl();

    @Override
    @Nonnull
    public TokenCreate setType(@Nonnull TypeEnum type) {
        switch (type) {
            case CRITICAL:
                token.setTokenType(TokenType.CRITICAL);
                break;
            case STANDARD:
                token.setTokenType(TokenType.STANDARD);
                break;
            default:
                throw new BadRequestException("Token type is not valid");
        }
        return this;
    }

    @Override
    @Nonnull
    public TokenCreate setToken(@Nonnull String tokenStr) {
        try {
            ECPublicKey ecPublicKey = new ECPublicKeyImpl(this.keyService.getSecret("token-public").getBytes());
            ECPrivateKey ecPrivateKey = new ECPrivateKeyImpl(this.keyService.getSecret("token-private").getBytes());

            Algorithm algorithm = Algorithm.ECDSA256(ecPublicKey, ecPrivateKey);

            JWTVerifier verifier = JWT.require(algorithm).build();

            DecodedJWT jwt = verifier.verify(tokenStr);

            token.setSubject(jwt.getSubject());
            token.setIssueTime(jwt.getIssuedAt().toInstant());
            token.setExpireTime(jwt.getExpiresAt().toInstant());

        } catch (JWTVerificationException e) {
            throw new BadRequestException("JWT verification does not succeed.");
        } catch (InvalidKeyException e) {
            throw new BadRequestException("Invalid key for ECDSA256.");
        }
        token.validate();
        return this;
    }

    @Override
    @Nonnull
    public Token create() {
        return this.token;
    }
}
