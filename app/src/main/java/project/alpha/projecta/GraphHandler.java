package project.alpha.projecta;

import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Christian on 2015-05-30.
 */
public class GraphHandler {


    private final View c;

    public GraphHandler(View v) {
        this.c = v;
    }

    public void initLineGraphSingel(double low, double high, int interval, String bank, String account, double rate, LineChart chart) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        chart.setDescription(bank + ", " + account + " " + interval + " års intervall," + rate + "%");

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        Entry c1e1 = new Entry((int) low, 0);
        valsComp1.add(c1e1);
        Entry c1e2 = new Entry((int) high, 1);
        valsComp1.add(c1e2);
        LineDataSet setComp1 = new LineDataSet(valsComp1, bank);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);


        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add(String.valueOf(year));
        xVals.add(String.valueOf(interval + year));

        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate();

    }

    public void initLineGraphMultiple(double low, List<Double> high, int interval, List<String> bank,
                                      List<String> account, List<Double> rate, LineChart chart) {
        double foul = 0, foul2 = 0;
        int index = 0;
        boolean rightOrder = true;
        if (high.get(0) > high.get(1)) {
            foul = high.get(0);
            foul2 = high.get(1);
        } else {
            rightOrder = false;
            foul = high.get(1);
            foul2 = high.get(0);
        }

        int year = Calendar.getInstance().get(Calendar.YEAR);
        chart.setDescription(bank.get(0) + "," + account.get(0) + "("+rate.get(0)+"%) och " + bank.get(1) +
                "," + account.get(1)+ "("+rate.get(1)+"%)");


        if(!rightOrder) {
            index ++;
        }

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        Log.e(null,"Här kollar vi konto " + account.get(index));
        Entry c1e1 = new Entry((int) low, 0);
        valsComp1.add(c1e1);
        Entry c1e2 = new Entry((int) foul, 2);
        valsComp1.add(c1e2);

        LineDataSet setComp1 = new LineDataSet(valsComp1, bank.get(index));
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);


        if(!rightOrder) {
            index --;
        } else {
            index ++;
        }

        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();

        Entry c2e1 = new Entry((int) low, 0);

        valsComp1.add(c2e1);
        Entry c2e2 = new Entry((int) foul2, 2);
        valsComp1.add(c2e2);


        LineDataSet setComp2 = new LineDataSet(valsComp2, bank.get(index));
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add(String.valueOf(year));
        xVals.add("");
        xVals.add(String.valueOf(year + interval));
        xVals.add("");

        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate();

        MainActivity.account.clear();
        MainActivity.bankName.clear();
        MainActivity.rate.clear();
        MainActivity.high.clear();
    }
}

