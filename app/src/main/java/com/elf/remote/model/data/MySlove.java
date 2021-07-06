package com.elf.remote.model.data;

import java.io.Serializable;

public class MySlove implements Serializable {
    public int Number;
    public String Title;
    public String Singer;
    public int Tempo;
    public int Tmep;
    public String Main;
    public String Intaval;
    public int count;
    public int Love_id;
    public int AbsMain;
    public int counter;
    public int id;

    public int getAbsMain() {
        return AbsMain;
    }

    public void setAbsMain(int absMain) {
        AbsMain = absMain;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTmep() {
        return Tmep;
    }

    public void setTmep(int tmep) {
        Tmep = tmep;
    }

    public String getIntaval() {
        return Intaval;
    }

    public void setIntaval(String intaval) {
        Intaval = intaval;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSinger() {
        return Singer;
    }

    public void setSinger(String singer) {
        Singer = singer;
    }

    public int getTempo() {
        return Tempo;
    }

    public void setTempo(int tempo) {
        Tempo = tempo;
    }

    public String getMain() {
        return Main;
    }

    public void setMain(String main) {
        Main = main;
    }

    public int getLove_id() {
        return Love_id;
    }

    public void setLove_id(int love_id) {
        Love_id = love_id;
    }
}
