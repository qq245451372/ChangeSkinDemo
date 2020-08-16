package com.example.skinlibs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

import java.lang.reflect.Field;

public class BaseActivity extends Activity {

    public SkinFactory skinFactory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SkinManager.getInstance().setContext(getApplicationContext());
        skinFactory = new SkinFactory();
        setLayoutInflaterFactory(getLayoutInflater());
        LayoutInflaterCompat.setFactory2(getLayoutInflater(),skinFactory);
    }

    public void apply()
    {
        skinFactory.apply();
    }

    public void setLayoutInflaterFactory(LayoutInflater original) {
        LayoutInflater layoutInflater = original;
        try {
            Field mFactorySet = LayoutInflater.class.getDeclaredField("mFactorySet");
            mFactorySet.setAccessible(true);
            mFactorySet.set(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
