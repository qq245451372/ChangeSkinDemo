package com.example.skinlibs;

import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

public class SkinManager {
    private static SkinManager skinManager = new SkinManager();

    private Context context;
    private Resources resources;

    private String packageName;

    private SkinManager(){}

    public void setContext(Context context)
    {
        this.context = context;
    }
    public static SkinManager getInstance()
    {
        return skinManager;
    }

    public void loadSkinApk(String path)
    {
        PackageManager packageManager = context.getPackageManager();

        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES);
        packageName = packageInfo.packageName;
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = assetManager.getClass().getDeclaredMethod("addAssetPath",String.class);
            method.invoke(assetManager,path);

            resources = new Resources(assetManager,context.getResources().getDisplayMetrics(),context.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Drawable getDrawable(int resId)
    {
        if (resourcesIsNull())
        {
            return ContextCompat.getDrawable(context,resId);
        }
        //colorPrimary
        String resourceEntryName = context.getResources().getResourceEntryName(resId);
        //color
        String resourceTypeName = context.getResources().getResourceTypeName(resId);

        int identifier = resources.getIdentifier(resourceEntryName,resourceTypeName,packageName);

        if (identifier == 0 )
        {
            return ContextCompat.getDrawable(context,resId);
        }

        return resources.getDrawable(identifier);
    }

    public int getColor(int resId)
    {
        if (resourcesIsNull())
        {
            return resId;
        }
        //colorPrimary
        String resourceEntryName = context.getResources().getResourceEntryName(resId);
        //color
        String resourceTypeName = context.getResources().getResourceTypeName(resId);

        int identifier = resources.getIdentifier(resourceEntryName,resourceTypeName,packageName);

        if (identifier == 0 )
        {
            return resId;
        }

        return resources.getColor(identifier);
    }

    public boolean resourcesIsNull()
    {
        if (resources == null)
        {
            return true;
        }

        return false;
    }
}
