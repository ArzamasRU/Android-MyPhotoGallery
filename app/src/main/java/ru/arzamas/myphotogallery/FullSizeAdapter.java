package ru.arzamas.myphotogallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

class FullSizeAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> arrImages;

    public FullSizeAdapter(Context context, ArrayList<String> arrImages){
        this.context = context;
        this.arrImages = arrImages;
    }

    @Override
    public int getCount() {
        return arrImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAdapter = inflater.inflate(R.layout.full_item, null);
        ImageView imageView = viewAdapter.findViewById(R.id.fullImageView);
        ViewPager viewPager = (ViewPager) container;

        Activity currActivity = ((FullScreenActivity) context);

        Button buttonBack = viewAdapter.findViewById(R.id.back);
        buttonBack.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("IMAGES", arrImages);
            intent.putExtra("ACTION", FullScreenActivity.FINISH_MAIN_ACTIVITY);
            currActivity.setResult(Activity.RESULT_OK,intent);
            currActivity.finish();
        });

        Button buttonDelete = viewAdapter.findViewById(R.id.delete);
        buttonDelete.setOnClickListener(view -> {
            arrImages.remove(viewPager.getCurrentItem());
            currActivity.recreate();
        });

        Glide.with(context)
                .load(arrImages.get(position))
                .centerCrop()
                .apply(new RequestOptions().centerInside())
                .into(imageView);

        viewPager.addOnPageChangeListener(new CircularViewPagerHandler(viewPager));
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
//            private int currState;
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
//
//            @Override
//            public void onPageSelected(int position) {}
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.d("!!!!!!!!!!! SetNextItem", Integer.toString(viewPager.getCurrentItem())
//                        + " " + Integer.toString(state));
//                if (state == ViewPager.SCROLL_STATE_IDLE && state != ViewPager.SCROLL_STATE_SETTLING) {
////                        if (state != ViewPager.SCROLL_STATE_IDLE) {
//                            final int lastPosition = viewPager.getAdapter().getCount() - 1;
//                            if (viewPager.getCurrentItem() == 0) {
//                                viewPager.setCurrentItem(lastPosition, true);
//                            } else if (viewPager.getCurrentItem() == lastPosition) {
//                                viewPager.setCurrentItem(0, true);
//                            }
////                        }
//                    }
//                }
//        });
        viewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        viewPager.addView(viewAdapter,0);
        return viewAdapter;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        ViewPager viewPager = (ViewPager) container;
        viewPager.removeView((View) object);
    }
}
