package com.tzj.db.demo;

import com.tzj.db.BaseDB;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dog extends BaseDB {
    public static String stat = "static";
    public String name = "刚出生";
    private int age;
    private boolean isMe;
    private Date birthDay = new Date();

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        this.name = age+"";
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        format.format(birthDay);
        return "name='" + name + '\'' +
                ", age=" + age +
                ", isMe=" + isMe +
                ", birthDay=" + format.format(birthDay) +
                ", _id=" + format.format(_id);
    }
}
