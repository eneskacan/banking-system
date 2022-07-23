package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.exception.UnexpectedErrorException;
import com.eneskacan.bankingsystem.model.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("JdbcLogsRepository")
public class JdbcLogsRepository implements ILogsRepository {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public boolean saveLog(Log l) throws UnexpectedErrorException {
        String sql = "INSERT INTO `logs` (message) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Fill fields in prepared statement
            ps.setString(1, l.getMessage()); // set message

            // Return true if log is successfully added
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new UnexpectedErrorException("JDBC Logs Repository: SQL error: " + e.getMessage());
        }
    }

    @Override
    public List<Log> getLogs() throws UnexpectedErrorException {
        List<Log> logs = new ArrayList<>();
        String sql = "SELECT * FROM `logs`";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Convert records to a log object and add to list
            while (rs.next()) {
                logs.add(new Log(rs.getString("message")));
            }
        } catch (SQLException e) {
            throw new UnexpectedErrorException("JDBC Logs Repository: SQL error: " + e.getMessage());
        }

        return logs;
    }
}
