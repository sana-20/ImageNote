package com.line.imagenote.models.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.line.imagenote.NoteActivity;
import com.line.imagenote.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> photoList;
    private PhotoListener listener;

    public interface PhotoListener {
        void onPhotoClicked(int position);
    }


    public ViewPagerAdapter(NoteActivity context, ArrayList<String> photoList, PhotoListener listener) {
        this.context = context;
        this.photoList = photoList;
        this.listener = listener;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return (null != photoList ? photoList.size() : 0);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater  layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_image, null);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClicked(position);
            }
        });

        ImageView imageView = view.findViewById(R.id.imageView);

        Glide.with(view)
                .load(photoList.get(position))
                .into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);

    }
}