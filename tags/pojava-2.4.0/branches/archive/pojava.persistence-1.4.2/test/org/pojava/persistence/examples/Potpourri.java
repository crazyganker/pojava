package org.pojava.persistence.examples;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.pojava.datetime.DateTime;

public class Potpourri {
    private String str; // = "hello";
    private int five; // = 5;
    private Date d; // = new Date(86400000);
    private DateTime dt; // = new DateTime(86400000);
    private Object bob; // = new Long(9876543210L);
    private Potpourri confused; // =this;
    private int[] numbers; // ={ 1, 2, 3 };
    private Set set;
    private Map map;

    public Potpourri() {
        // Currently, this is a requirement.
    }

    public Potpourri(String str, int five, Date d, DateTime dt, Object bob, Potpourri confused,
            Set set, Map map) {
        this.str = str;
        this.five = five;
        this.d = d;
        this.dt = dt;
        this.bob = bob;
        this.confused = confused;
        this.set = set;
        this.map = map;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public void setFive(int five) {
        this.five = five;
    }

    public void setD(Date d) {
        this.d = d;
    }

    public void setDt(DateTime dt) {
        this.dt = dt;
    }

    public void setBob(Object bob) {
        this.bob = bob;
    }

    public void setConfused(Potpourri confused) {
        this.confused = confused;
    }

    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }

    public String getStr() {
        return str;
    }

    public int getFive() {
        return five;
    }

    public Date getD() {
        return d;
    }

    public DateTime getDt() {
        return dt;
    }

    public Object getBob() {
        return bob;
    }

    public Potpourri getConfused() {
        return confused;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

}
