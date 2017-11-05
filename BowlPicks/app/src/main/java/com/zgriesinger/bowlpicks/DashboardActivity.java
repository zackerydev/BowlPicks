package com.zgriesinger.bowlpicks;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

import static java.lang.Integer.parseInt;

final class League {
    public String name;

    public League(String _name) {
        this.name = _name;
    }

    public League() {
    }
}

public class DashboardActivity extends AppCompatActivity {
    public String league;

    public void validateCreate(String leagueName) {
        final String lname = leagueName;
        final String err = "";
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final FirebaseUser loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        Query allPicks = db.getReference("Users");
        allPicks.orderByChild("league").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if(user.league == lname) {
                        err.concat("League already exists!");
                        break;
                    }
                }
                if(err == "") {
                    DatabaseReference users = db.getReference("Users").child(loggedUser.getUid());
                    users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.league = lname;
                            DatabaseReference dr = db.getReference();
                            dr.child("Users").child(loggedUser.getUid()).setValue(user);
                            finish();
                            startActivity(getIntent());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void displayRow(User user){
        final ArrayList<String> picks = user.picks;
        final String displayName = user.displayName;

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Winners").addValueEventListener(new ValueEventListener() {
            int score;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String winners = dataSnapshot.getValue().toString();
                String [] splitWinners = winners.split("\\|");
                for(int i = 0; i < splitWinners.length; i++) {
                    String [] teamPoints = splitWinners[i].split("\\,");
                    if(teamPoints[0].equals(picks.get(i))) {
                        score += parseInt(teamPoints[1]);
                    }
                }
                TableLayout pickTable = (TableLayout)findViewById(R.id.pick_table);
                TableRow row = new TableRow(DashboardActivity.this);
                TextView name = new TextView(DashboardActivity.this);
                name.setText(displayName);
                TableRow.LayoutParams textParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(20, 1, 1 ,1);
                row.setLayoutParams(textParams);
                name.setTextSize(20);
                row.addView(name);
                TextView scoreTV = new TextView(DashboardActivity.this);
                scoreTV.setLayoutParams(textParams);
                scoreTV.setText(Integer.toString(score));
                row.addView(scoreTV);
                for(int j = 0; j < picks.size(); j++) {
                    TextView pick = new TextView(DashboardActivity.this);
                    pick.setText(picks.get(j));
                    pick.setTextSize(20);
                    row.addView(pick);
                }
                pickTable.addView(row);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar dashToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(dashToolbar);


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
                        //headerLayout.addView(leagueName);
                    } else {
                        Toolbar dashToolbar = (Toolbar) findViewById(R.id.toolbar);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                        params.gravity = Gravity.RIGHT;

                        Button createLeague = new Button(DashboardActivity.this);
                        createLeague.setText("Create League");
                        createLeague.setLayoutParams(params);

                        createLeague.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v){
                                final EditText createLeague = new EditText(DashboardActivity.this);

                                LinearLayout header = (LinearLayout) findViewById(R.id.header);
                                header.removeAllViews();
                                createLeague.setHint("Type Here to Create or Join a League!:");
                                Button submitCreate = new Button(DashboardActivity.this);
                                submitCreate.setText("Join");
                                submitCreate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String leagueName = createLeague.getText().toString();
                                        validateCreate(leagueName);
                                    }
                                });
                                createLeague.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT));
                                submitCreate.setLayoutParams(new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));
                                header.addView(createLeague);
                                header.addView(submitCreate);

                            }
                        });


                        dashToolbar.addView(createLeague);
                        league = "";
                        TextView noPlayerError = new TextView(DashboardActivity.this);
                        noPlayerError.setText("Please create or join a league to see the people you are playing against!");
                        noPlayerError.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT));
                        noPlayerError.setTextSize(24);
                        noPlayerError.setTextColor(getResources().getColor(R.color.errorred));
                        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.header);
                        //headerLayout.addView(noPlayerError);
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
                    if(user.league == null) {
                        user.league = "";
                    }
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
