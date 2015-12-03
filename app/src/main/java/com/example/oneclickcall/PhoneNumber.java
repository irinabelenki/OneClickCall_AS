package com.example.oneclickcall;

/**
 * Created by Irina on 11/2/2015.
 */
public class PhoneNumber {

    String number = null;
    boolean selected = false;

    public PhoneNumber(String number, boolean selected) {
        super();
        this.number = number;
        this.selected = selected;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
