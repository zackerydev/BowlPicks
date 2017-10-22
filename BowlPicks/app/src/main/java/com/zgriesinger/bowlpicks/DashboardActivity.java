package com.zgriesinger.bowlpicks;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    public String league;
    public void displayRow(User user){
        TableLayout pickTable = (TableLayout)findViewById(R.id.pick_table);
        TableRow row = new TableRow(this);
        TextView name = new TextView(this);
        name.setText(user.displayName);
        TableRow.LayoutParams textParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(20, 1, 1 ,1);
        row.setLayoutParams(textParams);
        name.setTextSize(20);
        row.addView(name);
        for(int i = 0; i < user.picks.size(); i++) {
            TextView pick = new TextView(this);
            pick.setText(user.picks.get(i));
            pick.setTextSize(20);
            row.addView(pick);
        }
        pickTable.addView(row);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar dashToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(dashToolbar);
        Button joinLeague = new Button(this);
        joinLeague.setText("Join League");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.gravity = Gravity.RIGHT;
        joinLeague.setLayoutParams(params);

        Button createLeague = new Button(this);
        createLeague.setText("Create League");
        createLeague.setLayoutParams(params);

        dashToolbar.addView(joinLeague);
        dashToolbar.addView(createLeague);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query allPicks = database.getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            DatabaseReference users = database.getReference("Users").child(user.getUid());
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user  = dataSnapshot.getValue(User.class);
                    league = user.league;
                    if (league != null) {
                        TextView leagueName = new TextView(DashboardActivity.this);
                        leagueName.setText(league + " Leaderboard:");
                        leagueName.setTextSize(24);
                        leagueName.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT));
                        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.header);
                        headerLayout.addView(leagueName);
                    } else {
                        league = "";
                        TextView noPlayerError = new TextView(DashboardActivity.this);
                        noPlayerError.setText("Please create or join a league to see the people you are playing against!");
                        noPlayerError.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT));
                        noPlayerError.setTextSize(24);
                        noPlayerError.setTextColor(getResources().getColor(R.color.errorred));
                        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.header);
                        headerLayout.addView(noPlayerError);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        allPicks.orderByChild("displayName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if(user.league.equals(league)) {
                        displayRow(user);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
