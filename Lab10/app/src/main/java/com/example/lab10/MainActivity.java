package com.example.lab10;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import com.example.lab10.R;
import java.util.ArrayList;
import java.util.List;

import data.legency.LegacyDbHelper;
import data.legency.ProductLegacy;
import data.roomdb.AppDb;
import data.roomdb.Product;
import data.roomdb.ProductDao;


public class MainActivity extends AppCompatActivity {
    private Switch swSource;
    private Button btnAddRoom;
    private Button btnAddLegacy;
    private Button btnClearAll;
    private ListView lv;

    private ArrayAdapter<String> adapter;
    private List<Product> lastRoom = new ArrayList<>();
    private final List<String> rows = new ArrayList<>();

    private ProductDao dao;
    private LegacyDbHelper legacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swSource     = findViewById(R.id.swSource);
        btnAddRoom   = findViewById(R.id.btnAddRoom);
        btnAddLegacy = findViewById(R.id.btnAddLegacy);
        btnClearAll  = findViewById(R.id.btnClearAll);
        lv           = findViewById(R.id.lv);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rows);
        lv.setAdapter(adapter);

        dao    = AppDb.get(this).productDao();
        legacy = new LegacyDbHelper(this);

        dao.observeAll().observe(this, products -> {
            lastRoom = (products != null) ? products : new ArrayList<>();
            if (!isLegacySelected()) setListFromLocal(lastRoom);
        });

        btnAddRoom.setOnClickListener(v -> {
            if (isLegacySelected()) return;
            new Thread(() -> dao.insert(new Product("Coffee", 2.5))).start();
        });

        btnAddLegacy.setOnClickListener(v -> {
            if (!isLegacySelected()) return;
            new Thread(() -> {
                legacy.insert("Tea", 1.8);
                runOnUiThread(this::loadLegacy);
            }).start();
        });

        btnClearAll.setOnClickListener(v -> new Thread(() -> {
            if (isLegacySelected()) {
                legacy.deleteAll();
                runOnUiThread(this::loadLegacy);
            } else {
                dao.deleteAll();
                runOnUiThread(() -> setListFromLocal(dao.observeAll().getValue()));
            }
        }).start());

        swSource.setOnCheckedChangeListener((btn, isLegacy) -> {
            updateUiForSource(isLegacy);
            if (isLegacy) runOnUiThread(this::loadLegacy);
            else setListFromLocal(lastRoom);
        });

        swSource.setChecked(false);
        updateUiForSource(false);
    }

    private boolean isLegacySelected() {
        return swSource.isChecked();
    }

    private void updateUiForSource(boolean isLegacy) {
        btnAddLegacy.setEnabled(isLegacy);
        btnAddRoom.setEnabled(!isLegacy);
        btnClearAll.setText(isLegacy ? "Clear Legacy" : "Clear Room");
    }

    private void setListFromLocal(List<Product> list) {
        rows.clear();
        if (list != null && !list.isEmpty()) {
            for (Product p : list) rows.add("#" + p.id + " - " + p.name + " - $" + p.price);
        } else {
            rows.add("(Local empty)");
        }
        adapter.notifyDataSetChanged();
    }

    private void loadLegacy() {
        new Thread(() -> {
            List<ProductLegacy> data = legacy.getAll();
            rows.clear();
            if (data != null && !data.isEmpty()) {
                for (ProductLegacy d : data) {
                    rows.add("#" + d.id + " - " + d.name + " - $" + d.price);
                }
            } else rows.add("(Legacy empty)");
            runOnUiThread(adapter::notifyDataSetChanged);
        }).start();
    }
}