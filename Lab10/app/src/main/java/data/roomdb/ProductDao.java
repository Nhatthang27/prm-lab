package data.roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
@Dao
public interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    LiveData<List<Product>> observeAll();

    @Insert long insert(Product p);

    @Query("DELETE FROM products")
    void deleteAll();
}
