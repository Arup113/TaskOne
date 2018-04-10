package com.arupkumar.taskone.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by Arup Kumar Pramanik on 04/10/2018.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = BaseActivity.class.getSimpleName();
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        setupViewComponents();
    }

    public void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeViewComponentsEventListeners();
    }

    protected void setupViewComponents() {
        Log.d(TAG, "initializeViewComponents()");
        setContentView();
        setupActionBar();
        initializeEditTextComponents();
        initializeButtonComponents();
        initializeTextViewComponents();
        initializeImageViewComponents();
        initializeOtherViewComponents();
        initializeViewComponentsEventListeners();
    }

    abstract void setContentView();

    abstract void setupActionBar();

    abstract void initializeEditTextComponents();

    abstract void initializeButtonComponents();

    abstract void initializeTextViewComponents();

    abstract void initializeImageViewComponents();

    abstract void initializeOtherViewComponents();

    abstract void initializeViewComponentsEventListeners();

    abstract void removeViewComponentsEventListeners();

    abstract void exitThisWithAnimation();

    abstract void startNextWithAnimation(Intent intent, boolean isFinish);
}
