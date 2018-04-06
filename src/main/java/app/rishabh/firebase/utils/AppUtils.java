package app.rishabh.firebase.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.rishabh.firebase.R;

/**
 * Created by rishabh on 06-09-2016.
 */
public class AppUtils {
    private static AppUtils appUtils;
    private ProgressDialog mDialog;

    public static AppUtils getInstance() {
        if (appUtils == null) {
            appUtils = new AppUtils();
        }
        return appUtils;
    }

    /**
     * Validate blank strings
     *
     * @param text
     * @return
     */
    public boolean validate(String text) {
        if (text != null && text.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * check Email
     *
     * @param target
     * @return
     */
    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    /**
     * check Mobile Number
     *
     * @param target
     * @return
     */
    public boolean isValidNumber(CharSequence target) {
        if (target == null || target.length() < 7) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * show snackbar
     *
     * @param message
     * @param view
     */
    public void showSnackBar(String message, View view) {
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action",null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ffffff"));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#2C3139"));
        snackbar.show();
    }

    public void changeFocusToNext(final EditText text1, final EditText text2) {
        text1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (text1.getText().toString().length() == 1) {
                    text2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void afterTextChanged(Editable s) {
            }

        });
    }

    public void changeFocusToPrevious(final EditText text1, final EditText text2) {
        text1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (text1.getText().toString().length() == 0) {
                    text2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void afterTextChanged(Editable s) {
            }

        });
    }


    boolean res = false;


    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

   /* *//**
     * show progress dialog
     *//*
    public void showProgressDialog(Context context) {
        mDialog = new ProgressDialog(context, R.style.MyTheme);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mDialog.setIndeterminateDrawable(context.getResources()
                .getDrawable(R.drawable.progressbar_handler));
        mDialog.show();
    }


    public void showProgressDialogPink(Context context) {
        mDialog = new ProgressDialog(context, R.style.MyTheme);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mDialog.setIndeterminateDrawable(context.getResources()
                .getDrawable(R.drawable.progressbar_pink));
        mDialog.show();
    }*/

    /**
     * hide progress dialog
     */
    public void hideProgressDialog() {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    public void hideNativeKeyboard(Activity pActivity) {
        if (pActivity != null) {
            if (pActivity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) pActivity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(pActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    public void showProgressDialog(Context context) {

        hideProgressDialog();
        mDialog = new ProgressDialog(context, R.style.MyTheme);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progressbar_blue));
        mDialog.show();
    }


    public String getDataFromWeb(String baseUrl) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(baseUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(100000);
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
//read server response
            while ((line = bufferedReader.readLine()) != null) {
//append server response in string
                stringBuilder.append(line);
            }
            line = stringBuilder.toString();
            bufferedReader.close();
            httpURLConnection.disconnect();
            return line;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpURLConnection.disconnect();
        }
    }

    public String formateDate(String date_s) {
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        form.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = form.parse(date_s);
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            form.setTimeZone(tz);
            SimpleDateFormat postFormater = new SimpleDateFormat("MM-dd-yyyy, h:mm a");
            postFormater.setTimeZone(tz);
            //String current=postFormater.format(date);
            //String currentDate[]=current.split("-");
/*
            myear=Integer.parseInt(currentDate[0]);
            mMonth=Integer.parseInt(currentDate[1])-1;
            mday=Integer.parseInt(currentDate[2]);
*/
            return postFormater.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public String formateDateSGT(String date_s) {
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        form.setTimeZone(TimeZone.getTimeZone("SGT"));
        Date date = null;
        try {
            date = form.parse(date_s);
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            form.setTimeZone(tz);
            SimpleDateFormat postFormater = new SimpleDateFormat("MM-dd-yyyy, h:mm a");
            postFormater.setTimeZone(tz);
            //String current=postFormater.format(date);
            //String currentDate[]=current.split("-");
/*
            myear=Integer.parseInt(currentDate[0]);
            mMonth=Integer.parseInt(currentDate[1])-1;
            mday=Integer.parseInt(currentDate[2]);
*/
            return postFormater.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }


    public String getEncyptCardNo(String cardNo) {
        String card="";
        String decrypted_card_num = cardNo;
        int space = (decrypted_card_num.trim().length() / 4) - 1;
        int star = ((decrypted_card_num.trim().length() / 4) - 1) * 4;
        for (int i = 0; i < space; i++) {
            card = card + "****" + " ";
        }
        card = card + decrypted_card_num.substring(star, decrypted_card_num.length());
        return card;

    }
    public void hideKeyBoard(View view, MotionEvent ev, Activity activity) {
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrCoordinates[] = new int[2];
            view.getLocationOnScreen(scrCoordinates);
            float x = ev.getRawX() + view.getLeft() - scrCoordinates[0];
            float y = ev.getRawY() + view.getTop() - scrCoordinates[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((activity.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }

    }


    /**
     * Checks if the input parameter is a valid email.
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {

        if (email == null) {
            return false;
        }

        final String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Matcher matcher;
        Pattern pattern = Pattern.compile(emailPattern);

        matcher = pattern.matcher(email);

        if (matcher != null) {
            return matcher.matches();
        } else {
            return false;
        }
    }

}
