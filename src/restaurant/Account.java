/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package restaurant;

public abstract class Account {
    protected String username;
    protected String password;
    protected String role;

    public Account(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public abstract void showMenu();

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }
}

// ====================== ADMIN ======================
class Admin extends Account {

    public Admin(String username, String password) {
        super(username, password, "admin");
    }

    @Override
    public void showMenu() {
        System.out.println("\n=== MENU ADMIN ===");
        System.out.println("1. Tambah Menu");
        System.out.println("2. Tambah Meja");
        System.out.println("3. Lihat Pesanan");
        System.out.println("0. Keluar");
    }
}

// ====================== CUSTOMER ======================
class user extends Account {

    public user (String username, String password) {
        super(username, password, "user");
    }

    @Override
    public void showMenu() {
            System.out.println("\n=== Menu User(Aplikasi Manajemen Restoran) ===");
            System.out.println("1. Reservasi Meja");
            System.out.println("2. Pemesanan Menu (buat order)");
            System.out.println("3. Lihat Status Meja");
            System.out.println("4. Lihat Menu");
            System.out.println("5. Lihat Reservasi");
            System.out.println("0. Keluar");
            
   }
}


