package com.example.ilyada.tododenemem;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Not_Olustur_Activity extends AppCompatActivity {
    FirebaseDatabase db;


    Button kaydet;
    ImageButton btn_speech_icerik, btn_speech_baslik;
    EditText tv_icerik, tv_baslik;
    Spinner spinner_kategori;
    Spinner spinner_priority;
    String[] items_priority = {"Düsük", "Orta", "Yuksek"};
    String[] items_kategori = {"Is", "Okul", "Aile", "Diger"};
    DatePicker datePicker;
    public static Boolean ListSelected;
    public static int ListPosition;
    public static Notlar notum = new Notlar(); //Listviewden secilen not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_not_olustur);
        init();
        MainActivity.first = false;

        ArrayAdapter<String> adapter_priority = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items_priority);

        ArrayAdapter<String> adapter_kategori = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items_kategori);

        spinner_priority.setAdapter(adapter_priority);
        spinner_kategori.setAdapter(adapter_kategori);

        if (ListSelected) {

            for (int i = 0; i < 3; i++) {
                if (spinner_kategori.getItemAtPosition(i).toString().equalsIgnoreCase( MainActivity.notlar.get(ListPosition).getKategori()))
                    spinner_kategori.setSelection(i);
                if (spinner_priority.getItemAtPosition(i).toString().equalsIgnoreCase( MainActivity.notlar.get(ListPosition).getPriority()))
                    spinner_priority.setSelection(i);
            }
            tv_baslik.setText("" + MainActivity.notlar.get(ListPosition).getBaslik());
            tv_icerik.setText("" + MainActivity.notlar.get(ListPosition).getIcerik());

            String[] parts = MainActivity.notlar.get(ListPosition).deadline_tarihi.split("/");
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            datePicker.init(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), null);

        }

        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ListSelected) {
//               yeni not olustur..
                    Notlar yeni_not = new Notlar();
                    yeni_not.setBaslik(tv_baslik.getText().toString());
                    yeni_not.setIcerik(tv_icerik.getText().toString());

                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth() + 1;
                    int year = datePicker.getYear();
                    yeni_not.setDeadline_tarihi(Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year));

                    String formattedDate = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    yeni_not.setYazim_tarihi(formattedDate);

                    yeni_not.setKategori(spinner_kategori.getSelectedItem().toString());
                    yeni_not.setPriority(spinner_priority.getSelectedItem().toString());
                    MainActivity.notlar.add(yeni_not);



//                    NOTIFICATION ICIN DELAY HESAPLAMALARIM....

                    int delay_year = year - Calendar.getInstance().get(Calendar.YEAR);
                    int delay_month = month - (Calendar.getInstance().get(Calendar.MONTH)+1);
                    int delay_day = day - Calendar.getInstance().get(Calendar.DATE);

                    double delay_year_mili = 3155760000.0 * delay_year;
                    double delay_month_mili = TimeUnit.DAYS.toMillis(delay_month*30);
                    double delay_day_mili = TimeUnit.DAYS.toMillis(delay_day);

                    long delay = (long) ( delay_day_mili  + delay_month_mili + delay_year_mili);


                    // PUSH NOTIFICATION..
                    scheduleNotification(getNotification("5 second delay"), delay);


                }
                else {

                    //Var olan notu güncelle
                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth() + 1;
                    int year = datePicker.getYear();

                    String formattedDate = java.text.DateFormat.getDateTimeInstance().format(new Date());


                    MainActivity.notlar.get(ListPosition).setPriority(spinner_priority.getSelectedItem().toString());
                    MainActivity.notlar.get(ListPosition).setKategori(spinner_kategori.getSelectedItem().toString());
                    MainActivity.notlar.get(ListPosition).setYazim_tarihi(formattedDate);
                    MainActivity.notlar.get(ListPosition).setDeadline_tarihi(Integer.toString(day) + "/" +
                            Integer.toString(month) + "/" + Integer.toString(year));
                    MainActivity.notlar.get(ListPosition).setIcerik(tv_icerik.getText().toString());
                    MainActivity.notlar.get(ListPosition).setBaslik(tv_baslik.getText().toString());




                    //       Güncel degerler üstünden             NOTIFICATION ICIN DELAY HESAPLAMALARIM....

                    int delay_year = year - Calendar.getInstance().get(Calendar.YEAR);
                    int delay_month = month - (Calendar.getInstance().get(Calendar.MONTH)+1);
                    int delay_day = day - Calendar.getInstance().get(Calendar.DATE);

                    double delay_year_mili = 3155760000.0 * delay_year;
                    double delay_month_mili = TimeUnit.DAYS.toMillis(delay_month*30);
                    double delay_day_mili = TimeUnit.DAYS.toMillis(delay_day);

                    long delay = (long) ( delay_day_mili  + delay_month_mili + delay_year_mili);


                    // PUSH NOTIFICATION..
                    scheduleNotification(getNotification("5 second delay"), delay);


                }
                veritabani_islemleri();
                finish();
                startActivity(new Intent(Not_Olustur_Activity.this,MainActivity.class));

            }
        });

        btn_speech_icerik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.speech_get_icerik));
                try {

                    startActivityForResult(intent, 1 );  //kod 1 > speech ile içerik yazma !!

                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.speech_destek),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_speech_baslik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.speech_get_baslik));
                try {

                    startActivityForResult(intent, 2 );  //kod 1 > speech ile içerik yazma !!

                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.speech_destek),
                            Toast.LENGTH_SHORT).show();
                }



            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //kod 1 -> speech ile içerik yazma !!
            case 1: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> notum = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tv_icerik.setText(notum.get(0));
                }
                break;
            }

            //kod 2 -> speech ile baslik yazma !!
            case 2: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> not_basligi = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tv_baslik.setText(not_basligi.get(0));

                }
                break;
            }

//            //REHBERDEN BİLGİ ALMA !
//            case 3: {
//                if (resultCode == Activity.RESULT_OK) {
//                    Uri contactData = data.getData();
//                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
//                    if (c.moveToFirst()) {
//                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.));
//                        Toast.makeText(Not_Olustur_Activity.this,""+name,Toast.LENGTH_LONG).show();
//                        // TODO Whatever you want to do with the selected contact name.
//                    }
//                }
//                break;
//            }
        }
    }

    private void init(){
        btn_speech_baslik = (ImageButton)findViewById(R.id.btn_speech_baslik);

        db=FirebaseDatabase.getInstance();
        btn_speech_icerik = (ImageButton)findViewById(R.id.btn_speech_icerik);
        datePicker = (DatePicker) findViewById(R.id.datepicker);
        tv_baslik = (EditText) findViewById(R.id.tv_baslik);
        tv_icerik = (EditText) findViewById(R.id.tv_icerik);
        datePicker = (DatePicker) findViewById(R.id.datepicker);
        spinner_kategori=(Spinner)findViewById(R.id.spinner_kategori);
        spinner_priority=(Spinner)findViewById(R.id.spinner_priority);
        kaydet=(Button)findViewById(R.id.btn_kaydet);

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Not_Olustur_Activity.this,MainActivity.class));
//    <.    notum =null;
    }


    private void scheduleNotification(Notification notification,long delay) {

        Intent notificationIntent = new Intent(this, Notipublish.class);
        notificationIntent.putExtra(Notipublish.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(Notipublish.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Yapılacakları Unutma!");
        builder.setContentText("Deadline'ı gelen notlarını kontrol et !");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    public void veritabani_islemleri() {

        DatabaseReference dbrefuser=MainActivity.db.getReference(MainActivity.userid);
        dbrefuser.removeValue(); //kayıtlı kullanıcı verileri için

        DatabaseReference dbRef = MainActivity.db.getReference(MainActivity.userid + "/Notlar/");

        for (int i = 0; i < MainActivity.notlar.size(); i++) {
            if (MainActivity.notlar.get(i).getBaslik().length() > 0) {

                String key = dbRef.push().getKey();
                DatabaseReference dbRefkeyli = MainActivity.db.getReference(MainActivity.userid + "/Notlar/" + key);
                dbRefkeyli.setValue(MainActivity.notlar.get(i));

            }
            else {
                String key = dbRef.push().getKey();
                DatabaseReference dbRefkeyli = MainActivity.db.getReference(MainActivity.userid + "/Notlar/" + key);
                dbRefkeyli.removeValue();
            }
        }
    }


}
