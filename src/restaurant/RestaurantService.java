package restaurant;

import java.sql.*;
import java.util.*;

public class RestaurantService {
    private Map<String, Account> user = new HashMap<>();

    // Pastikan admin default ada di database
    public void ensureAdminExists() {
        String sqlCheck = "SELECT COUNT(*) FROM user WHERE role = 'admin'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCheck);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next() && rs.getInt(1) == 0) {
                // Tidak ada admin, buat admin default
                String sqlInsert = "INSERT INTO user (username, password, role) VALUES (?, ?, 'admin')";
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                    psInsert.setString(1, "admin");
                    psInsert.setString(2, "admin123");
                    psInsert.executeUpdate();
                    System.out.println("Admin default dibuat: username='admin', password='admin123'");
                }
            }

        } catch (SQLException ex) {
            System.err.println("Error ensureAdminExists: " + ex.getMessage());
        }
    }
   
        // Menambahkan meja baru
    public boolean addTable(int tableNumber, int capacity) {
    String sql = "INSERT INTO tables (table_number, capacity, status) VALUES (?, ?, 'Tersedia')";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, tableNumber);
        ps.setInt(2, capacity);
        ps.executeUpdate();
        return true;
    } catch (SQLException ex) {
        System.err.println("addTable gagal: " + ex.getMessage());
        return false;
    }
 }

    // Menambahkan menu baru
    public boolean addMenuItem(String name, double price) {
    String sql = "INSERT INTO menu_items (name, price) VALUES (?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, name);
        ps.setDouble(2, price);
        ps.executeUpdate();
        return true;
    } catch (SQLException ex) {
        System.err.println("addMenuItem gagal: " + ex.getMessage());
        return false;
    }
}

    // REGISTER USER BARU (hanya user biasa)
    public boolean register(String username, String password) {
        String role = "user"; // role default untuk register
        String sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("Register gagal: " + ex.getMessage());
            return false;
        }
    }

    // LOGIN USER / ADMIN
    public Account login(String username, String password) {
        String sql = "SELECT username, password, role FROM user WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPass = rs.getString("password");
                String role = rs.getString("role");

                if (dbPass.equals(password)) {
                    if (role.equalsIgnoreCase("admin")) return new Admin(username, password);
                    else return new user(username, password);
                } else {
                    System.err.println("Password salah!");
                }
            } else {
                System.err.println("Username tidak ditemukan!");
            }

        } catch (SQLException ex) {
            System.err.println("Login error: " + ex.getMessage());
        }

        return null;
    }

    // === FUNGSI RESTAURANT YANG SUDAH ADA ===
    public List<TableModel> getAllTables() {
        List<TableModel> list = new ArrayList<>();
        String sql = "SELECT id, table_number, capacity, status FROM tables ORDER BY table_number";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TableModel(rs.getInt("id"), rs.getInt("table_number"),
                        rs.getInt("capacity"), rs.getString("status")));
            }
        } catch (SQLException ex) {
            System.err.println("getAllTables error: " + ex.getMessage());
        }
        return list;
    }

    public List<MenuItem> getAllMenu() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT id, name, price FROM menu_items ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new MenuItem(rs.getInt("id"), rs.getString("name"), rs.getInt("price")));
            }
        } catch (SQLException ex) {
            System.err.println("getAllMenu error: " + ex.getMessage());
        }
        return list;
    }

    public TableModel findAvailableTable(int guests) {
        String sql = "SELECT id, table_number, capacity, status FROM tables WHERE status = 'Tersedia' AND capacity >= ? ORDER BY capacity ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guests);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TableModel(rs.getInt("id"), rs.getInt("table_number"), rs.getInt("capacity"), rs.getString("status"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("findAvailableTable error: " + ex.getMessage());
        }
        return null;
    }

    public boolean createReservation(String customerName, int guests, String time, int tableId) {
        String insertRes = "INSERT INTO reservations (customer_name, guests, time, table_id) VALUES (?, ?, ?, ?)";
        String updateTable = "UPDATE tables SET status = 'Dipesan' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(insertRes);
                 PreparedStatement ps2 = conn.prepareStatement(updateTable)) {

                ps1.setString(1, customerName);
                ps1.setInt(2, guests);
                ps1.setString(3, time);
                ps1.setInt(4, tableId);
                ps1.executeUpdate();

                ps2.setInt(1, tableId);
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("createReservation rollback: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.err.println("createReservation conn error: " + ex.getMessage());
        }
        return false;
    }

    public boolean createOrder(int tableId, Map<Integer,Integer> items) {
        String insertOrder = "INSERT INTO orders (table_id) VALUES (?)";
        String insertOrderItem = "INSERT INTO order_items (order_id, menu_item_id, qty) VALUES (?, ?, ?)";
        String updateTable = "UPDATE tables SET status = 'Terisi' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            int orderId = -1;
            try (PreparedStatement psOrder = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {

                psOrder.setInt(1, tableId);
                psOrder.executeUpdate();
                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    if (keys.next()) orderId = keys.getInt(1);
                }

                try (PreparedStatement psItem = conn.prepareStatement(insertOrderItem)) {
                    for (Map.Entry<Integer,Integer> e : items.entrySet()) {
                        psItem.setInt(1, orderId);
                        psItem.setInt(2, e.getKey());
                        psItem.setInt(3, e.getValue());
                        psItem.addBatch();
                    }
                    psItem.executeBatch();
                }

                try (PreparedStatement psUpdate = conn.prepareStatement(updateTable)) {
                    psUpdate.setInt(1, tableId);
                    psUpdate.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("createOrder rollback: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.err.println("createOrder conn error: " + ex.getMessage());
        }
        return false;
    }

    public void printTableStatus() {
        List<TableModel> tables = getAllTables();
        System.out.println("=== Status Meja ===");
        for (TableModel t : tables) {
            System.out.println(t);
        }
    }

    public double getMenuPrice(int menuId) {
        String sql = "SELECT price FROM menu_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("price");
            }
        } catch (SQLException ex) {
            System.err.println("getMenuPrice error: " + ex.getMessage());
        }
        return 0;
    }

    public void printReservations() {
        String sql = "SELECT r.id, r.customer_name, r.guests, r.time, t.table_number FROM reservations r JOIN tables t ON r.table_id = t.id ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("=== Reservasi ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt(1) + ", Nama: " + rs.getString(2) + ", Tamu: " + rs.getInt(3)
                        + ", Waktu: " + rs.getString(4) + ", Meja: " + rs.getInt(5));
            }
        } catch (SQLException ex) {
            System.err.println("printReservations error: " + ex.getMessage());
        }
    }
}

