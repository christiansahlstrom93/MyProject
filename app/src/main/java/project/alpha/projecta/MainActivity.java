package project.alpha.projecta;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends ActionBarActivity implements MaterialTabListener {

    MaterialTabHost tabHost;
    public ViewPager pager;
    private MyPageAdapter pagerAdapter;

    private boolean openSetting = true;

    public static double RATE, START_VALUE;
    public static int INTERVAL;
    public static String BANK, ACCOUNT;
    public static Bitmap URL;
    public static MainActivity instance;


    public static List<Double> high = new ArrayList<>();
    public static List<String> bankName = new ArrayList<>();
    public static List<String> account = new ArrayList<>();
    public static List<Double> rate = new ArrayList<>();
    public static boolean COMPARE = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        pagerAdapter = new MyPageAdapter(getSupportFragmentManager(), getApplicationContext());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                tabHost.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setIcon(pagerAdapter.getIcon(i))
                            .setTabListener(this)
            );
        }


        android.support.v7.app.ActionBar bar = getSupportActionBar();

        if (bar != null) {
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
            bar.setTitle(null);
            bar.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
            bar.setBackgroundDrawable(new ColorDrawable(Color.rgb(27, 171, 46)));
            bar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = LayoutInflater.from(this);
            View v = inflator.inflate(R.layout.titleview, null);


            final ImageView set = (ImageView) v.findViewById(R.id.imageView4);
            set.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    set.setSelected(arg1.getAction() == MotionEvent.ACTION_DOWN);
                    if(openSetting) {
                        openSetting = false;
                        startActivity(new Intent(MainActivity.this, Settings.class));
                    }
                    return true;
                }
            });

            TextView t = (TextView) v.findViewById(R.id.title);
            t.setText("Cashnet");
            bar.setCustomView(v);
        }
    }

    @Override
    protected void onPostResume() {
        openSetting = true;
        Log.e("Rolle", "INNE");
        super.onPostResume();
    }

    @Override
    public void onTabSelected(MaterialTab tab) {

        pager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabReselected(MaterialTab tab) {
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
    }
}