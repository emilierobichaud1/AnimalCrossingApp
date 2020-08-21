package com.pixl.acnh;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    AutoCompleteTextView villager_name;
    TextView personality;
    TextView species;
    TextView birthday;
    TextView catchphrase;
    ImageView villager_pic;
    ImageView ac_logo;
    ImageView frame;
    Button my_favorites_button;
    Button my_list_button;
    Button remove_favorites_button;
    Button remove_list_button;
    GridLayout grid_view;
    GridLayout fave_grid;
    Map<String, Villager> villager_dict = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = findViewById(R.id.tabhost);
        tabHost.setup();

        //tab 1
        TabHost.TabSpec spec1 = tabHost.newTabSpec("My Villagers");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("", getResources().getDrawable(R.drawable.villager_list));

        //tab 2
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Home");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("", getResources().getDrawable(R.drawable.house));

        //tab 3
        TabHost.TabSpec spec3 = tabHost.newTabSpec("Dream Villagers");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("", getResources().getDrawable(R.drawable.star));

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        tabHost.setCurrentTab(1);

        personality = findViewById(R.id.personality);
        species = findViewById(R.id.species);
        birthday = findViewById(R.id.birthday);
        catchphrase = findViewById(R.id.catchphrase);
        villager_pic = findViewById(R.id.villager_pic);
        villager_name = findViewById(R.id.villager_name);
        my_favorites_button = findViewById(R.id.my_favorites_button);
        my_list_button = findViewById(R.id.my_list_button);
        remove_favorites_button = findViewById(R.id.remove_favorites_button);
        remove_list_button = findViewById(R.id.remove_list_button);
        ac_logo = findViewById(R.id.logo);
        frame = findViewById(R.id.frame);

        grid_view = findViewById(R.id.grid);
        grid_view.setColumnCount(2);
        grid_view.setRowCount(5);

        fave_grid = findViewById(R.id.fave_grid);
        fave_grid.setColumnCount(2);

        final String[] villager_names = new String[392];
        List<Villager> all_villagers = null;

        try {
            all_villagers = handleCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (all_villagers != null) {
            for (int i = 0; i < all_villagers.size(); i++) {
                villager_names[i] = all_villagers.get(i).getName();
            }
        }

        villager_dict = getDict(all_villagers);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_custom, villager_names);
        villager_name.setAdapter(adapter);
        villager_name.setOnItemClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);


        final String item = parent.getItemAtPosition(position).toString();
        personality.setText("Personality: " + villager_dict.get(item).getPersonality());
        species.setText("Species: " + villager_dict.get(item).getSpecies());
        birthday.setText("Birthday: " + villager_dict.get(item).getBirthday());
        catchphrase.setText("Catchphrase: " + villager_dict.get(item).getCatchphrase());
        ac_logo.setVisibility(View.GONE);
        frame.setVisibility(View.VISIBLE);
        my_favorites_button.setVisibility(View.VISIBLE);
        my_list_button.setVisibility(View.VISIBLE);
        Picasso.get().load(villager_dict.get(item).getPic_link()).resize(380, 380).into(villager_pic);

        if (grid_view.findViewById(item.hashCode()) == null) {
            my_list_button.setVisibility(View.VISIBLE);
            remove_list_button.setVisibility(View.INVISIBLE);
        }

        if (fave_grid.findViewById(item.hashCode()) == null) {
            my_favorites_button.setVisibility(View.VISIBLE);
            remove_favorites_button.setVisibility(View.INVISIBLE);
        }

        my_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid_view.getChildCount() < 10) {
                    ImageView villager = new ImageView(getApplicationContext());
                    villager.setPadding(25, 25, 25, 25);
                    villager.setId(item.hashCode());
                    Picasso.get().load(villager_dict.get(item).getPic_link()).resize(380, 380).into(villager);
                    grid_view.addView(villager);
                    my_list_button.setVisibility(View.INVISIBLE);
                    remove_list_button.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Oops! You can only have 10 villagers. " +
                            "Remove from your list in order to add more!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        remove_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_list_button.setVisibility(View.VISIBLE);
                remove_list_button.setVisibility(View.INVISIBLE);
                grid_view.removeView(grid_view.findViewById(item.hashCode()));
            }
        });

        my_favorites_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView dream_villager = new ImageView(getApplicationContext());
                dream_villager.setPadding(25, 25, 25, 25);
                dream_villager.setId(item.hashCode());
                Picasso.get().load(villager_dict.get(item).getPic_link()).resize(380, 380).into(dream_villager);
                fave_grid.addView(dream_villager);
                my_favorites_button.setVisibility(View.INVISIBLE);
                remove_favorites_button.setVisibility(View.VISIBLE);
            }
        });

        remove_favorites_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_favorites_button.setVisibility(View.VISIBLE);
                remove_favorites_button.setVisibility(View.INVISIBLE);
                fave_grid.removeView(fave_grid.findViewById(item.hashCode()));
            }
        });
    }


    public List<Villager> handleCSV() throws IOException {
        List<Villager> all_villagers = new ArrayList<>();
        BufferedReader csv_reader = new BufferedReader(new InputStreamReader(getAssets().open("acnh.csv")));

        String row;
        while ((row = csv_reader.readLine()) != null) {
            String[] data = row.split(",");
            Villager villager = new Villager(data[0], data[2], data[3], data[4], data[5].replace("\"\"", ""), data[1]);
            all_villagers.add(villager);
        }
        csv_reader.close();
        return all_villagers;
    }

    public Map<String, Villager> getDict(List<Villager> all_villagers) {
        Map<String, Villager> villager_dict = new HashMap<>();
        for (int i = 0; i < all_villagers.size(); i++) {
            villager_dict.put(all_villagers.get(i).getName(), all_villagers.get(i));
        }
        return villager_dict;
    }
}
