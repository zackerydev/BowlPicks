package com.zgriesinger.bowlpicks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    public void displayRow(User user){
        TableLayout pickTable = (TableLayout)findViewById(R.id.pick_table);
        TableRow row = new TableRow(this);
        TextView name = new TextView(this);
        name.setText(user.displayName);
        name.setPadding(3, 1, 2, 1);
        row.addView(name);
        for(int i = 0; i < user.picks.size(); i++) {
            TextView pick = new TextView(this);
            pick.setText(user.picks.get(i));
            row.addView(pick);
        }
        pickTable.addView(row);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query allPicks = database.getReference("Users");
        allPicks.orderByChild("displayName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    displayRow(user);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
