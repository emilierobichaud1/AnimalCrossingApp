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
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

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
    Map<String, Villager> villager_dict = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost=findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1=tabHost.newTabSpec("My Villagers");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("", getResources().getDrawable(R.drawable.villager_list));

        TabHost.TabSpec spec2=tabHost.newTabSpec("Home");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("", getResources().getDrawable(R.drawable.house));

        TabHost.TabSpec spec3=tabHost.newTabSpec("Dream Villagers");
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
        ac_logo = findViewById(R.id.logo);
        frame = findViewById(R.id.frame);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);


        String item = parent.getItemAtPosition(position).toString();
        personality.setText("Personality: " + villager_dict.get(item).getPersonality());
        species.setText("Species: " + villager_dict.get(item).getSpecies());
        birthday.setText("Birthday: " + villager_dict.get(item).getBirthday());
        catchphrase.setText("Catchphrase: " + villager_dict.get(item).getCatchphrase());
        ac_logo.setVisibility(View.GONE);
        frame.setVisibility(View.VISIBLE);
        Picasso.get().load(villager_dict.get(item).getPic_link()).resize(380, 380).into(villager_pic);
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

    public Map<String,Villager> getDict(List<Villager> all_villagers) {
        Map<String,Villager> villager_dict = new HashMap<>();
        for(int i=0;i<all_villagers.size();i++) {
            villager_dict.put(all_villagers.get(i).getName(), all_villagers.get(i));
        }
        return villager_dict;
    }
}
