package com.zgriesinger.bowlpicks;

import java.util.ArrayList;

/**
 * Created by Zackery on 10/6/2017.
 */

public class User {
    public String displayName;
    public ArrayList<String> picks;
    public User(String _displayName, ArrayList<String> _picks) {
        this.displayName = _displayName;
        this.picks = _picks;
    }
    public User() {}
}
