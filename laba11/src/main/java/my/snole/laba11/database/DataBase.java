package my.snole.laba11.database;

import my.snole.laba11.model.ant.WarriorAnt;
import my.snole.laba11.model.ant.WorkerAnt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "mamba";

    public DataBase() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    public static void saveWorkerAnt(WorkerAnt ant) {
        String sql = "INSERT INTO worker_ants (birth_time, lifetime, birth_x, birth_y) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, ant.getBirthTime());
            stmt.setLong(2, ant.getLifetime());
            stmt.setDouble(3, ant.getBirthX());
            stmt.setDouble(4, ant.getBirthY());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveWarriorAnt(WarriorAnt ant) {
        String sql = "INSERT INTO warrior_ants (birth_time, lifetime, birth_x, birth_y, radius) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, ant.getBirthTime());
            stmt.setLong(2, ant.getLifetime());
            stmt.setDouble(3, ant.getBirthX());
            stmt.setDouble(4, ant.getBirthY());
            stmt.setInt(5, ant.getRadius());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<WorkerAnt> loadWorkerAnts() {
        List<WorkerAnt> workerAnts = new ArrayList<>();
        String sql = "SELECT * FROM worker_ants";

        long currentTime = System.currentTimeMillis();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                WorkerAnt ant = new WorkerAnt();
                ant.setBirthTime(rs.getLong("birth_time"));
                ant.setLifetime(rs.getLong("lifetime"));
                ant.setBirthPosition(rs.getDouble("birth_x"), rs.getDouble("birth_y"));

                long timeSinceSave = currentTime - rs.getLong("birth_time");
                ant.setBirthTime(ant.getBirthTime() + timeSinceSave);

                workerAnts.add(ant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workerAnts;
    }

    public static List<WarriorAnt> loadWarriorAnts() {
        List<WarriorAnt> warriorAnts = new ArrayList<>();
        String sql = "SELECT * FROM warrior_ants";

        long currentTime = System.currentTimeMillis();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                WarriorAnt ant = new WarriorAnt();
                ant.setBirthTime(rs.getLong("birth_time"));
                ant.setLifetime(rs.getLong("lifetime"));
                ant.setBirthPosition(rs.getDouble("birth_x"), rs.getDouble("birth_y"));
                ant.setRadius(rs.getInt("radius"));

                long timeSinceSave = currentTime - rs.getLong("birth_time");
                ant.setBirthTime(ant.getBirthTime() + timeSinceSave);

                warriorAnts.add(ant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return warriorAnts;
    }

    public static void clearTables() {
        String sql = "TRUNCATE TABLE worker_ants, warrior_ants";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
