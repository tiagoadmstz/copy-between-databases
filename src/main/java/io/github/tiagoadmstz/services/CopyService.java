package io.github.tiagoadmstz.services;

import io.github.tiagoadmstz.dal.SqlServerConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static io.github.tiagoadmstz.services.DataBaseConstants.*;

public class CopyService {
    private final SqlServerConnection serverConnection;
    private final HashMap<DataBaseConstants, String> originDatabase = new HashMap<DataBaseConstants, String>();
    private final HashMap<DataBaseConstants, String> destinyDatabase = new HashMap<>();
    private Connection originConnection;
    private Connection destinyConnection;

    public CopyService(SqlServerConnection serverConnection, String[] originDatabase, String[] destinyDatabase) {
        this.serverConnection = serverConnection;
        this.originDatabase.put(SERVER_ADDRESS, originDatabase[0]);
        this.originDatabase.put(DATABASE, originDatabase[1]);
        this.originDatabase.put(USER_NAME, originDatabase[2]);
        this.originDatabase.put(PASSWORD, originDatabase[3]);
        this.destinyDatabase.put(SERVER_ADDRESS, destinyDatabase[0]);
        this.destinyDatabase.put(DATABASE, destinyDatabase[1]);
        this.destinyDatabase.put(USER_NAME, destinyDatabase[2]);
        this.destinyDatabase.put(PASSWORD, destinyDatabase[3]);
    }

    public void copyDataBetweenBases() {
        try {
            this.originConnection = serverConnection.getConnection (
                    originDatabase.get(SERVER_ADDRESS),
                    originDatabase.get(DATABASE),
                    originDatabase.get(USER_NAME),
                    originDatabase.get(PASSWORD)
            );
            this.destinyConnection = serverConnection.getConnection (
                    destinyDatabase.get(SERVER_ADDRESS),
                    destinyDatabase.get(DATABASE),
                    destinyDatabase.get(USER_NAME),
                    destinyDatabase.get(PASSWORD)
            );
            ResultSet resultSet = this.getOriginData("select * from tabela");
            while (resultSet.next()) {
                this.insertInDestiny(
                        "update tabela set campo = ? where id = ?",
                        resultSet.getString("CAMPO"),
                        resultSet.getInt("ID")
                );
            }
            this.serverConnection.releaseAllConnections();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertInDestiny(String insertSql, Object ... values) {
        try {
            PreparedStatement preparedStatement = this.destinyConnection.prepareStatement(insertSql);
            int paramIndex = 0;
            for (Object value : values) {
                preparedStatement.setObject(paramIndex++, value);
            }
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private ResultSet getOriginData(String selectSql) {
        try {
            PreparedStatement preparedStatement = this.originConnection.prepareStatement(selectSql);
            return preparedStatement.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
}
