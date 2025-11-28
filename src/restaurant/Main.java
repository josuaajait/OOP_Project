package restaurant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static RestaurantService service = new RestaurantService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

    // Admin
    service.ensureAdminExists();

    // login dan register
    Account currentUser = null;

    while (currentUser == null) {
        System.out.println("\n=== SISTEM LOGIN ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("Pilih: ");
        int p = readInt();

        switch (p) {
            case 1:
                currentUser = handleRegister();
                break;
            case 2:
                currentUser = handleLogin();
                break;
            default:
                System.out.println("Pilihan tidak ada.");
        }
    }

    System.out.println("\nSelamat datang, " + currentUser.getUsername());
    System.out.println();

    // Tampilkan menu sesuai role
    currentUser.showMenu();

    // Menu utama RESTORAN sesuai role
    if (currentUser instanceof Admin) {
        handleAdminMenu();
    } else if (currentUser instanceof user) {
        handleUserMenu();
    }
 }

    // Method menu admin
    private static void handleAdminMenu() {
    int pilihan;
    do {
        System.out.print("Pilih: ");
        pilihan = readInt();

        switch (pilihan) {
            case 1: // Tambah Menu
                System.out.print("Masukkan nama menu: ");
                String menuName = scanner.nextLine();
                System.out.print("Masukkan harga: ");
                double price = Double.parseDouble(scanner.nextLine());
                if (service.addMenuItem(menuName, price)) {
                    System.out.println("Menu berhasil ditambahkan!");
                } else {
                    System.out.println("Gagal menambahkan menu.");
                }
                break;
            case 2: // Tambah Meja
                System.out.print("Masukkan nomor meja: ");
                int tableNumber = readInt();
                System.out.print("Masukkan kapasitas meja: ");
                int capacity = readInt();
                if (service.addTable(tableNumber, capacity)) {
                    System.out.println("Meja berhasil ditambahkan!");
                } else {
                    System.out.println("Gagal menambahkan meja.");
                }
                break;
            case 3: // Lihat Pesanan / Reservasi
                service.printReservations();
                break;
            case 0:
                System.out.println("Logout...");
                break;
            default:
                System.out.println("Pilihan tidak valid.");
        }
    } while (pilihan != 0);
 }

    // Membaca double (untuk harga)
    private static double readDouble() {
    while (true) {
        try {
            String line = scanner.nextLine();
            if (line.isEmpty()) continue;
            return Double.parseDouble(line.trim());
        } catch (NumberFormatException ex) {
            System.out.print("Input angka desimal yang valid: ");
        }
    }
}

    // Method menu user/customer
    private static void handleUserMenu() {
    int pilihan;
    do {
        System.out.print("Pilih: ");
        pilihan = readInt();

        switch (pilihan) {
            case 1:
                handleReservation();
                break;
            case 2:
                handleOrder();
                break;
            case 3:
                service.printTableStatus();
                break;
            case 4:
                printMenu();
                break;
            case 5:
                service.printReservations();
                break;
            case 0:
                System.out.println("Logout...");
                break;
            default:
                System.out.println("Pilihan tidak valid.");
        }
    } while (pilihan != 0);
}

    // LOGIN & REGISTER
    private static Account handleRegister() {
        System.out.print("Buat Username: ");
        String user = scanner.nextLine();

        System.out.print("Buat Password: ");
        String pass = scanner.nextLine();

        System.out.print("Daftar sebagai (admin/user): ");
        String role = scanner.nextLine().toLowerCase();

        boolean ok = service.register(user, pass);
        if (!ok) {
            System.out.println("Username sudah dipakai, coba lagi.");
            return null;
        }

        System.out.println("Registrasi berhasil! Silakan login.");
        return null;
    }

    private static Account handleLogin() {
        System.out.print("Username: ");
        String user = scanner.nextLine();

        System.out.print("Password: ");
        String pass = scanner.nextLine();

        Account acc = service.login(user, pass);
        if (acc == null) {
            System.out.println("Username atau password salah.");
            return null;
        }

        return acc;
    }
    
    // fitur restoran
    private static void handleReservation() {
        System.out.print("Nama Pelanggan: ");
        String name = scanner.nextLine();
        System.out.print("Jumlah Tamu: ");
        int guests = readInt();
        System.out.print("Waktu Reservasi: ");
        String time = scanner.nextLine();

        TableModel table = service.findAvailableTable(guests);
        if (table == null) {
            System.out.println("Maaf, tidak ada meja tersedia untuk " + guests + " tamu.");
            return;
        }

        boolean ok = service.createReservation(name, guests, time, table.getId());
        if (ok) System.out.println("Reservasi berhasil pada " + table);
        else System.out.println("Reservasi gagal.");
    }

    private static void handleOrder() {
        System.out.print("Masukkan nomor meja (table_number): ");
        int tableNumber = readInt();

        List<TableModel> tables = service.getAllTables();
        TableModel chosen = null;
        for (TableModel t : tables) {
            if (t.getTableNumber() == tableNumber) {
                chosen = t;
                break;
            }
        }
        if (chosen == null) {
            System.out.println("Meja tidak ditemukan.");
            return;
        }

        Map<Integer,Integer> cart = new HashMap<>();
        int pilih;
        do {
            printMenu();
            System.out.print("Pilih Menu ID (0 selesai): ");
            pilih = readInt();
            if (pilih == 0) break;

            System.out.print("Jumlah: ");
            int qty = readInt();
            cart.put(pilih, cart.getOrDefault(pilih, 0) + qty);
            System.out.println("Ditambahkan ke cart.");
        } while (true);

        if (cart.isEmpty()) {
            System.out.println("Tidak ada item. Batal order.");
            return;
        }

        boolean ok = service.createOrder(chosen.getId(), cart);
        if (ok) {
            double total = 0;
            for (Map.Entry<Integer,Integer> e : cart.entrySet()) {
                double price = service.getMenuPrice(e.getKey());
                total += price * e.getValue();
            }
            System.out.println("Order berhasil. Total: Rp " + total);
        } else {
            System.out.println("Order gagal.");
        }
    }

    private static void printMenu() {
        List<MenuItem> menu = service.getAllMenu();
        System.out.println("=== Menu ===");
        for (MenuItem m : menu) {
            System.out.println(m.toString());
        }
    }

    private static int readInt() {
        while (true) {
            try {
                String line = scanner.nextLine();
                if (line.isEmpty()) continue;
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException ex) {
                System.out.print("Input angka yang valid: ");
            }
        }
    }
}
