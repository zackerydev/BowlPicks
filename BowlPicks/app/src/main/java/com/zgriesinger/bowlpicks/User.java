package com.zgriesinger.bowlpicks;

import java.util.ArrayList;

/**
 * Created by Zackery on 10/6/2017.
 */

public class User {
    public String displayName;
    public ArrayList<String> picks;
    public boolean picked;
    public String league;
    public User(String _displayName, ArrayList<String> _picks, boolean _picked, String _league) {
        this.displayName = _displayName;
        this.picks = _picks;
        this.picked = _picked;
        this.league = _league;
    }
    public User() {}
}
