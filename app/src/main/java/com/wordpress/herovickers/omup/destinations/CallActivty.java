package com.wordpress.herovickers.omup.destinations;

import android.content.Intent;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.wordpress.herovickers.omup.models.RecentCall;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.Listeners.EndPointListner;
import com.wordpress.herovickers.omup.utility.MyAsteriskSettings;
import com.wordpress.herovickers.omup.utility.Phone;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.utility.PrefsManager;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;


import org.asteriskjava.pbx.Call;
import org.asteriskjava.pbx.CallerID;
import org.asteriskjava.pbx.EndPoint;
import org.asteriskjava.pbx.PBX;
import org.asteriskjava.pbx.PBXException;
import org.asteriskjava.pbx.PBXFactory;
import org.asteriskjava.pbx.TechType;
import org.asteriskjava.pbx.Trunk;
import org.asteriskjava.pbx.activities.DialActivity;
import org.asteriskjava.pbx.internal.core.AsteriskPBX;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CallActivty extends AppCompatActivity implements EndPointListner {
    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";
    public Endpoint endpoint;
    public Incoming incoming;
    public Outgoing outgoing;
    String TAG="CallActivty ";
    TextView contactName;
    Button callBtn;
    FloatingActionButton Hangupbtn;
    String newString;
    TextView phoneNumber,tv_speaker;
    TextView callDuration;
    Chronometer choronoMeter;
    LinearLayout callConnectedLayout;
    LinearLayout incomingCallsLayout;
    private String callType;
    private long createdAt;
    private String lastCallDuration = "00:00";
    private String stringFullName="";
    private String stringPhoneNumber="";
    User user ;
    long elapsedTime;
    Boolean resume = false;
    long minutes;
    long seconds;
    private String mDefaultFormat = "%02d:%02d:%02d";

    /**This activity handles, Incoming and Outgoing Calls*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        setContentView(R.layout.activity_call);
        user = new PrefsManager(this).getUserData();
        if(user==null)
        {
            Log.d(TAG,"user is null "+user);
        }else
            Log.d(TAG,"user is not null "+user);
        callConnectedLayout = findViewById(R.id.call_connected_layout);
        incomingCallsLayout = findViewById(R.id.incoming_call_layout);
        /**Get extras from the intent that started this activity, If it is an incoming call,
         * set the visibiliy of callConnectedLayout to GONE and make incoming calls visble then set
         * the callDuration TextView to "INCOMING CALL".
         * And if call is connected, set the visibility of incomingCallsLayout to GONE and make
         * callCOnnectedLayout visible*/

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Initialize

/*
        try {
            PBXFactory.init(new MyAsteriskSettings());
            AsteriskPBX asteriskPbx = (AsteriskPBX) PBXFactory.getActivePBX();
            asteriskPbx.createAgiEntryPoint();
        }
        catch(java.io.IOException | org.asteriskjava.manager.AuthenticationFailedException | org.asteriskjava.manager.TimeoutException e) {
            Log.d("CreateAgiEntryPoint", "AuthenticationFailed or Timeout  "+e.toString());
            System.out.println(e);
        }
*/
        // Initialize Asterisk
        try {
            PBXFactory.init(new MyAsteriskSettings());

            AsteriskPBX asteriskPbx = (AsteriskPBX) PBXFactory.getActivePBX();
            asteriskPbx.createAgiEntryPoint();
        }
        catch(Exception e) {
            Log.d("CreateAgiEntryPoint", "AuthenticationFailed or Timeout  "+e.toString());
            System.out.println(e);
        }
        //Set contact name and phone number
        contactName = findViewById(R.id.contact_name);
        phoneNumber = findViewById(R.id.phone_number);
        choronoMeter = findViewById(R.id.cmTimer);
        tv_speaker=findViewById(R.id.tv_speaker);
                //TODO set call duration to "Calling" call is not connected yet and set to count call duration when connected
        callDuration = findViewById(R.id.call_duration);


        Intent intent = getIntent();

        callType = intent.getType();
        Log.d("callType=",""+callType);
        if (callType!=null && callType.equals("Outgoing")){
            callDuration.setText("Calling...");
        }else {
            //TODO set Timer to start counting then format properly and store it in @lastCallDuration variable
        }
        stringFullName = ""+intent.getStringExtra("CONTACT_NAME");
        stringPhoneNumber = ""+intent.getStringExtra("CONTACT_NUMBER");
        contactName.setText(stringFullName);
        phoneNumber.setText(stringPhoneNumber);
        //Get the timestamp of when the call starts either incoming or outgoing
        createdAt = System.currentTimeMillis();

        endpoint = Phone.getInstance(this).endpoint;
        incoming = Phone.getInstance(this).incoming;
        outgoing = new Outgoing(endpoint);

        Hangupbtn = findViewById(R.id.hangBtn);
        /**Call Button eliminated, the call action should begin immediately this activity is started*/
        //callBtn = findViewById(R.id.callBtn);
        /*if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            *//**Get contact name extras from string too and setText on contactName Textview*//*
            if (extras == null) {
                newString = null;
            } else {
                newString = extras.getString("phone");
            }
        } else {
            newString = (String) savedInstanceState.getSerializable("phone");
        }*/

        Hangupbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveCallInformationToDatabase();
            }
        });
        /*try
        {
            PBX pbx = PBXFactory.getActivePBX();

            // We are going to dial from extension 100 to 5551234

            // The trunk MUST match the section header (e.g. [default]) that appears
            // in your /etc/asterisk/sip.d file (assuming you are using a SIP trunk).
            // The trunk is used to select which SIP trunk to dial through.
            Trunk trunk = pbx.buildTrunk("5244877354gw1");

            // We are going to dial from extension 100
            EndPoint from = pbx.buildEndPoint(TechType.SIP, "100");
            // Provide confirmation to the agent which no. we are dialing by
            // showing it on their handset.
            CallerID fromCallerID = pbx.buildCallerID("12028883999", "Dialing");

            // The caller ID to display on the called parties phone.
            // On most systems the caller id name part won't display
            CallerID toCallerID = pbx.buildCallerID("12028883999", "... is calling");
            // The party we are going to call.
            EndPoint to = pbx.buildEndPoint(TechType.SIP, trunk, newString);

            // Now dial. This method won't return until the call is answered
            // or the dial timeout is reached.
            final DialActivity dial = pbx.dial(from, fromCallerID, to, toCallerID);

            // We should have a live call here.


//            Thread.sleep(20000);

            // Bit tired now so time to hangup.
            Hangupbtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    try {
                        PBX pbx = PBXFactory.getActivePBX();
                        Call call = dial.getNewCall();
                        pbx.hangup(call);
                    }
                    catch(PBXException e){
                        System.out.println(e);
                    }
                    saveCallInformationToDatabase();

                }
            });

        }
        catch (Error e)
        {
            System.out.println(e);
        }*/


      /*  tv_speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
Log.e("tv_speaker","createdAt="+createdAt+"    lastCallDuration="+ lastCallDuration);
            }
        });*/
        if (!resume) {
            choronoMeter.setBase(SystemClock.elapsedRealtime());
            choronoMeter.start();
        } else {
            choronoMeter.start();
        }
        setUpChronometerCallTm();
    }

    private void setUpChronometerCallTm() {
        choronoMeter.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer arg0) {
                if (!resume) {
                      minutes = ((SystemClock.elapsedRealtime() - choronoMeter.getBase())/1000) / 60;
                      seconds = ((SystemClock.elapsedRealtime() - choronoMeter.getBase())/1000) % 60;
                    elapsedTime = SystemClock.elapsedRealtime();
                     long seconds = (SystemClock.elapsedRealtime() - choronoMeter.getBase()) / 1000;
                    int hh = (int)(seconds / 3600);
                    int mm = (int)((seconds % 3600) / 60);
                    int ss = (int)(seconds % 60);
                    String strText = String.format(Locale.US, mDefaultFormat, hh, mm,ss);
                choronoMeter.setText(strText);
                    Log.d(TAG, "onChronometerTick: hh=" + hh + " : mm=" +mm+" : ss="+ss);
                } else {
                      minutes = ((elapsedTime - choronoMeter.getBase())/1000) / 60;
                      seconds = ((elapsedTime - choronoMeter.getBase())/1000) % 60;
                    elapsedTime = elapsedTime + 1000;
                    long seconds = (SystemClock.elapsedRealtime() - choronoMeter.getBase()) / 1000;
                    int hh = (int)(seconds / 3600);
                    int mm = (int)((seconds % 3600) / 60);
                    int ss = (int)(seconds % 60);
                    String strText = String.format(Locale.US, mDefaultFormat, hh, mm,ss);
                    choronoMeter.setText(strText);
                    Log.d(TAG, "onChronometerTick: hh=" + hh + " : mm=" +mm+" : ss="+ss);
                }
            }
        });

    }

    private void saveCallInformationToDatabase() {
String strMinutes=""+minutes;
        String strScnds=""+seconds;
        choronoMeter.stop();
if( (strMinutes).length()==1)
{
    strMinutes="0"+strMinutes;
}
        if( (strScnds).length()==1)
        {
            strScnds="0"+strScnds;
        }
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("fullName", stringFullName);
        userInfo.put("phoneNumber", stringPhoneNumber);
        RecentCall recentCall = new RecentCall(callType, createdAt,choronoMeter.getText().toString()/*strMinutes+":"+strScnds*/, userInfo);
        FirestoreViewModel viewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        //save to User Document
        viewModel.saveRecentCallInformation(recentCall);
        //Save to all Conversation Document
        viewModel.saveRecentCallInfoSP(recentCall, getCombinedId());
        Intent intent = new Intent(CallActivty.this, HomeActivity.class);
        startActivity(intent);
    }

    private String getCombinedId() {
        String userPhoneNumber =""+ user.getPhoneNumber();
        Log.e(TAG,userPhoneNumber+"     "+user);
        if(stringPhoneNumber.length()>0)
        {
            stringPhoneNumber=stringPhoneNumber.replaceAll("\\s","");
        }
        String receiverPhoneNumber = stringPhoneNumber;
        if (receiverPhoneNumber.length()>0 && receiverPhoneNumber.charAt(0) == '+'){
            receiverPhoneNumber = "0"+receiverPhoneNumber.substring(4);
        }
        if (userPhoneNumber.charAt(0) == '+'){
            userPhoneNumber = "0"+userPhoneNumber.substring(4);
        }
        if (Long.valueOf(userPhoneNumber) > Long.valueOf(receiverPhoneNumber)){
            return userPhoneNumber+receiverPhoneNumber;
        }else{
            return receiverPhoneNumber+userPhoneNumber;
        }
    }


    public void makeCall(View view) {


//        callBtn.setText("Hangup");
        Log.d("value of text", newString);

//        outgoing = Phone.getInstance(this).createOutgoingCall();

//        outgoing.call(newString);

        //TODO change to callDuration.setText() instead and set the call time when call is connected
        callDuration.setVisibility(View.VISIBLE);
        //callBtn.setVisibility(View.GONE);
        //Hangupbtn.setVisibility(View.VISIBLE);

//            } else {
//
//                Log.d("Outgoing", "Hangup");
//
//                outgoing.hangup();
//
//                CallActivty.this.runOnUiThread(new Runnable()
//                {
//                    public void run()
//                    {
//                        callBtn.setText("Make a call");
//
//                    }
//                });
//            }



    }

    public void onLogin() {

        Log.d("PlivoInbound", "Logging in");

    }

    public void onLogout() {

        Log.d("PlivoInbound", "Logged out");
        finish();

    }

    public void onLoginFailed() {

        Log.d("PlivoInbound", "Login failed");

    }

    public void onIncomingCall(Incoming incoming) {
    Log.d("PlivoInbound", "Incoming call received");
//
//        this.incoming = incoming;
//
//        this.incoming.answer();
//
//        VoiceActivity.this.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                declineBtn.setText("Decline");
//
//            }
//        });
    }

    public void dial()
    {
        try
        {
            PBX pbx = PBXFactory.getActivePBX();

            // We are going to dial from extension 100 to 5551234

            // The trunk MUST match the section header (e.g. [default]) that appears
            // in your /etc/asterisk/sip.d file (assuming you are using a SIP trunk).
            // The trunk is used to select which SIP trunk to dial through.
            Trunk trunk = pbx.buildTrunk("5244877354gw1");
/*your server is ready, SIP credential to test are extensions 101, 102, 103, 104 all password test1234 host = 80.211.134.130:8631
SIP port is 8631, you can register these extensions and call each other, in case of any help required please let me know*/


            // We are going to dial from extension 100
            EndPoint from = pbx.buildEndPoint(TechType.SIP, "101");
            // Provide confirmation to the agent which no. we are dialing by
            // showing it on their handset.
            CallerID fromCallerID = pbx.buildCallerID("12028883999", "Dialing");

            // The caller ID to display on the called parties phone.
            // On most systems the caller id name part won't display
            CallerID toCallerID = pbx.buildCallerID("12028883999", "... is calling");
            // The party we are going to call.
            EndPoint to = pbx.buildEndPoint(TechType.SIP, trunk, newString);

            // Now dial. This method won't return until the call is answered
            // or the dial timeout is reached.
            DialActivity dial = pbx.dial(from, fromCallerID, to, toCallerID);

            // We should have a live call here.


//            Thread.sleep(20000);

            // Bit tired now so time to hangup.

        }
        catch (Error e)
        {
            System.out.println(e);
        }

    }

    public void onIncomingCallHangup(Incoming incoming) {

//        VoiceActivity.this.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//
//                declineBtn.setText("Decline");
//
//            }
//        });
    }

    public void onIncomingCallRejected(Incoming incoming) {


//        VoiceActivity.this.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                declineBtn.setText("Decline");
//
//            }
//        });
    }

    public void onOutgoingCall(Outgoing outgoing) {

    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {

//        VoiceActivity.this.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                callBtn.setText("Make a call");
//
//            }
//        });
    }

    public void onOutgoingCallHangup(Outgoing outgoing) {

//        VoiceActivity.this.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                callBtn.setText("Make a call");
//
//            }
//        });
    }

    public void onOutgoingCallInvalid(Outgoing outgoing) {

//        VoiceActivity.this.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                callBtn.setText("Make a call");
//
//            }
//        });
    }

    public void onIncomingDigitNotification(String digits) {


//        VoiceActivity.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                {
//                    Log.d("DTMF value is: ",digits);
//                    switch (digits) {
//                        case "-6":
//                            Toast.makeText(VoiceActivity.this, "Incoming DTMF: " + "*", Toast.LENGTH_SHORT).show();
//                            break;
//                        case "-13":
//                            Toast.makeText(VoiceActivity.this, "Incoming DTMF: " + "#", Toast.LENGTH_SHORT).show();
//                            break;
//                        default:
//                            Toast.makeText(VoiceActivity.this, "Incoming DTMF: " + digits, Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//
//                }
//
//            }
//        });

    }
}
