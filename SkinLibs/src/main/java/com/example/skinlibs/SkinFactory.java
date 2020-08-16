package com.example.skinlibs;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

//拦截XML的生成过程
public class SkinFactory implements LayoutInflater.Factory2 {

    private static final String[] prxfixList = {
            "android.widget."      ,
            "android.view."      ,
            "android.webkit."      ,
    };

    private List<SkinView> viewList = new ArrayList<>();
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        System.out.println("XM---------------------"+name);

        View view = null;
        if (name.contains("."))
        {
            view = onCreateView(name,context,attrs);
        }
        else
        {
            for (String s:prxfixList)
            {
                String viewName = s + name;
                view = onCreateView(viewName,context,attrs);

                if (view != null){
                    break;
                }
            }
        }

        if (view != null)
        {
            paserView(view,name,attrs);
        }

        return view;
    }

    private void paserView(View view,String name,AttributeSet attributeSet)
    {
        List<SkinItem> skinItems = new ArrayList<>();
        for (int x = 0;x<attributeSet.getAttributeCount();x++)
        {
            String attrName = attributeSet.getAttributeName(x);

            if (attrName.contains("background") || attrName.contains("textColor")|| attrName.contains("src"))
            {
                String attributeValue = attributeSet.getAttributeValue(x);
                int resId = Integer.parseInt(attributeValue.substring(1));

                //colorPrimary
                String resourceEntryName = view.getResources().getResourceEntryName(resId);
                //color
                String resourceTypeName = view.getResources().getResourceTypeName(resId);

//                viewList.add(view);
                SkinItem skinItem = new SkinItem(attrName,resourceTypeName,resourceEntryName,resId);

                skinItems.add(skinItem);
            }
        }

        if (skinItems.size() > 0){
            SkinView skinView = new SkinView(view,skinItems);
            viewList.add(skinView);
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;

        try {
            Class aclass= context.getClassLoader().loadClass(name);
            Constructor<? extends View> constructor = aclass.getConstructor(Context.class,AttributeSet.class);

            view = constructor.newInstance(context,attrs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    public void apply()
    {
        for(SkinView skinView : viewList)
        {
            skinView.apply();
        }
    }

    class SkinView
    {
        View view;
        List<SkinItem> skinItems;

        public SkinView(View view,List<SkinItem> skinItems)
        {
            this.view = view;
            this.skinItems = skinItems;
        }

        public void apply(){
            for (SkinItem skinItem:skinItems)
            {

                if (skinItem.getName().equals("background"))
                {
                    if(skinItem.getTypeName().equals("color"))
                    {
                        int color = SkinManager.getInstance().getColor(skinItem.getResId());
                        view.setBackgroundColor(color);
                    }
                    else if(skinItem.getTypeName().equals("drawable") || skinItem.getTypeName().equals("mipmap"))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            view.setBackground(SkinManager.getInstance().getDrawable(skinItem.getResId()));
                        }
                        else
                        {
                            view.setBackgroundDrawable(SkinManager.getInstance().getDrawable(skinItem.getResId()));
                        }
                    }
                }
                else if(skinItem.getName().equals("src"))
                {
                    if (skinItem.getTypeName().equals("drawable") || skinItem.getTypeName().equals("mipmap"))
                    {
                        ((ImageView)view).setImageDrawable(SkinManager.getInstance().getDrawable(skinItem.getResId()));
                    }
                }
                else if(skinItem.getName().equals("textColor"))
                {
                    ((TextView)view).setTextColor(SkinManager.getInstance().getColor(skinItem.getResId()));
                }
            }
        }
    }

    class SkinItem{
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getEntryName() {
            return entryName;
        }

        public void setEntryName(String entryName) {
            this.entryName = entryName;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

        String name;
        String typeName;
        String entryName;
        int resId;

        public SkinItem(String name,String typeName,String entryName,int resId)
        {
            this.name = name;
            this.typeName = typeName;
            this.entryName = entryName;
            this.resId = resId;
        }
    }

}
