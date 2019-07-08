package com.yahoo.identity.services.storage.sql;

import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import com.kosprov.jargon2.api.Jargon2;
import com.yahoo.identity.IdentityException;
import com.yahoo.identity.services.account.AccountCreate;
import com.yahoo.identity.services.random.RandomService;
import com.yahoo.identity.services.storage.AccountModel;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

import javax.annotation.Nonnull;
import javax.ws.rs.BadRequestException;


public class SqlAccountCreate implements AccountCreate {

    private final SqlSessionFactory sqlSessionFactory;
    private final AccountModel account = new AccountModel();
    private final SecureRandom secureRandom;

    public SqlAccountCreate(@Nonnull SqlSessionFactory sqlSessionFactory, @Nonnull RandomService randomService) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.secureRandom = randomService.getRandom();
    }

    @Override
    @Nonnull
    public AccountCreate setUsername(@Nonnull String username) {
        account.setUsername(escapeHtml4(username));
        return this;
    }

    @Override
    @Nonnull
    public AccountCreate setFirstName(@Nonnull String firstName) {
        account.setFirstName(escapeHtml4(firstName));
        return this;
    }

    @Override
    @Nonnull
    public AccountCreate setLastName(@Nonnull String lastName) {
        account.setLastName(escapeHtml4(lastName));
        return this;
    }

    @Override
    @Nonnull
    public AccountCreate setEmail(@Nonnull String email) {
        account.setEmail(escapeHtml4(email));
        return this;
    }

    @Override
    @Nonnull
    public AccountCreate setEmailStatus(@Nonnull boolean emailStatus) {
        account.setEmailStatus(emailStatus);
        return this;
    }

    @Override
    @Nonnull
    public AccountCreate setPassword(@Nonnull String password) {

        byte[] saltBytes = new byte[64];
        secureRandom.nextBytes(saltBytes);

        account.setPasswordSalt(Base64.getEncoder().encodeToString(saltBytes));

        Jargon2.Hasher hasher = jargon2Hasher();
        try {
            account.setPasswordHash(hasher.salt(saltBytes).password(password.getBytes("UTF-8")).encodedHash());
        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException("Unsupported encoding.");
        }
        return this;
    }

    @Override
    @Nonnull
    public AccountCreate setCreateTime(@Nonnull Instant createTime) {
        account.setCreateTs(createTime.toEpochMilli());
        return this;
    }

    @Override
    @Nonnull
    public AccountCreate setUpdateTime(@Nonnull Instant updateTime) {
        account.setUpdateTs(updateTime.toEpochMilli());
        return this;
    }

    @Nonnull
    @Override
    public AccountCreate setDescription(@Nonnull String title) {
        account.setDescription(escapeHtml4(title));
        return this;
    }

    @Nonnull
    @Override
    public AccountCreate setBlockUntilTime(@Nonnull Instant blockUntil) {
        account.setBlockUntilTs(blockUntil.toEpochMilli());
        return this;
    }

    @Nonnull
    @Override
    public AccountCreate setConsecutiveFails(@Nonnull int consecutiveFails) {
        account.setConsecutiveFails(consecutiveFails);
        return this;
    }

    @Nonnull
    @Override
    public String create() throws IdentityException {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            try {
                mapper.insertAccount(account);
                session.commit();
            } catch (Exception e) {
                throw new BadRequestException("account already exists");
            }
        }
        return account.getUsername();
    }
}
