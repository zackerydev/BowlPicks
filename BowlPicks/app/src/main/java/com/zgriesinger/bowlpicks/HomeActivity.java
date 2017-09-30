package com.zgriesinger.bowlpicks;

import android.app.ActionBar;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public void displayPickList(ArrayList<Game> bowlGames) {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.home);

        Collections.sort(bowlGames);

        layout.removeAllViews();
        layout.removeAllViewsInLayout();
        LinearLayout outerLL = new LinearLayout(this);
        outerLL.setOrientation(LinearLayout.VERTICAL);
        outerLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        outerLL.setPadding(30,30,30,30);
        outerLL.setGravity(Gravity.CENTER);

        layout.addView(outerLL);

        for(int i = 0; i < bowlGames.size(); i++) {
            TextView bowlName = new TextView(this);
            bowlName.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            bowlName.setText(bowlGames.get(i).name);
            outerLL.addView(bowlName);

            LinearLayout innerLL = new LinearLayout(this);
            innerLL.setOrientation(LinearLayout.HORIZONTAL);
            innerLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            innerLL.setGravity(Gravity.CENTER);

            RadioGroup radioG = new RadioGroup(this);
            radioG.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            radioG.setOrientation(RadioGroup.HORIZONTAL);

            RadioButton homeTeam = new RadioButton(this);
            homeTeam.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            homeTeam.setText(bowlGames.get(i).home);
            homeTeam.setTextSize(25);
            homeTeam.setPadding(10,10,10,10);
            radioG.addView(homeTeam);

            TextView vs = new TextView(this);
            vs.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            vs.setText(R.string.vs);
            vs.setTextSize(25);
            radioG.addView(vs);


            RadioButton awayTeam = new RadioButton(this);
            awayTeam.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            awayTeam.setText(bowlGames.get(i).away);
            awayTeam.setTextSize(25);
            awayTeam.setPadding(10,10,10,10);
            radioG.addView(awayTeam);

            innerLL.addView(radioG);
            outerLL.addView(innerLL);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final ArrayList<Game> bowlGames = new ArrayList();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query gameRef = database.getReference("Games");
        gameRef.orderByChild("order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Map<String, String>> map = (Map)dataSnapshot.getValue();
                //Iterator it = map.entrySet().iterator();
                for (String key : map.keySet()) {
                    int gameOrder;
                    if(map.get(key).get("order") != null){
                        gameOrder = Integer.parseInt(map.get(key).get("order"));
                    } else {
                        gameOrder = 999;
                    }
                    Game game = new Game(map.get(key).get("home"), map.get(key).get("away"), map.get(key).get("name"), gameOrder);
                    bowlGames.add(game);
                }

                displayPickList(bowlGames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
