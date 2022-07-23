package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.exception.UnexpectedErrorException;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.model.AssetTypes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
@Qualifier("JdbcAccountsRepository")
public class JdbcAccountsRepository implements IAccountsRepository {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public Account createAccount(Account account) throws UnexpectedErrorException {
        String sql = "INSERT INTO `accounts` " +
                "(name, surname, email, id_number, account_type, balance, last_updated, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Fill fields in prepared statement
            ps.setString(1, account.getName()); // set name
            ps.setString(2, account.getSurname()); // set surname
            ps.setString(3, account.getEmail()); // set email
            ps.setString(4, account.getIdNumber()); // set id number
            ps.setString(5, account.getAccountType().toString()); // set account type
            ps.setDouble(6, account.getBalance()); // set balance
            ps.setTimestamp(7, new Timestamp(account.getLastUpdated())); // set update time
            ps.setInt(8, account.getIsDeleted()); // set is deleted flag

            // Execute query
            int affectedRows = ps.executeUpdate();

            // Throw an exception if no rows are updated
            if (affectedRows == 0) {
                throw new UnexpectedErrorException("JDBC Accounts Repository: SQL error: Creating user failed, no rows affected");
            }

            // Throw an exception if account id is not available
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    account.setId(generatedKeys.getInt(1));
                } else {
                    throw new UnexpectedErrorException("JDBC Accounts Repository: SQL error: Creating user failed, no ID obtained");
                }
            }
        } catch (SQLException e) {
            throw new UnexpectedErrorException("JDBC Accounts Repository: SQL error: " + e.getMessage());
        }

        return account;
    }

    @Override
    public Account updateAccount(Account account) throws UnexpectedErrorException {
        String sql = "UPDATE `accounts` SET balance = ?, last_updated = ?, is_deleted = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Fill fields in prepared statement
            ps.setDouble(1, account.getBalance()); // set balance
            ps.setTimestamp(2, new Timestamp(account.getLastUpdated())); // set update time
            ps.setInt(3, account.getIsDeleted()); // set is deleted flag
            ps.setLong(4, account.getId()); // set id number

            // Execute query
            int affectedRows = ps.executeUpdate();

            // Throw an exception if no rows are updated
            if (affectedRows == 0) {
                throw new UnexpectedErrorException("JDBC Accounts Repository: SQL error: Updating user failed, no rows affected");
            }
        } catch (SQLException e) {
            throw new UnexpectedErrorException("JDBC Accounts Repository: SQL error: " + e.getMessage());
        }

        return account;
    }

    @Override
    public Account getAccount(long id) throws UnexpectedErrorException {
        String sql = "SELECT * FROM `accounts` WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Fill fields in the prepared statement
            ps.setLong(1, id); // set id

            // Execute query
            ResultSet rs = ps.executeQuery();

            // Convert records to an account object and return
            while (rs.next()) {
                return Account.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .surname(rs.getString("surname"))
                        .email(rs.getString("email"))
                        .idNumber(rs.getString("id_number"))
                        .accountType(AssetTypes.valueOf(rs.getString("account_type")))
                        .lastUpdated(rs.getTimestamp("last_updated").getTime())
                        .isDeleted(rs.getInt("is_deleted"))
                        .build();
            }
        } catch (SQLException e) {
            throw new UnexpectedErrorException("JDBC Accounts Repository: SQL error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean deleteAccount(Account account) throws UnexpectedErrorException {
        account.setIsDeleted(1); // set deleted flag as true
        Account updatedAccount = updateAccount(account);

        return updatedAccount != null && updatedAccount.getIsDeleted() == 1;
    }
}
