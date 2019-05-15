package net.scriptingninja.smsb;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.content;
import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    public static Map<String, String> map = new HashMap<String, String>();


    private static Context mContext;

    public Button sign;
    public LinearLayout register;
    public LinearLayout creditView;
    public TextView textCredit;
    public TextView textPhone;
    public LinearLayout noNetwork;
    public Button refreshNetwork;
    public TextView textSim;
    public Button unRegister;
    public Button buyCredit;
    public LinearLayout billing;


    private static final String PRODUCT_ID = "tencredits"; //"tencredits"; //"com.anjlab.test.iab.s2.p5";
    private static final String MERCHANT_ID = "09592250594168645609"; // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String LICENSE_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArNEaGqvCT4RujyHFItQCwAN45q2BacYYCcBs3xjmNoC21s9oBj1zHHPPZWn0Ya3ldcPrxVJUlXa6zKggvkUY9VrREJ9ZvfWd7a723V4YF+gBL227kxkFKV+u9+nntwSyVnQjNiLj4G5k0ZJ3wdOxdSanKmqrj/rxBw+HoidzCEsFuSPbJHR8LsU2R/h68OXDFEr/2EEzW8Y1VMaRYc5NOGlslhzY3CyXb4CLjo4qTvSFoVh43V/WnysnKCrlPZ9QhCDAdJ5pm70/yjYFT7DiOkCuGNAnPimjBtGP7WV8zkMQs3kG3UFfeIWhpKElZMvyZ5hZAAXeCVRvV/mJqlzfSQIDAQAB";

    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    private boolean registeredSim = false;


    private static final Pattern
            urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new PackageManager.NameNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
    }


    // Called whenever we need to wake up the device
    //public SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
    public static void minus(String sim) throws InterruptedException {

        //final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        PostMethod pmM = new PostMethod();
        Gson gs = new Gson();
        pmM.setList("indentity", sim);
        pmM.setList("minus", "smile");
        String tempString = null;
        AsyncTask<String, Integer, String> tempS = pmM.execute("http://159.203.131.184/api/resms/select/");
        try {
            tempString = tempS.get();
            Log.d("credit minus", tempString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            pmM.getCancel();

        }
    }

    public static class SmsListener extends BroadcastReceiver{

        private SharedPreferences preferences;
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.d("Function", "onReceive");

            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;

                PostMethod pm = new PostMethod();
                Gson gs = new Gson();
                String msg_from;
                String tempString = "";



                PowerManager p = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock mWakeLock = p.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "net.scriptingninja.resms");
                mWakeLock.acquire();
                mWakeLock.release();
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("net.scriptingninja.resms");
                keyguardLock.disableKeyguard();

                openApp(context, "net.scriptingninja.smsb.MainActivity");

                Boolean openUrl = false;
                final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                pm.setList("indentity", tm.getSimSerialNumber());

                AsyncTask<String, Integer, String> tempA = pm.execute("http://159.203.131.184/api/resms/select/");
                try {
                    tempString = tempA.get();
                    if(pm.getStatus(tempString).equals("success")){
                        //Toast.makeText(m, "Successfully Saved!", Toast.LENGTH_SHORT).show();
                        if (bundle != null){
                            //---retrieve the SMS message received---
                            try{
                                Object[] pdus = (Object[]) bundle.get("pdus");
                                msgs = new SmsMessage[pdus.length];

                                StringBuilder myBody = new StringBuilder();
                                int counter = 0;
                                for(int i=0; i<msgs.length; i++){
                                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                                    msg_from = msgs[i].getOriginatingAddress();

                                    String msgBody = msgs[i].getMessageBody();
                                    String phoneTemp = "";
                                    Log.d("---what is back--", tempString);
                                    Resms resms = gs.fromJson(pm.getValue(tempString, "resms"), Resms.class);


                                    //Integer num = Integer.parseInt(pm.getValue(tempString, "credit"));
                                    Log.d("FROM", msg_from);

                                    Log.d("RESMS n", resms.getPhoneNumber());
                                    Log.d("RESMS c", resms.getCredit().toString());
                                    if(msg_from.equals(resms.getPhoneNumber())){
                                        Log.d("check", "Yes");
                                    }else{
                                        Log.d("check", "No");
                                    }
                                    if(resms.getCredit() >= 1){
                                        Log.d("check", "Yes");
                                    }else{
                                        Log.d("check", "No");
                                    }

                                    //17275340278
                                    if(msg_from.equals(resms.getPhoneNumber()) && resms.getCredit() >= 1 && resms.getActive()){
                                        map.put("from", msg_from);
                                        Log.d("MESSAGE BODY", msgBody);
                                        String lines[] = msgBody.split("\\r?\\n");
                                        for(int j = 0; j < lines.length; j++){
                                            Log.d("line "+counter +": ", lines[j]);
                                            myBody.append(lines[j]);
                                            if(counter == 9){
                                                map.put("url", lines[j].split(":")[1] + ":" + lines[j].split(":")[2]);
                                            }
                                            Log.d("split", (lines[j]).split(":")[0]);
                                            String temp = ((lines[j]).split(":")[0]).replace("- ", "");
                                            if(lines[j].split(":").length > 1){
                                                if(!(temp).equals("claim") && !(temp).equals("Click to Claim")){
                                                    map.put(temp, lines[j].split(":")[1]);
                                                }else{
                                                    if(temp.equals("Click to Claim") || (lines[j].split(":")[1]).contains("https://") ){
                                                        map.put("url", (lines[j].split(":")[1]).replace(" ", ""));
                                                    }else {
                                                        if (temp.equals("claim") || temp.equals("lick to claim")) {
                                                            map.put("url", lines[j].split(":")[1] + ":" + lines[j].split(":")[2]);
                                                        }
                                                    }
                                                }
                                            }
                                            counter++;
                                        }
                                    }
                                }
                                Log.d("myBody", myBody.toString());
                                if(!(map.containsKey("url") && (map.get("url").contains("http") || map.get("url").contains("https")))){
                                    Log.d("Do Parse", "Yes");

                                    String result = null;
                                    Matcher matcher = urlPattern.matcher(myBody);
                                    while (matcher.find()) {
                                        int matchStart = matcher.start(1);
                                        int matchEnd = matcher.end();
                                        // now you have the offsets of a URL match
                                        result = myBody.substring(matchStart, matchEnd);
                                    }

                                    if(result.contains("http://") || result.contains("https://")){

                                        openUrl = true;
                                        map.put("url", result);

                                    }

                                }else{
                                    openUrl = false;
                                    Log.d("Do Parse", "No");
                                }


                                Log.d("message", map.toString());
                                map.put("url", map.get("url").replace(" ", ""));
                                //map.put("url", "http://scriptingninja.net");
                                Log.d("URL", map.get("url") );

                                try {
                                    if(openUrl || map.containsKey("url")){
                                        //Uri uri = Uri.parse("googlechrome://navigate?url=" + map.get("url"));
                                        openUrl = true;

                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(map.get("url")));

                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        i.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                                                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                                                + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//
                                        i.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//                                        i.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

//                                        Intent i = new Intent(Intent.ACTION_VIEW);
//                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        i.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//                                        i.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                                        i.putExtra("android.support.customtabs.extra.SESSION", context.getPackageName());
                                        i.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);

                                        context.startActivity(i);
                                        //wait();
                                    }
                                } catch (ActivityNotFoundException e) {
                                    // Chrome is probably not installed
                                   // openUrl = false;
                                    Log.d("error", e.toString());
                                }
                            }catch(Exception e){
                                Log.d("Exception caught",e.getMessage());
                                //openUrl = false;
                            }
                        }

                    }else{
                        Log.d("--- Message -- ", pm.getData(tempString).toString());
                        //Toast.makeText(MainActivity.this, "Error In server", Toast.LENGTH_SHORT).show();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {


                        pm.getCancel();

                        if(openUrl){
                            try{
                                minus(tm.getSimSerialNumber().toString());
                            }catch (InterruptedException e){
                                Log.d("error", e.toString());
                            }
                            openUrl = false;
                        }

                        map.clear();


                }
            }
        }

    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_GRANTED ) {
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    if(isPermissionGranted()){
                        //onResume();
                        clickActions();


                        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                        String st = tm.getSimSerialNumber().toString();
                        refresh(st);
                    }
                    //do ur specific task after read phone state granted
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void refresh(String sim){
        PostMethod pm1 = new PostMethod();
        pm1.setList("indentity", sim);
        AsyncTask<String, Integer, String> tempR = pm1.execute("http://159.203.131.184/api/resms/select/");
        Gson gs = new Gson();
        String tempString;
        EditText phone = (EditText) findViewById(R.id.phoneNumber);
            //do ur specific task after read phone state
            try {
                tempString = tempR.get();
                if(pm1.getStatus(tempString).equals("success")){
                    //Toast.makeText(MainActivity.this, "Loaded information!", Toast.LENGTH_SHORT).show();
                    Log.d("--- Success -- ",tempString);
                    Resms myResms = gs.fromJson(pm1.getValue(tempString, "resms"), Resms.class);
                    Log.d("credit", myResms.getCredit().toString());
                    //Log.d("indentity", pm.getValue(tempString, "indentity"));
                    if(myResms.getCredit() != null && myResms.getIndentity()!=null){
                        textCredit.setText("Your Credit is "+myResms.getCredit());
                        textPhone.setText("Listening to \""+ myResms.getPhoneNumber() +"\"");
                        phone.setText(myResms.getPhoneNumber());
                        if(myResms.getActive()){
                            sign.callOnClick();
                        }else{
                            textPhone.setVisibility(View.INVISIBLE);
                        }
                        Log.d("DATA", tempString);
                        registeredSim = true;
                    }else{
                        textCredit.setText("Your Credit is 0");
                        textPhone.setText("Register your first number \n to get 10 credits for free");
                        registeredSim = false;
                    }
                    register.setVisibility(View.VISIBLE);
                    creditView.setVisibility(View.VISIBLE);
                    billing.setVisibility(View.VISIBLE);
                    noNetwork.setVisibility(View.GONE);



                    textSim.setText("Your sim number is "+sim);

                }else{
                    Log.d("--- Message -- ", pm1.getData(tempString).toString());
                    Toast.makeText(MainActivity.this, "Error In server or No Network", Toast.LENGTH_SHORT).show();
                    register.setVisibility(View.INVISIBLE);
                    creditView.setVisibility(View.INVISIBLE);
                    billing.setVisibility(View.INVISIBLE);
                    noNetwork.setVisibility(View.VISIBLE);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }finally {
                pm1.getCancel();
            }
    }

    private SmsListener smsListener = new SmsListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//
//        PowerManager p = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = p.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
//        wakeLock.acquire();
//        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
//        keyguardLock.disableKeyguard();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        buyCredit = (Button) findViewById(R.id.buyCredit);
        final String tmDevice, tmSerial, androidId;
        //tmDevice = "" + tm.getDeviceId();

        if(!BillingProcessor.isIabServiceAvailable(this)) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }
        //        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
        clickActions();
        bp = new BillingProcessor(this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                //showToast("onProductPurchased: " + productId);



                PostMethod pm = new PostMethod();

                try{
                    final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                    pm.setList("indentity", tm.getSimSerialNumber().toString());
                    AsyncTask<String, Integer, String> tempR = pm.execute("http://159.203.131.184/api/resms/buy/");
                    Gson gs = new Gson();
                    String tempString;
                    tempString = tempR.get();
                    if(pm.getStatus(tempString).equals("success")) {
                        refresh(tm.getSimSerialNumber());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }finally {
                    showToast("Thank you for your Purchase!");
                    bp.consumePurchase(productId);
                    pm.getCancel();
                }




                //updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, Throwable error) {
                showToast("Purchases Didn't Complete: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
                //showToast("onBillingInitialized");
                Log.d("BILLING", "onBillingInitialized");
                readyToPurchase = true;
                //updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                //showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d("LOG", "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d("LOG", "Owned Subscription: " + sku);
                //updateTextViews();
            }
        });
        //buyCredit
    }
    @Override
    public void onResume() {
        super.onResume();
        if(isPermissionGranted()){
            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            refresh(tm.getSimSerialNumber());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("data Back"," data: "+ data+ " request code: "+ requestCode+ " resultCode: "+ requestCode);
        }
    }

    private void clickActions(){

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        sign = (Button) findViewById(R.id.rSMSM);
        register = (LinearLayout) findViewById(R.id.register);
        creditView = (LinearLayout) findViewById(R.id.creditView);
        billing = (LinearLayout) findViewById(R.id.billing);
        textCredit = (TextView) findViewById(R.id.textCredit);
        noNetwork = (LinearLayout) findViewById(R.id.noNetwork);
        refreshNetwork = (Button) findViewById(R.id.refreshNetwork);
        textPhone = (TextView) findViewById(R.id.textPhone);
        textSim = (TextView) findViewById(R.id.textSim);
        unRegister = (Button) findViewById(R.id.urSMSM);
        unRegister.setVisibility(View.INVISIBLE);
        mContext = this;
        //final Window win= getWindow();
        //win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        final EditText phone1;

        String tmSerial;

        phone1 = (EditText) findViewById(R.id.phoneNumber);
        if(!isPermissionGranted()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS}, 1);
            return;
        }
        final Gson gs = new Gson();
        String tempString;

        //tmSerial = "" + tm.getSimSerialNumber().toString();
        PostMethod pm = new PostMethod();
        pm.setList("indentity", tm.getSimSerialNumber().toString());
        //refresh(tmSerial);

        unRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SmsListener smsListener = new SmsListener();
                PostMethod pm = new PostMethod();

                pm.setList("indentity", tm.getSimSerialNumber().toString());
                pm.setList("active", "f");
                AsyncTask<String, Integer, String> tempA = pm.execute("http://159.203.131.184/api/resms/insert/");

                try {
                    String tempString = tempA.get();
                    if(pm.getStatus(tempString).equals("success")) {
                        try{
                            unregisterReceiver(smsListener);
                        }catch (IllegalArgumentException e){
                            Log.d("ERROR", e.toString());
                        }
                        Toast.makeText(MainActivity.this, "Unregister to SMS", Toast.LENGTH_SHORT).show();
                        phone1.setVisibility(View.VISIBLE);
                        sign.setVisibility(View.VISIBLE);
                        textPhone.setVisibility(View.INVISIBLE);
                        textPhone.setVisibility(View.INVISIBLE);
                        unRegister.setVisibility(View.INVISIBLE);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }finally {
                    pm.getCancel();
                }


            }


        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostMethod pm = new PostMethod();
                String tempString;
                //tmSerial = tm.getSimSerialNumber().toString();
                if( !(phone1.getText()).toString().equals("")) {
                    pm.setList("phoneNumber", phone1.getText().toString());
                    Log.d("i", tm.getSimSerialNumber().toString());
                    Log.d("p", phone1.getText().toString());
                    pm.setList("indentity", tm.getSimSerialNumber().toString());
                    //pm.setList("active", 1);
                    pm.setList("active", "true");
                    AsyncTask<String, Integer, String> tempA = pm.execute("http://159.203.131.184/api/resms/insert/");
                    try {
                        tempString = tempA.get();
                        if(pm.getStatus(tempString).equals("success")){
                            Toast.makeText(MainActivity.this, "Successfully Completed!", Toast.LENGTH_SHORT).show();
                            Resms myResms = gs.fromJson(pm.getValue(tempString, "resms"), Resms.class);
                            Log.d("credit", myResms.getCredit().toString());
                            if(myResms.getCredit() != null){
                                textCredit.setText("Your Credit is "+myResms.getCredit());
                            }else{
                                textCredit.setText("Your Credit is 0");
                            }
                            phone1.setText("");
                            registeredSim = true;
                            textPhone.setText("Listening to \""+ myResms.getPhoneNumber() +"\"");
                            textSim.setText("Your sim number is "+tm.getSimSerialNumber().toString());

                            String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
                            //unregisterReceiver(smsListener);


                            if(smsListener == null){
                                //try {
                                //unregisterReceiver(smsListener);
                                registerReceiver(smsListener, new IntentFilter(SMS_RECEIVED));
                                //}catch (IllegalArgumentException e){
                                //  registerReceiver(smsListener, new IntentFilter(SMS_RECEIVED));
                                //}
                            }

                            phone1.setVisibility(View.INVISIBLE);
                            sign.setVisibility(View.INVISIBLE);
                            textPhone.setVisibility(View.VISIBLE);
                            unRegister.setVisibility(View.VISIBLE);
                            Log.d("Function", "SmsListener");
                        }else{
                            Log.d("--- Message -- ", pm.getData(tempString).toString());
                            Toast.makeText(MainActivity.this, "Error In server or Invalid Number ", Toast.LENGTH_SHORT).show();
                            phone1.setVisibility(View.VISIBLE);
                            sign.setVisibility(View.VISIBLE);
                            textPhone.setVisibility(View.INVISIBLE);
                            unRegister.setVisibility(View.INVISIBLE);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }finally {
                        pm.getCancel();
                    }
                }else{
                    if(isPermissionGranted()){
                        Toast.makeText(MainActivity.this, "Please Enter Phone to Read", Toast.LENGTH_SHORT).show();
                    }else{
                        showToast("Please Check Permission");
                    }
                }

            }

        });


        buyCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readyToPurchase && registeredSim){
                    bp.purchase(MainActivity.this, PRODUCT_ID);
                }else{
                    showToast("Please enter register a number first");
                }

            };
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy(){
        if(bp != null)
            bp.release();

        super.onDestroy();
    }


}
