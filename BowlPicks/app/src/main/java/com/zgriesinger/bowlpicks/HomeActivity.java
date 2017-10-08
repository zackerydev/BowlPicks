package com.zgriesinger.bowlpicks;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public void showDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
    public void submitPicks(ArrayList<String> pickList) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Users").child(user.getUid()).child("picks").setValue(pickList);

        showDashboard();
    }

    public void displayPickList(ArrayList<Game> bowlGames) {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.home);
        Collections.sort(bowlGames);

        LinearLayout outerLL = (LinearLayout)findViewById(R.id.picks);

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
            radioG.setId(100+i);
            radioG.setOrientation(RadioGroup.HORIZONTAL);

            RadioButton homeTeam = new RadioButton(this);
            homeTeam.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            homeTeam.setId(10 + i);
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
            awayTeam.setId(20 + i);
            awayTeam.setText(bowlGames.get(i).away);
            awayTeam.setTextSize(25);
            awayTeam.setPadding(10,10,10,10);
            radioG.addView(awayTeam);

            innerLL.addView(radioG);
            outerLL.addView(innerLL);

        }

        Button submit = new Button(this);
        submit.setText(R.string.submit);
        submit.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        outerLL.addView(submit);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<String> gameArray = new ArrayList();
                for(int j = 0; j < 3; j++) {
                    RadioGroup rg = (RadioGroup)findViewById(100 + j);
                    RadioButton home = (RadioButton)findViewById(10 + j);
                    RadioButton away = (RadioButton)findViewById(20 + j);
                    if(home.isChecked()) {
                        gameArray.add(home.getText().toString());
                    } else if(away.isChecked()) {
                        gameArray.add(away.getText().toString());
                    } else {
                        home.setTextColor(Color.parseColor("#FF0000"));
                        away.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
                submitPicks(gameArray);
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);
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
