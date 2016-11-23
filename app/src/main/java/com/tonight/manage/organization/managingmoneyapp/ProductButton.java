package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

/**
 * Created by 10 on 2016-11-23.
 */

public class ProductButton extends Button{

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    private String name;

    public ProductButton(Context context, ProductBuilder builder) {
        super(context);
        this.name = builder.name;
        setText(name);

        float spTextSize = 4;
        float textSize = spTextSize * getResources().getDisplayMetrics().scaledDensity;
        setTextSize(textSize);
        setMaxLines(1);
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
        setTextColor(Color.WHITE);
    }

    public String getName() {
        return this.name;
    }

    public static class ProductBuilder {
        private String name;

        public ProductBuilder(String name) {

            this.name = name;
        }
        public ProductButton build() {
            return new ProductButton(MyApplication.getItemContext(),this);
        }

    }
}