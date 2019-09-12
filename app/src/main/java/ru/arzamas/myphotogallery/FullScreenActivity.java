package ru.arzamas.myphotogallery;

import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class FullScreenActivity extends Activity implements MyActions{

    private int position;
    private ArrayList<String> arrImages;
    protected int currAction = FullScreenActivity.RECREATE_MAIN_ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        if (savedInstanceState==null){
            /*after selecting the photo in the main activity */
            Intent intent = getIntent();
            arrImages = (ArrayList<String>) intent.getSerializableExtra("IMAGES");
            position = intent.getIntExtra("POSITION",0);
        } else {
            /*after recreating*/
            arrImages = (ArrayList<String>) savedInstanceState.getSerializable("IMAGES");
        }

        if (arrImages == null || arrImages.isEmpty()) {
            /*if all images have been deleted*/
            onBackPressed();
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
        Intent intent = new Intent();
        intent.putExtra("IMAGES", arrImages);
        intent.putExtra("ACTION", currAction);
        setResult(Activity.RESULT_OK,intent);
        super.onBackPressed();
    }
}
