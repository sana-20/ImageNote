package com.line.imagenote.models.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.line.imagenote.NoteActivity;
import com.line.imagenote.PhotoActivity;
import com.line.imagenote.R;
import com.line.imagenote.models.Attachment;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Attachment> photoList;
    private long noteId;
//    private PhotoListener listener;
//
//    public interface PhotoListener {
//        void onPhotoClicked(int position);
//    }


    public ViewPagerAdapter(NoteActivity context, ArrayList<Attachment> photoList, long noteId) {
        this.context = context;
        this.photoList = photoList;
        this.noteId = noteId;
//        this.listener = listener;
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
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("noteId", noteId);
                intent.putExtra("photoId", photoList.get(position).getPhotoId());
                intent.putExtra("uri", photoList.get(position).getUri());
                context.startActivity(intent);
            }
        });

        ImageView imageView = view.findViewById(R.id.imageView);

        Glide.with(view)
                .load(photoList.get(position).getUri())
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