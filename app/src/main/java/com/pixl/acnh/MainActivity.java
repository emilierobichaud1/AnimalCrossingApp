package com.pixl.acnh;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
    private static List<Villager> my_villagers = new ArrayList<>();
    private static List<Villager> my_fave_villagers = new ArrayList<>();

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "myprefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //get the 'My Villagers' and 'Fave Villagers' that already exist in sharedpreferences
        List<Villager> my_villager_list;
        my_villager_list = new Gson().fromJson(sharedpreferences.getString("myVillagers", null),
                new TypeToken<List<Villager>>() {
        }.getType());
        if(my_villagers.size()>0) {
            my_villagers.addAll(my_villager_list);
        }
        List<Villager> my_faves_list;
        my_faves_list = new Gson().fromJson(sharedpreferences.getString("myFaves", null),
                new TypeToken<List<Villager>>() {
        }.getType());
        if(my_fave_villagers.size()>0) {
            my_villagers.addAll(my_villager_list);
        }

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

        setSavedPreferences();
    }

    @Override
    public void onStop() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        super.onStop();
        if (my_villagers.size() > 0) {
            editor.putString("myVillagers", new Gson().toJson(my_villagers)).apply();
        }
        if (my_fave_villagers.size() > 0) {
            editor.putString("myFaves", new Gson().toJson(my_fave_villagers)).apply();
        }
    }

    @Override
    public void onPause() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        super.onPause();
        if (my_villagers.size() > 0) {
            editor.putString("myVillagers", new Gson().toJson(my_villagers)).apply();
        }
        if (my_fave_villagers.size() > 0) {
            editor.putString("myFaves", new Gson().toJson(my_fave_villagers)).apply();
        }
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
                    my_villagers.add(villager_dict.get(item));
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
                my_villagers.remove(villager_dict.get(item));
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
                my_fave_villagers.add(villager_dict.get(item));
            }
        });

        remove_favorites_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_favorites_button.setVisibility(View.VISIBLE);
                remove_favorites_button.setVisibility(View.INVISIBLE);
                fave_grid.removeView(fave_grid.findViewById(item.hashCode()));
                my_fave_villagers.remove(villager_dict.get(item));
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

    public void setSavedPreferences() {
        if (sharedpreferences.contains("myVillagers")) {
            List<Villager> my_villager_list;
            my_villager_list = new Gson().fromJson(sharedpreferences.getString("myVillagers", null), new TypeToken<List<Villager>>() {
            }.getType());
            for (int i = 0; i < my_villager_list.size(); i++) {
                ImageView villager = new ImageView(getApplicationContext());
                villager.setPadding(25, 25, 25, 25);
                villager.setId(my_villager_list.get(i).getName().hashCode());
                Picasso.get().load(my_villager_list.get(i).getPic_link()).resize(380, 380).into(villager);
                grid_view.addView(villager);
            }
        }
        if (sharedpreferences.contains("myFaves")) {
            List<Villager> my_faves_list;
            my_faves_list = new Gson().fromJson(sharedpreferences.getString("myFaves", null), new TypeToken<List<Villager>>() {
            }.getType());
            for (int i = 0; i < my_faves_list.size(); i++) {
                ImageView villager = new ImageView(getApplicationContext());
                villager.setPadding(25, 25, 25, 25);
                villager.setId(my_faves_list.get(i).getName().hashCode());
                Picasso.get().load(my_faves_list.get(i).getPic_link()).resize(380, 380).into(villager);
                fave_grid.addView(villager);
            }
        }
    }
}
