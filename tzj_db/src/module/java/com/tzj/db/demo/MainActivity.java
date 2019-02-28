package com.tzj.db.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.tzj.db.callback.DBCallBack;

import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private Dog mDog;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        mDog = new Dog();

    }

    public void insert(View view){
//        Dog dog = mDog.selectFirst("order by age desc");
        Dog dog = null;
        if (dog == null){
            mDog.setAge(1);
            mDog.setMe(false);
            mDog.setBirthDay(new Date());
        }else{
            dog.setAge(dog.getAge()+1);
            mDog = dog;
        }
        mDog.where(null)
                .insert(new DBCallBack() {
            @Override
            public void onResult(List list, long idRoNum) {
                select(null);
            }
        });
    }
    public void delete(View view){
        mDog.where("age>10")
                .delete(new DBCallBack() {
                    @Override
                    public void onResult(List list, long idRoNum) {
                        select(null);
                    }
                });
    }
    public void select(View view){
        mDog.where(null)
                .select(new DBCallBack() {
                    @Override
                    public void onResult(List list, long idRoNum) {
                        StringBuffer sb = new StringBuffer();
                        for (Object obj:list){
                            sb.append(obj.toString()).append("\n");
                        }
                        textView.setText(sb.toString());
                    }
                });
    }
    public void update(View view){
        mDog.where("age = ?",1)
                .update(new DBCallBack() {
                    @Override
                    public void onResult(List list, long idRoNum) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDog.close();
    }
}
