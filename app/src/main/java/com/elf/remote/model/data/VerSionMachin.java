package com.elf.remote.model.data;

import java.util.ArrayList;

public class VerSionMachin {
    private static String Name;
    private static int Stop;
    private static boolean rdy;
    private static boolean con;
    private static int volume;
    private static int speakerKind;
    private static String FilePath;
    private static boolean MultiCheck;
    private static ArrayList<String> PathList;
    private static int SetKind;

    public static int getSpeakerKind() {
        return speakerKind;
    }

    public static void setSpeakerKind(int speakerKind) {
        VerSionMachin.speakerKind = speakerKind;
    }

    public static int getSetKind() {
        return SetKind;
    }

    public static void setSetKind(int setKind) {
        SetKind = setKind;
    }

    public static boolean isCon() {
        return con;
    }

    public static void setCon(boolean con) {
        VerSionMachin.con = con;
    }

    public static ArrayList<String> getPathList() {
        return PathList;
    }

    public static void setPathList(ArrayList<String> pathList) {
        PathList = pathList;
    }

    public static boolean isMultiCheck() {
        return MultiCheck;
    }

    public static void setMultiCheck(boolean multiCheck) {
        MultiCheck = multiCheck;
    }

    public static String getFilePath() {
        return FilePath;
    }

    public static void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public static int getVolume() {
        return volume;
    }

    public static void setVolume(int volume) {
        VerSionMachin.volume = volume;
    }

    public static boolean isRdy() {
        return rdy;
    }

    public static void setRdy(boolean rdy) {
        VerSionMachin.rdy = rdy;
    }

    public static int getStop() {
        return Stop;
    }

    public static void setStop(int stop) {
        Stop = stop;
    }

    public static void setName(String name) {
        Name = name;
    }

    public static String getName() {
        return Name;
    }
}
