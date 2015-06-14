package project.alpha.projecta;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Christian on 2015-05-30.
 */
public class GraphFragment extends Fragment {

    public static View v;
    public static GraphFragment instance;
    private static double profit;
    private static double saving;
    ImageView cover;
    public static Activity mAct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.graphicalview, container, false);
        instance = this;
        mAct = getActivity();
        return v;
    }

    public void updateUI(double low, double high, int interval, String bank, String account, double rate, Bitmap picURL) {
        GraphHandler g = new GraphHandler(v);
        LineChart chart = (LineChart) v.findViewById(R.id.chart);
        ImageView img = (ImageView) v.findViewById(R.id.imageView2);
        img.setImageBitmap(picURL);
        TextView t1 = (TextView) v.findViewById(R.id.textView2);
        t1.setVisibility(View.VISIBLE);
        t1.setText(MainActivity.BANK + ", " + MainActivity.ACCOUNT);
        TextView t2 = (TextView) v.findViewById(R.id.textView3);
        TextView t3 = (TextView) v.findViewById(R.id.textView5);
        DecimalFormat df = new DecimalFormat("#.00");
        t2.setText("+" + df.format(profit));
        t3.setText(df.format(saving) + " efter " + MainActivity.INTERVAL + " år");
        g.initLineGraphSingel(low, high, interval, bank, account, rate, chart);

        cover = (ImageView) v.findViewById(R.id.imageView8);
        cover.setVisibility(View.INVISIBLE);
    }

    public void updateUICompare(double low, List<Double> high, int interval, List<String> bank, List<String> account, List<Double> rate) {
        GraphHandler g = new GraphHandler(v);
        ImageView img = (ImageView) v.findViewById(R.id.imageView2);



        img.setImageDrawable(mAct.getResources().getDrawable(R.drawable.scale));


        LineChart chart = (LineChart) v.findViewById(R.id.chart);
        TextView t1 = (TextView) v.findViewById(R.id.textView2);
        t1.setText(interval + " års sparande");
        TextView t2 = (TextView) v.findViewById(R.id.textView3);
        TextView t3 = (TextView) v.findViewById(R.id.textView5);
        DecimalFormat df = new DecimalFormat("#.00");
        t2.setText("+" + df.format(high.get(0) - low) + " med " + bank.get(0) + "," + account.get(0));

        t3.setText("+" + df.format(high.get(1) - low) + " med " + bank.get(1) + "," + account.get(1));


        g.initLineGraphMultiple(low, high, interval, bank, account, rate, chart);

        cover = (ImageView) v.findViewById(R.id.imageView8);
        cover.setVisibility(View.INVISIBLE);
    }

    public static void setProfit(double profit) {
        instance.profit = profit;
    }

    public static void setSaving(double saving) {
        instance.saving = saving;
    }
}
