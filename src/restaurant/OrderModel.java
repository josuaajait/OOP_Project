/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package restaurant;

/**
 *
 * @author CYBORG 15
 */
import java.util.HashMap;
import java.util.Map;

public class OrderModel {
    private int id;
    private int tableId;
    private Map<Integer, Integer> items = new HashMap<>(); // menuItemId -> qty

    public OrderModel() {}

    public OrderModel(int id, int tableId) {
        this.id = id;
        this.tableId = tableId;
    }

    // getter & setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public Map<Integer,Integer> getItems() { return items; }

    public void addItem(int menuItemId, int qty) {
        items.put(menuItemId, items.getOrDefault(menuItemId, 0) + qty);
    }
}
