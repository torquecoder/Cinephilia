package tech.torque.cinephilia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import tech.torque.cinephilia.Activities.MainActivity;
import tech.torque.cinephilia.R;

import com.squareup.picasso.Picasso;


public class PosterAdapter extends BaseAdapter {


    private Context mContext;

    public PosterAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return MainActivity.images.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView poster;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            poster = (ImageView) inflater.inflate(R.layout.movie_poster, parent, false);
        } else
            poster = (ImageView) convertView;

        Picasso.with(mContext).load(MainActivity.images.get(position)).fit().centerInside().into(poster);

        return poster;
    }

}

