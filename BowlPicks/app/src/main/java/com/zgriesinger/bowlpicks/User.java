package com.zgriesinger.bowlpicks;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by Zackery on 10/6/2017.
 */

public class User {

    public String displayName;
    public ArrayList<String> picks;
    public boolean picked;
    public String league;
    public String winners;
    public User(String _displayName, ArrayList<String> _picks, boolean _picked, String _league) {
        this.displayName = _displayName;
        this.picks = _picks;
        this.picked = _picked;
        this.league = _league;


    }
    public User() {}
}
