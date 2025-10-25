package data.roomdb;


import android.content.Context;
import androidx.room.*;
@Database(entities = {Product.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase {
    private static volatile AppDb INSTANCE;
    public abstract ProductDao productDao();

    public static AppDb get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                                    AppDb.class, "app.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}