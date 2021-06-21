package com.example.dddg.ui.dashboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dddg.DBHelper;
import com.example.dddg.MyCursorAdapter;
import com.example.dddg.NaverItem;
import com.example.dddg.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public static Context context_db;

    DBHelper helper;
    SQLiteDatabase db;
    NaverItem NI = new NaverItem();
    Button delbtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ListView listView = (ListView) root.findViewById(R.id.listView);

        helper = new DBHelper(getActivity(), "dog.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);


        String sql = "select * from dog;";
        Cursor c = db.rawQuery(sql, null);
        String[] strs = new String[]{"title", "image", "summary"};
        int[] ints = new int[]{R.id.title, R.id.image, R.id.summary};

        MyCursorAdapter adapter = null;
        adapter = new MyCursorAdapter(listView.getContext(), R.layout.db_list, c, strs, ints);

        listView.setAdapter(adapter);

        return root;
    }
}