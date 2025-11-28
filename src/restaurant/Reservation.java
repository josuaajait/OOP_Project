/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package restaurant;

/**
 *
 * @author CYBORG 15
 */
public class Reservation {
    private int id;
    private String customerName;
    private int guests;
    private String time;
    private int tableId;

    public Reservation() {}

    public Reservation(int id, String customerName, int guests, String time, int tableId) {
        this.id = id;
        this.customerName = customerName;
        this.guests = guests;
        this.time = time;
        this.tableId = tableId;
    }

    // getter & setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public int getGuests() { return guests; }
    public void setGuests(int guests) { this.guests = guests; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    @Override
    public String toString() {
        return "Reservasi: " + customerName + ", Tamu: " + guests + ", Waktu: " + time + ", Table ID: " + tableId;
    }
}
