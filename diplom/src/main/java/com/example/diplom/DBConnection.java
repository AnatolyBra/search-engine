package com.example.diplom;

import com.example.diplom.model.Index;
import com.example.diplom.model.Lemma;
import com.example.diplom.model.Page;

import java.sql.*;

public class DBConnection {
    private static Connection connection;

    private static final String dbName = "jdbc:mysql://localhost:3306/search_engine";
    private static final String dbUser = "root";
    private static final String dbPass = "testtest";

    private static StringBuilder insertQuery = new StringBuilder();

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(dbName, dbUser, dbPass);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
    /**
     * метод executeInsertPage() заполняет таблицу _page
     * **/
    public static void executeInsertPage(int code, String content, String path) throws SQLException {
        String sql = "INSERT INTO `_page`(`code`, `content`, `path`) VALUES(?,?,?)";

        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, code);
        ps.setString(2, content);
        ps.setString(3, path);
        ps.addBatch();
        ps.executeBatch();

    }

    /**
     * метод executeInsertLemma() заполняет таблицу _lemma
     * **/
    public static void executeInsertLemma(String lemma, int frequency) throws SQLException {
        String sql = "INSERT INTO `_lemma`(`lemma`,`frequency`) " +
                    "VALUES(?,?) ON DUPLICATE KEY UPDATE `frequency`=`frequency` + 1";

        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setString(1, lemma);
        ps.setInt(2, frequency);
        ps.addBatch();
        ps.executeBatch();
    }
    /**
     * метод calculateRank() пока не ясно
     * беру id от lemma и ставлю в lemma_id - реализовано
     * беру id от page и ставлю в page_id - нужно применить условие к текущей странице - реализовано
     * Rank лучше рассчитывать при добавлении леммы в таблицу.
     * **/
    public static void calculateRank(String path, int countTitle, int countBody) throws SQLException {
        String sqlLemma = "SELECT `id`, `frequency` FROM _lemma";
        Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sqlLemma);
        Index index = new Index();
        while (resultSet.next()){
            int id = resultSet.getInt(1);
            int frequency = resultSet.getInt(2);
            index.setLemmaId(id);
        }
        String sqlPage = "SELECT `id` FROM _page WHERE `path`='"+path+"'";
        ResultSet resultSetPage = statement.executeQuery(sqlPage);
        while (resultSetPage.next()){
            int id = resultSetPage.getInt(1);
            index.setPageId(id);
        }
        float rank =0;

        rank = (float) ( countTitle + 0.8 * countBody);

        String sqlInsert = "INSERT INTO _index(`page_id`,`lemma_id`,`rank`) VALUES(?,?,?)";
        PreparedStatement ps = getConnection().prepareStatement(sqlInsert);
        ps.setInt(1, index.getLemmaId());
        ps.setInt(2, index.getPageId());
        ps.setFloat(3, rank);
        ps.addBatch();
        ps.executeBatch();
    }

    public static void executeSelector(String name, String selector) throws SQLException {
        double weight;
        String sql = "INSERT INTO _field(name, selector, weight) VALUES(?,?,?)";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, selector);
        if(name.equals("title")){
            weight = 1.0;
        } else weight = 0.8;
        ps.setDouble(3, weight);
        ps.addBatch();
        ps.executeBatch();
    }
}








