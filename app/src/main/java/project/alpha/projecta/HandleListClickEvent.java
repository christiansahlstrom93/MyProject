package project.alpha.projecta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christian on 2015-06-03.
 */
public class HandleListClickEvent implements MathListener {

    String bitmap;
    Bitmap mBitmap;
    List<String> accountName = new ArrayList<>();
    List<Double> rate = new ArrayList<>();
    List<String> accountNameOrder = new ArrayList<>();
    List<Double> rateOrder = new ArrayList<>();
    private ProgressDialog pd;
    Context context;
    Activity act;
    AlertDialog.Builder alert;
    private CustomListAdapter adapter;
    private List<BankInfo> bankInfo = new ArrayList<>();
    private ListView bankList;
    String bank;

    public void onClick(View view, String bank, String bitmap, Context c, Activity act) {
        this.context = c;
        this.act = act;
        this.bank = bank;
        this.bitmap = bitmap;
        DownloadAccounts da = new DownloadAccounts(view, bank);
        showPd(c);
        da.execute();
    }

    void showPd(Context c) {
        this.pd = new ProgressDialog(c);
        this.pd.setMessage("Laddar data");
        this.pd.setCancelable(false);
        this.pd.show();
    }

    void hidePd() {
        if (this.pd != null) {
            this.pd.dismiss();
        }
    }

    private void openDialogForInput() {
        alert = new AlertDialog.Builder(context);
        AlertDialog mDialog;
        LayoutInflater factory = LayoutInflater.from(context);
        final View v = factory.inflate(R.layout.account_layout, null);
        TextView t = (TextView) v.findViewById(R.id.textView4);
        if(new MainView().hasSAsLastDigit(bank)) {
            t.setText(bank + " konton");
        } else {
            t.setText(bank + "s konton");
        }
        ImageView img = (ImageView) v.findViewById(R.id.imageView);
        img.setImageBitmap(mBitmap);
        alert.setView(v);
        alert.setCancelable(true);
        mDialog = alert.create();
        mDialog.show();

        for (int i = 0; i < accountName.size(); i++) {
            initListViews(v, accountName.get(i), rate.get(i), mDialog);
        }
    }

    private void initListViews(View v, String accName, double rate, final AlertDialog dialog) {
        bankList = (ListView) v.findViewById(R.id.listView);
        adapter = new CustomListAdapter(act, bankInfo);
        bankList.setAdapter(adapter);

        BankInfo bankItems = new BankInfo();
        bankItems.setTitle(accName);
        if (rate <= 0.5) {
            bankItems.setThumbnailUrl("http://pixabay.com/static/uploads/photo/2013/07/12/15/54/smiley-150548_640.png");
        } else {
            bankItems.setThumbnailUrl("http://pixabay.com/static/uploads/photo/2013/07/12/15/58/smiley-150660_640.png");
        }
        bankItems.setOwnerInfo("Tryck för att jämföra");
        bankItems.setYear("" + rate + "% sparränta");
        bankItems.setTheDraw(context.getResources().getDrawable(R.drawable.rates));
        bankInfo.add(bankItems);
        adapter.notifyDataSetChanged();
        rateOrder.add(rate);
        accountNameOrder.add(accName);

        bankList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.RATE = rateOrder.get(position);
                MainActivity.ACCOUNT = accountNameOrder.get(position);
                MainActivity.BANK = bank;
                MainActivity.URL = mBitmap;

                if(!MainActivity.COMPARE) {

                    if (MainActivity.START_VALUE != 0 && MainActivity.INTERVAL != 0) {
                        new GraphFragment().updateUI(MainActivity.START_VALUE, percentCalculation(MainActivity.START_VALUE, MainActivity.RATE, MainActivity.INTERVAL)
                                , MainActivity.INTERVAL, MainActivity.BANK, MainActivity.ACCOUNT, MainActivity.RATE, MainActivity.URL);

                        if (!MainView.instance.updateMainViewUI()) {
                            Toast.makeText(context, "Oväntat fel.. Försök igen.", Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.instance.pager.setCurrentItem(1);
                    } else {
                        if (!MainView.instance.updateMainViewRate()) {
                            Toast.makeText(context, "Oväntat fel.. Försök igen.", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(context, "Var god fyll i ditt saldo och hur länge du vill spara", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                } else {

                    if(MainActivity.account.contains(MainActivity.ACCOUNT) && MainActivity.bankName.contains(MainActivity.BANK)) {
                        Toast.makeText(context,"Du har redan valt detta kontot för jämförelse",Toast.LENGTH_SHORT).show();
                    } else {
                        MainActivity.account.add(MainActivity.ACCOUNT);
                        MainActivity.bankName.add(MainActivity.BANK);
                        MainActivity.rate.add(MainActivity.RATE);
                        MainActivity.high.add(percentCalculation(MainActivity.START_VALUE, MainActivity.RATE, MainActivity.INTERVAL));
                    }

                    if(MainActivity.account.size() >= 2) {
                        MainActivity.instance.pager.setCurrentItem(1);
                        new GraphFragment().updateUICompare(MainActivity.START_VALUE, MainActivity.high,
                                MainActivity.INTERVAL, MainActivity.bankName, MainActivity.account, MainActivity.rate);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context,"Välj ett konto till som du vill jämföra",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });


    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch (IOException e) {

            return null;
        }
    }

    @Override
    public double percentCalculation(double start, double rate, int interval) {
        double checker = start;
        while (interval > 0) {
            start += (divideAndConquer(start,(rate*1)));
            interval--;
        }
        GraphFragment.setProfit(start - checker);
        GraphFragment.setSaving(start);
        return start;
    }

    @Override
    public double divideAndConquer(double start, double rate) {
        double holdUpValue = (start / 100);
        holdUpValue = (holdUpValue * rate);
        return holdUpValue;
    }

    private class DownloadAccounts extends AsyncTask<Cursor, Long, Long> {
        DatabaseConnector dbConn;
        String bank;
        View view;

        public DownloadAccounts(View view, String bank) {
            dbConn = new DatabaseConnector();
            this.view = view;
            this.bank = bank;
        }

        protected Long doInBackground(Cursor... urls) {

            try {
                while (!dbConn.connectingDatabase()) {

                }
            } catch (Exception e) {
            }

            selectAccounts(dbConn.getConn(), bank);
            mBitmap = getBitmapFromURL(bitmap);
            return null;
        }

        void selectAccounts(Connection conn, String bank) {
            try {
                String args = "Select * from Accounts where Banks_Bank = '" + bank + "'";
                dbConn.setStatement(conn.createStatement());
                ResultSet set = dbConn.getStatement().executeQuery(args);

                while (set.next()) {
                    accountName.add(set.getString("Accountname"));
                    rate.add(set.getDouble("Rate"));
                }
            } catch (Exception e) {
                Log.e("Rolle", "Här" + e);
            }
        }


        protected void onPostExecute(Long result) {
            openDialogForInput();
            hidePd();
        }
    }

}
