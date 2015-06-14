package project.alpha.projecta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christian on 2015-05-30.
 */
public class MainView extends Fragment implements MathListener {
    private boolean allowedToPopUp = true;

    List<BankInfo> bankInfo = new ArrayList<BankInfo>();
    List<String> urls = new ArrayList<>();
    List<String> bankNames = new ArrayList<>();
    List<String> bankNamesInCorrectOrder = new ArrayList<>();
    List<String> urlsInCorrectOrder = new ArrayList<>();
    List<String> homepage = new ArrayList<>();
    List<String> homepageInCorrectOrder = new ArrayList<>();
    List<Boolean> hasWar = new ArrayList<>();

    CustomListAdapter adapter;
    View view;
    ProgressDialog pd;
    LinearLayout right, left;
    AlertDialog.Builder alert;
    project.alpha.projecta.ListView bankList;
    TextView saldo, rate;
    static MainView instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile, container, false);
        instance = this;
        initComponents();

        DownloadFilesTask dft = new DownloadFilesTask();
        showPd();
        dft.execute();
        return view;
    }

    private void initComponents() {
        bankList = (project.alpha.projecta.ListView) view.findViewById(R.id.listView);

        final ImageView plusButton = (ImageView) view.findViewById(R.id.imageView3);
        plusButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                plusButton.setSelected(arg1.getAction() == MotionEvent.ACTION_DOWN);
                if (allowedToPopUp) {
                    allowedToPopUp = false;
                    openDialogForInput();
                }
                return true;
            }
        });

        bankList.setOnDetectScrollListener(new OnDetectScrollListener() {
            @Override
            public void onUpScrolling() {
                plusButton.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                plusButton.setVisibility(View.VISIBLE);
                            }
                        });
            }

            @Override
            public void onDownScrolling() {
                plusButton.animate()
                        .translationY(plusButton.getHeight())
                        .alpha(1.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                plusButton.setVisibility(View.VISIBLE);
                            }
                        });


            }
        });


        left = (LinearLayout) view.findViewById(R.id.leftLinne);
        right = (LinearLayout) view.findViewById(R.id.rightLinne);

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                left.setSelected(arg1.getAction() == MotionEvent.ACTION_DOWN);
                if (allowedToPopUp) {
                    //  allowedToPopUp = false;
                    //openDialogForInput();
                }
                return true;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                right.setSelected(arg1.getAction() == MotionEvent.ACTION_DOWN);
                if (allowedToPopUp) {
                    //allowedToPopUp = false;
                    //openDialogForInput();
                }
                return true;
            }
        });

        saldo = (TextView) view.findViewById(R.id.textView6);
        rate = (TextView) view.findViewById(R.id.rate);
        restoreValues();
    }

    void initialBankInfo(final String url, final String title, final boolean hasWar, final String homepage) {

        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(final Bitmap bitmap) {
                        try {
                            adapter = new CustomListAdapter(getActivity(), bankInfo);
                            bankList.setAdapter(adapter);

                            BankInfo bankItems = new BankInfo();
                            bankItems.setTitle(title);
                            bankItems.setThumbnailUrl(url);
                            bankItems.setOwnerInfo("Tryck för mer info");
                            bankItems.setYear("Insättningsgaranti");
                            if (hasWar) {
                                bankItems.setTheDraw(getActivity().getResources().getDrawable(R.drawable.bock_no_padding));
                            } else {
                                bankItems.setTheDraw(getActivity().getResources().getDrawable(R.drawable.nowar));
                            }
                            bankNamesInCorrectOrder.add(title);
                            homepageInCorrectOrder.add(homepage);
                            urlsInCorrectOrder.add(url);
                            bankInfo.add(bankItems);
                            adapter.notifyDataSetChanged();

                            bankList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    HandleListClickEvent mEvent = new HandleListClickEvent();
                                    mEvent.onClick(MainView.this.view, bankNamesInCorrectOrder.get(position), urlsInCorrectOrder.get(position), getActivity(), getActivity());
                                }
                            });

                            bankList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    showAlertForHomepage(bankNamesInCorrectOrder.get(position),homepageInCorrectOrder.get(position));
                                    return false;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        AppController.getInstance().addToRequestQueue(request);

    }

    private void showAlertForHomepage(String bName  , final String hPage) {

        String title = "Vill du besöka "+ bName+"s hemsida?";

        if(hasSAsLastDigit(bName)) {
            title = "Vill du besöka "+ bName+ " hemsida?";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setMessage(title)

                .setPositiveButton("Besök lokalt",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        showLocalWebPage(hPage);
                    }
                })
                .setNegativeButton("Besök via webbläsare",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(hPage));
                        startActivity( browse );
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    void showPd() {
        this.pd = new ProgressDialog(getActivity());
        this.pd.setMessage("Laddar data");
        this.pd.show();
    }

    void hidePd() {
        if (this.pd != null) {
            this.pd.dismiss();
        }
    }

    private void openDialogForInput() {
        alert = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.costum_alertbuilder, null);
        final EditText cash = (EditText) v.findViewById(R.id.editText);
        final EditText interval = (EditText) v.findViewById(R.id.editText2);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);

        if (MainActivity.COMPARE) {
            spinner.setSelection(1);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    MainActivity.COMPARE = true;
                } else {
                    MainActivity.COMPARE = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (MainActivity.START_VALUE == 0) {
            cash.setHint("Ditt sparbelopp");
        } else {
            cash.setText("" + MainActivity.START_VALUE);
        }
        if (MainActivity.START_VALUE == 0) {
            interval.setHint("Antal år du vill spara");
        } else {
            interval.setText("" + MainActivity.INTERVAL);
        }
        final SeekBar seekbar = (SeekBar) v.findViewById(R.id.seekBar);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                interval.setText(Integer.toString(progress + 1));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        alert.setView(v);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    double saldo = Double.parseDouble(cash.getText().toString());
                    int intervalString = Integer.parseInt(interval.getText().toString());
                    double breaker = Double.parseDouble("100000000000");

                    if (saldo < breaker) {
                        MainView.this.saldo.setText("" + saldo);
                        MainActivity.START_VALUE = saldo;
                        MainActivity.INTERVAL = intervalString;
                        savePreferences((float) MainActivity.START_VALUE, MainActivity.INTERVAL);
                        if (!MainActivity.COMPARE) {
                            if (MainActivity.BANK != null && MainActivity.ACCOUNT != null && MainActivity.URL != null) {
                                new GraphFragment().updateUI(MainActivity.START_VALUE,
                                        percentCalculation(MainActivity.START_VALUE, MainActivity.RATE, MainActivity.INTERVAL),
                                        MainActivity.INTERVAL, MainActivity.BANK, MainActivity.ACCOUNT, MainActivity.RATE,
                                        MainActivity.URL);
                                updateMainViewUI();
                                MainActivity.instance.pager.setCurrentItem(1);
                            } else {
                                updateMainViewSaldo();
                                Toast.makeText(getActivity(), "Var god välj bank och ett konto", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (MainActivity.bankName.size() >= 2 && MainActivity.account.size() >= 2) {
                                if (MainActivity.account.size() >= 2 && MainActivity.bankName.size() >= 2) {
                                    int index = MainActivity.rate.size();
                                    if (index >= 1) {
                                        index--;
                                    }
                                    percentCalculation(MainActivity.START_VALUE, MainActivity.rate.get(index), MainActivity.INTERVAL);

                                    new GraphFragment().updateUICompare(MainActivity.START_VALUE, MainActivity.high,
                                            MainActivity.INTERVAL, MainActivity.bankName, MainActivity.account, MainActivity.rate);
                                    MainActivity.instance.pager.setCurrentItem(1);
                                }
                            } else {
                                updateMainViewSaldo();
                                if (MainActivity.bankName.size() >= 1 && MainActivity.account.size() >= 1) {
                                    Toast.makeText(getActivity(), "Var god välj ett konto till", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Var god välj banker och konton för jämförelse", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "För stor input", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Fyll i båda fälten", Toast.LENGTH_SHORT).show();
                }

                allowedToPopUp = true;
            }
        });

        alert.setNegativeButton("Avbryt",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        allowedToPopUp = true;
                        dialog.cancel();
                    }
                });
        alert.show();

    }

    public boolean updateMainViewUI() {

        try {
            saldo.setText("" + MainActivity.START_VALUE);
            rate.setText("" + MainActivity.RATE + "%");
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean updateMainViewSaldo() {
        try {
            saldo.setText("" + MainActivity.START_VALUE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateMainViewRate() {
        try {
            rate.setText("" + MainActivity.RATE + "%");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void restoreValues() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Values",
                getActivity().getApplicationContext().MODE_PRIVATE);

        MainActivity.INTERVAL = sharedPref.getInt("interval", 0);
        MainActivity.START_VALUE = sharedPref.getFloat("start_value", 0);

        saldo.setText("" + MainActivity.START_VALUE);
    }

    private void savePreferences(float start, int interval) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Values",
                getActivity().getApplicationContext().MODE_PRIVATE);

        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putInt("interval", interval);
        ed.putFloat("start_value", start);
        ed.apply();
    }

    public boolean hasSAsLastDigit(String s) {

        if(s.charAt(s.length()-1) == 's') {
            return true;
        }

        return false;
    }

    private void showLocalWebPage(String URL) {
        try {
            alert = new AlertDialog.Builder(getActivity());
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View v = factory.inflate(R.layout.homepageviewer, null);
            WebView web = (WebView) v.findViewById(R.id.webView);
            web.loadUrl(URL);
            alert.setView(v);
            alert.show();
        }catch (Exception e) {
            Log.e("ROLLE","ERROR I WE = " + e);
        }

    }

    private void gotoURL(String url) {

    }

    @Override
    public double percentCalculation(double start, double rate, int interval) {
        double checker = start;
        while (interval > 0) {
            start += (divideAndConquer(start, (rate * 1)));
            interval--;
        }
        GraphFragment.setProfit(start - checker);
        GraphFragment.setSaving(start);

        if (MainActivity.COMPARE) {
            MainActivity.high.add(start);
        }
        return start;
    }

    @Override
    public double divideAndConquer(double start, double rate) {

        double holdUpValue = (start / 100);

        holdUpValue = (holdUpValue * rate);

        return holdUpValue;
    }


    private class DownloadFilesTask extends AsyncTask<Cursor, Long, Long> {
        DatabaseConnector dbConn;

        public DownloadFilesTask() {
            dbConn = new DatabaseConnector();
        }

        protected Long doInBackground(Cursor... urls) {

            try {
                while (!dbConn.connectingDatabase()) {

                }
            } catch (Exception e) {
            }

            selectGenBankInfo(dbConn.getConn());
            return null;
        }

        void selectGenBankInfo(Connection conn) {
            try {
                String args = "Select * from Banks";
                dbConn.setStatement(conn.createStatement());
                ResultSet set = dbConn.getStatement().executeQuery(args);

                while (set.next()) {
                    urls.add(set.getString("URL"));
                    bankNames.add(set.getString("Bank"));
                    hasWar.add(set.getBoolean("Warranty"));
                    homepage.add(set.getString("Homepage"));
                }
            } catch (Exception e) {
                Log.e("ROLLE", "" + e);
            }
        }


        protected void onPostExecute(Long result) {

            for (int i = 0; i < urls.size(); i++) {
                initialBankInfo(urls.get(i), bankNames.get(i), hasWar.get(i), homepage.get(i));
            }
            hidePd();

            bankNames.clear();
            homepage.clear();
            urls.clear();

        }
    }
}




