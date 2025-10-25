package data.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
}
