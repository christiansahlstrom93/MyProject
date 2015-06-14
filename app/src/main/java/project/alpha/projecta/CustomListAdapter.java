package project.alpha.projecta;

/**
 * Created by Christian on 2015-05-31.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<BankInfo> bankInfo;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<BankInfo> bankItems) {
        this.activity = activity;
        this.bankInfo = bankItems;
    }

    @Override
    public int getCount() {
        return bankInfo.size();
    }

    @Override
    public Object getItem(int location) {
        return bankInfo.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);
        ImageView lilPic = (ImageView) convertView.findViewById(R.id.imageView7);
        BankInfo m = bankInfo.get(position);

        lilPic.setImageDrawable(m.getTheDraw());

        year.setText(m.getYear());
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        title.setText(m.getTitle());

        rating.setText(m.getOwnerInfo());


        return convertView;
    }
}
