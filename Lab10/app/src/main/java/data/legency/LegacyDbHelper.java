package data.legency;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LegacyDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "legacy.db";
    private static final int DB_VER = 1;

    public LegacyDbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VER);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products_legacy(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "price REAL NOT NULL)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS products_legacy");
        onCreate(db);
    }

    public long insert(String name, double price){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("price", price);
        return db.insert("products_legacy", null, cv);
    }

    public List<ProductLegacy> getAll(){
        ArrayList<ProductLegacy> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id,name,price FROM products_legacy ORDER BY id DESC", null);
        try {
            while (c.moveToNext()){
                list.add(new ProductLegacy(
                        c.getLong(0),
                        c.getString(1),
                        c.getDouble(2)
                ));
            }
        } finally {
            c.close();
        }
        return list;
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("ProductLegacy", null, null);
        db.close();
    }
}