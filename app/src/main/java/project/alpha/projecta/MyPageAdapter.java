package project.alpha.projecta;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Christian on 2015-05-30.
 */
public class MyPageAdapter extends FragmentPagerAdapter {

    static int[] icons = {R.drawable.bank, R.drawable.gris};
    Context c;
    public MyPageAdapter(FragmentManager fm,Context v) {
        super(fm);
        this.c = v;
    }

    @Override
    public Fragment getItem(int position) {

        try {

            switch (position) {

                case 0:

                    return new MainView();
                case 1:
                    return new GraphFragment();

                default:
                    break;

            }
        } catch (Exception e) {

        }


        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public Drawable getIcon(int position) {

        Drawable drawable = c.getResources().getDrawable(icons[position]);

        return drawable;
    }
}
