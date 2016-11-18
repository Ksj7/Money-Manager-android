package com.tonight.manage.organization.managingmoneyapp.Abstract;

/**
 * Created by sujinKim on 2016-11-12.
 */

public class ToggleListener{
}

   /* private final ToggleExpandLayout top;
    private final ToggleExpandLayout bottom;
    private final View mView;
    private final boolean isTop;

    public ToggleListener(Builder builder) {
        top = builder.top;
        bottom = builder.bottom;
        mView = builder.mView;
        isTop = builder.isTop;

    }

    public static class Builder {
        private ToggleExpandLayout top;
        private ToggleExpandLayout bottom;
        private View mView;
        private boolean isTop;

        public Builder setTop(ToggleExpandLayout top) {
            this.top = top;
            return this;
        }

        public Builder setBottom(ToggleExpandLayout bottom) {
            this.bottom = bottom;
            return this;
        }

        public Builder setView(View view) {
            this.mView = view;
            return this;
        }

        public Builder isTop(boolean isTop){
            this.isTop = isTop;
            return this;
        }

        public ToggleListener build()
        { return new ToggleListener(this);}

        
    }
    @Override
    public void onStartOpen() {
    }

    @Override
    public void onOpen() {

        if(isTop)
        {
            bottom.setY(bottom.getY()+top.getHeight()*2);
            top.setMinimumHeight(top.getHeight()*3);
            mView.setMinimumHeight(mView.getHeight() + top.getHeight()*2);
        }
        else
        {
           *//* temp = (bottom.getY()-top.getY()) * 4;
            float min = temp + bottom.getHeight();
            mView.setMinimumHeight((int) min + panelHeight);*//*
        }
    }

    @Override
    public void onStartClose() {

    }

    @Override
    public void onClosed() {
        if(isTop) {
            bottom.setY(bottom.getY() - top.getHeight()*2);
            mView.setMinimumHeight(mView.getHeight() -  top.getHeight()*2);
        }else{
        }
    }
}*/