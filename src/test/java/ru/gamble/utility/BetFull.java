package ru.gamble.utility;

import ru.gamble.stepdefs.CommonStepDefs;

import java.util.ArrayList;
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

    public void normalizationBet(){
        List<String> newNames = new ArrayList<>();
        names.forEach(el->newNames.add(CommonStepDefs.stringParse(el)));
        setNames(newNames);

        setSum(sum.replace(",",".").replace(".00",""));

        setType(type.toLowerCase());
    }


    public boolean equals(BetFull betFull) {
        if (!this.getNames().equals(betFull.getNames())){
            System.out.println("Несовпадение: " + this.getNames() + " и " + betFull.getNames());
            return false;
        }
        if (!this.getCoefs().equals(betFull.getCoefs())){
            System.out.println("Несовпадение: " + this.getCoefs() + " и " + betFull.getCoefs());
            return false;
        }
        if (!this.getDates().equals(betFull.getDates())){
            System.out.println("Несовпадение: " + this.getDates() + " и " + betFull.getDates());
            return false;
        }
        if (!this.getType().equals(betFull.getType())){
            System.out.println("Несовпадение: " + this.getType() + " и " + betFull.getType());
            return false;
        }
        if (!this.getTimeBet().equals(betFull.getTimeBet())){
            System.out.println("Несовпадение: " + this.getTimeBet() + " и " + betFull.getTimeBet());
            return false;
        }
        if (!this.getSum().equals(betFull.getSum())){
            System.out.println("Несовпадение: " + this.getSum() + " и " + betFull.getSum());
            return false;
        }
        return true;
        //return super.equals(obj);
    }
}
