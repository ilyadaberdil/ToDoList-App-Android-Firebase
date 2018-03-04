package com.example.ilyada.tododenemem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.tts.TextToSpeech;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextToSpeech t1;

    public static ArrayList<Notlar> notlar = new ArrayList<>(); //Kullanıcı notları
    CustomAdapter myCustomAdapter;
    SwipeMenuListView listView;
    private List<ApplicationInfo> mAppList;
    TextView tv_notyok;
    private FirebaseAuth auth;
    public static boolean first=true;
    public static String userid;
    public static FirebaseUser user;
    public static FirebaseDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        userid = user.getUid();

        tv_notyok = (TextView)findViewById(R.id.tv_notyok);

        mAppList = getPackageManager().getInstalledApplications(0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this,Not_Olustur_Activity.class));
                Not_Olustur_Activity.ListSelected = false;




            }
        });
                listView = (SwipeMenuListView) findViewById(R.id.lv);
                if(!(notlar.size() > 0)) {
                    listView.setVisibility(View.GONE);
                    tv_notyok.setVisibility(View.VISIBLE);
                }
                else {
                    listView.setVisibility(View.VISIBLE);
                    tv_notyok.setVisibility(View.GONE );
                }

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;
                }
            }
        };
        listView.setMenuCreator(creator);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    Locale locale = new Locale("tr", "TR");
                    t1.setLanguage(locale);
                }
            }
        });

        myCustomAdapter= new CustomAdapter(this,R.layout.not_row,notlar);
        listView.setAdapter(myCustomAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Not_Olustur_Activity.ListPosition = position;
                Not_Olustur_Activity.ListSelected = true;
                finish();
                startActivity(new Intent(MainActivity.this,Not_Olustur_Activity.class));

            }

        });



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                @Override
                                                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                                               int pos, long id) {

               //speech YAPILIYOR.!
                  t1.speak(notlar.get(pos).getIcerik().toString(), TextToSpeech.QUEUE_FLUSH, null);
                  return true;

          }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                ApplicationInfo item = mAppList.get(position);
                switch (index) {
                    case 0:
                        //DÜZENLE EDİT !
                        Not_Olustur_Activity.ListPosition = position;
                        Not_Olustur_Activity.ListSelected = true;
                        finish();
                        startActivity(new Intent(MainActivity.this, Not_Olustur_Activity.class));
                        break;
                    case 1:
                        //PAYLAŞ
                        Intent i = new Intent(android.content.Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Notum");
                        i.putExtra(android.content.Intent.EXTRA_TEXT, "" + notlar.get(position).getBaslik() + "\n" + notlar.get(position).getIcerik());
                        startActivity(Intent.createChooser(i, "Paylaş"));
                        break;

                    case 2:
                        // delete

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(MainActivity.this);
                        }
                        builder.setTitle("Silme İşlemi")
                                .setMessage("Seçilen notu silmek istediğinize emin misiniz? ")
                                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAppList.remove(position);
                                        notlar.remove(position);
                                        Not_Olustur_Activity nesne= new Not_Olustur_Activity();
                                        nesne.veritabani_islemleri();
                                        myCustomAdapter.notifyDataSetChanged();

                                    }
                                })
                                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //hayır..
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                        break;

                case 3:
                // Calendar API EKLE !

                    String s = notlar.get(position).getDeadline_tarihi();
                    String[] split = s.split("/");
                    String day = split[0];
                    String month = split[1];
                    String year = split[2];
                Calendar endTime = Calendar.getInstance();
                endTime.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 17, 50);
                long startMillis = 0;
                    long endMillis = 0;
                    endMillis = endTime.getTimeInMillis();
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(2017, 12, 20, 6, 30);
                startMillis = beginTime.getTimeInMillis();

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("title", notlar.get(position).getBaslik());
                intent.putExtra("description", notlar.get(position).getIcerik());
                intent.putExtra("beginTime", startMillis);
                intent.putExtra("endTime", endMillis);
                startActivity(intent);


            }
                return false;
            }
        });


    }


    @Override
    public void onBackPressed() {
        notlar.clear();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.action_sort_deadline) {
//            notlar.sort(Comparator<notlar>);
            Collections.sort(notlar, Sort_deadlinedate);
            //            Collections.sort(notlar, StringAscComparator);

            myCustomAdapter.notifyDataSetChanged();

        }

        if (id == R.id.action_sort_priority) {
//            notlar.sort(Comparator<notlar>);
            Collections.sort(notlar, Sort_priority);
            //            Collections.sort(notlar, StringAscComparator);

            myCustomAdapter.notifyDataSetChanged();

        }
          if (id == R.id.action_sign_out) {

              auth.signOut();
              notlar.clear();
              Intent intent = new Intent(MainActivity.this, LoginActivity.class);
              startActivity(intent);
              finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void createMenu1(SwipeMenu menu) {
        SwipeMenuItem item1 = new SwipeMenuItem(
                getApplicationContext());


        item1.setBackground(new ColorDrawable(Color.parseColor("#EFEBE9")));
        item1.setWidth(dp2px(50));
        item1.setIcon(R.drawable.ic_edit);
        menu.addMenuItem(item1);
        SwipeMenuItem item2 = new SwipeMenuItem(
                getApplicationContext());


        item2.setWidth(dp2px(50));
        item2.setIcon(R.drawable.ic_share);
        menu.addMenuItem(item2);
        SwipeMenuItem item3 = new SwipeMenuItem(
                getApplicationContext());

        item3.setBackground(new ColorDrawable(Color.parseColor("#EEEEEE")));
        item3.setWidth(dp2px(50));
        item3.setIcon(R.drawable.ic_delete);
        menu.addMenuItem(item3);

        SwipeMenuItem item4 = new SwipeMenuItem(
                getApplicationContext());

        item4.setWidth(dp2px(50));
        item4.setIcon(R.drawable.ic_add);
        menu.addMenuItem(item4);

    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    //Deadline icin tarihleri karsılastır
    public Comparator<Notlar> Sort_deadlinedate = new Comparator<Notlar>() {

        @Override
        public int compare(Notlar o1, Notlar o2) {

            SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");

            try {
                //Date'e convert et..
                Date d=dateFormat.parse(o1.getDeadline_tarihi());
                Date d2=dateFormat.parse(o2.getDeadline_tarihi());

                return d.compareTo(d2);
            }
            catch(Exception e) {

                System.out.println("Excep"+e); // Zorunlu ( Logla kalsın )
                return 0;

            }
        }
    };

    //PRİORITY SORT icin PRIORTY karsılastır
    public Comparator<Notlar> Sort_priority = new Comparator<Notlar>() {

        @Override
        public int compare(Notlar o1, Notlar o2) {

            Integer priorityo1=0,priorityo2=0;
            if(o1.getPriority().equalsIgnoreCase("Düsük")){
                priorityo1 = 3;
            }
            if(o1.getPriority().equalsIgnoreCase("Orta")){
                priorityo1 = 2;
            }
            if(o1.getPriority().equalsIgnoreCase("Yuksek")){
                priorityo1 = 1;
            }
            if(o2.getPriority().equalsIgnoreCase("Düsük")){
                priorityo2 = 3;
            }
            if(o2.getPriority().equalsIgnoreCase("Orta")){
                priorityo2 = 2;
            }
            if(o2.getPriority().equalsIgnoreCase("Yuksek")){
                priorityo2 = 1;
            }
            return priorityo1.compareTo(priorityo2);


        }
    };

    //uygulama pause modda speechi durdursun..
    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }



    }


