package com.zgriesinger.bowlpicks;

/**
 * Created by ZG052265 on 9/30/2017.
 */

public class Game implements Comparable<Game> {
    public String home;
    public String away;
    public String name;
    public int order;

    public Game(String _home, String _away, String _name, int _order) {
        this.home = _home;
        this.away = _away;
        this.name = _name;
        this.order = _order;
    }

    public int compareTo(Game compareGame) {
        return this.order - compareGame.order;
    }
}
