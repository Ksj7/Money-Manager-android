package com.tonight.manage.organization.managingmoneyapp.Builder;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import com.tonight.manage.organization.managingmoneyapp.MyApplication;
import com.tonight.manage.organization.managingmoneyapp.R;

/**
 * Created by 10 on 2016-11-23.
 */

public class ProductButton extends Button{

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    private String name;
    private Context context;

    public ProductButton(Context context, ProductBuilder builder, int color) {
        super(context);
        this.context = context;
        this.name = builder.name;
        setText(name);

        float spTextSize = 4;
        float textSize = spTextSize * getResources().getDisplayMetrics().scaledDensity;
        setTextSize(textSize);
        setMaxLines(1);
        setTextColor(Color.WHITE);
        if(color == 0){
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
        }else if(color==1){
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkRed));
        }
    }

    public String getName() {
        return this.name;
    }

    public static class ProductBuilder {
        private String name;
        private int color;

        public ProductBuilder(String name, int color) {
            this.name = name;
            this.color = color;
        }
        public ProductButton build() {
            return new ProductButton(MyApplication.getItemContext(),this,color);
        }

    }
}