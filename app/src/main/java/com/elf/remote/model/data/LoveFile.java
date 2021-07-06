package com.elf.remote.model.data;

import java.io.Serializable;

public class LoveFile implements Serializable {
    public int Id;
    public int GroupID;
    public int Number;
    public int Tempo;
    public int PlayKey;
    public int UseStaff;
    public int Staff1Type;
    public int Staff2Type;
    public int Staff3Type;
    public int Staff1Oct;
    public int Staff2Oct;
    public int Staff3Oct;
    public int Staff1Key;
    public int Staff2Key;
    public int Staff3Key;
    public int ETC;

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

    public String Title;
    public String Singer;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public int getTempo() {
        return Tempo;
    }

    public void setTempo(int tempo) {
        Tempo = tempo;
    }

    public int getPlayKey() {
        return PlayKey;
    }

    public void setPlayKey(int playKey) {
        PlayKey = playKey;
    }

    public int getUseStaff() {
        return UseStaff;
    }

    public void setUseStaff(int useStaff) {
        UseStaff = useStaff;
    }

    public int getStaff1Type() {
        return Staff1Type;
    }

    public void setStaff1Type(int staff1Type) {
        Staff1Type = staff1Type;
    }

    public int getStaff2Type() {
        return Staff2Type;
    }

    public void setStaff2Type(int staff2Type) {
        Staff2Type = staff2Type;
    }

    public int getStaff3Type() {
        return Staff3Type;
    }

    public void setStaff3Type(int staff3Type) {
        Staff3Type = staff3Type;
    }

    public int getStaff1Oct() {
        return Staff1Oct;
    }

    public void setStaff1Oct(int staff1Oct) {
        Staff1Oct = staff1Oct;
    }

    public int getStaff2Oct() {
        return Staff2Oct;
    }

    public void setStaff2Oct(int staff2Oct) {
        Staff2Oct = staff2Oct;
    }

    public int getStaff3Oct() {
        return Staff3Oct;
    }

    public void setStaff3Oct(int staff3Oct) {
        Staff3Oct = staff3Oct;
    }

    public int getStaff1Key() {
        return Staff1Key;
    }

    public void setStaff1Key(int staff1Key) {
        Staff1Key = staff1Key;
    }

    public int getStaff2Key() {
        return Staff2Key;
    }

    public void setStaff2Key(int staff2Key) {
        Staff2Key = staff2Key;
    }

    public int getStaff3Key() {
        return Staff3Key;
    }

    public void setStaff3Key(int staff3Key) {
        Staff3Key = staff3Key;
    }

    public int getETC() {
        return ETC;
    }

    public void setETC(int ETC) {
        this.ETC = ETC;
    }
}
