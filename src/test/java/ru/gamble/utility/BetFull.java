package ru.gamble.utility;

import java.util.List;

public class BetFull {
    private List<String> names;
    private List<String> coefs;
    private List<String> dates;
    private String type;
    private String sum;
    private String timeBet;

    public void setCoefs(List<String> coefs) {
        this.coefs = coefs;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimeBet(String timeBet) {
        this.timeBet = timeBet;
    }

    public List<String> getNames(){
        return this.names;
    }

    public List<String> getCoefs() {
        return coefs;
    }

    public List<String> getDates() {
        return dates;
    }

    public String getSum() {
        return sum;
    }

    public String getType() {
        return type;
    }

    public String getTimeBet() {
        return timeBet;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
