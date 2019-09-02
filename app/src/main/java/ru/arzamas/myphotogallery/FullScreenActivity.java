package ru.arzamas.myphotogallery;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class FullScreenActivity extends Activity implements MyActions{

    private int position;
    private ArrayList<String> arrImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        if (savedInstanceState==null){
            Intent intent = getIntent();
            arrImages = (ArrayList<String>) intent.getSerializableExtra("IMAGES");
            position = intent.getIntExtra("POSITION",0);
        } else {
            arrImages = (ArrayList<String>) savedInstanceState.getSerializable("IMAGES");
        }

        if (arrImages == null || arrImages.isEmpty()) {
            Intent intentE = new Intent();
            intentE.putExtra("IMAGES", arrImages);
            intentE.putExtra("ACTION", RECREATE_MAIN_ACTIVITY);
            setResult(Activity.RESULT_OK,intentE);
            finish();
            //            notifyCloseFullScreen();
        }

        ViewPager viewPager = findViewById(R.id.viewPager);
        FullSizeAdapter fullSizeAdapter = new FullSizeAdapter(this, arrImages);
        viewPager.setAdapter(fullSizeAdapter);
        viewPager.setCurrentItem(position, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("IMAGES", arrImages);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        notifyCloseFullScreen();
    }

    private void notifyCloseFullScreen(){
        Intent intent = new Intent("closeFullScreen");
        intent.putExtra("IMAGES", arrImages);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
