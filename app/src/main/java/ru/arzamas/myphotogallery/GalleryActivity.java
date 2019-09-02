package ru.arzamas.myphotogallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GalleryActivity extends AppCompatActivity implements MyActions{

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private ArrayList<String> arrImages;
    private String strPid;
    private SharedPreferences myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        check permission
        if (ContextCompat.checkSelfPermission(GalleryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            finish();
        }

        myPref = getPreferences(Activity.MODE_PRIVATE);
        strPid = Integer.toString(android.os.Process.myPid());
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
//        It will allow RecyclerView to avoid invalidating the whole layout when its adapter contents change.
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter("closeFullScreen"));

        boolean prefContains = myPref.contains("IMAGES"+ strPid);

        if (savedInstanceState == null && !prefContains) {
            arrImages = getAllImagesByFolder(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString(),
                    null);
        } else if (savedInstanceState != null) {
           arrImages = (ArrayList<String>) savedInstanceState.getSerializable("IMAGES");
       } else {
            arrImages = getAllImagesByFolder(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString(),
                    myPref.getStringSet("IMAGES" + strPid, null));
        }

        myPref.edit().clear().apply();

        if (arrImages.isEmpty()) {
            TextView empty = findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);
        }

        IRecyclerViewListener listener = (view, position) -> {
            // open full screen activity
            Intent intentI = new Intent(getApplicationContext(), FullScreenActivity.class);
            intentI.putExtra("IMAGES", arrImages);
            intentI.putExtra("POSITION", position);
//            startActivity(i);
            startActivityForResult(intentI,999);
        };

        GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(this, arrImages, listener);
        recyclerView.setAdapter(galleryImageAdapter);
    }

    public ArrayList<String> getAllImagesByFolder(String path, Set<String> revisedSet){
        ArrayList<String> list = new ArrayList<>();
        String tempStr;

        Uri allVideosuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};

        try (Cursor cursor = GalleryActivity.this.getContentResolver().query(allVideosuri, projection,
                MediaStore.Images.Media.DATA + " like ? ", new String[]{"%" + path + "%"},
                null)) {
            cursor.moveToFirst();
            do {
                tempStr = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                if (revisedSet == null)
                    list.add(tempStr);
                else if (revisedSet.contains(tempStr))
                    list.add(tempStr);
            } while (cursor.moveToNext());
            Collections.reverse(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> oldArrImages = arrImages;
            arrImages = (ArrayList<String>) intent.getSerializableExtra("IMAGES");
            if (oldArrImages != null && !oldArrImages.equals(arrImages)) {
                Intent intentB = new Intent();
                intentB.putExtra("IMAGES", arrImages);
                GalleryActivity.this.setResult(Activity.RESULT_OK,intentB);
                GalleryActivity.this.recreate();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK) {
            if (data.getIntExtra("ACTION", 0) == RECREATE_MAIN_ACTIVITY) {
                arrImages = (ArrayList<String>) data.getSerializableExtra("IMAGES");
                recreate();
            } else if (data.getIntExtra("ACTION", 0) == FINISH_MAIN_ACTIVITY) {
                arrImages = (ArrayList<String>) data.getSerializableExtra("IMAGES");
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("IMAGES", arrImages);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Set<String> revisedSet = new HashSet<>(arrImages);
        myPref.edit().putStringSet("IMAGES" + strPid, revisedSet).apply();
    }

}
