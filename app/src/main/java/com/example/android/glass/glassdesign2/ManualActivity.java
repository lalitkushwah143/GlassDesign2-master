package com.example.android.glass.glassdesign2;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.example.android.glass.glassdesign2.data.DataLyo;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.LyoLayoutFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ManualActivity extends BaseActivity {

    private List<BaseFragment> fragments = new ArrayList<>();
    public static CustomViewPager viewPager;
    private TabLayout indicator;

    private ArrayList<DataLyo> arrayList=new ArrayList<>();

    private final String ACCESS_TOKEN="ds7P8ue7X0PmwaS-pSYAIZqbCZ1ZhrwO4iASYCmsTJY";
    private final String SPACE_ID= "h7copa94aofe";

    private static final int REQUEST_CODE = 201;
    private String MENU_KEY="menu_key";

    public static Boolean FLAG5 = false;
    public static Boolean FLAG8 = false;
    private TextView textView;
    private String manual_key;
    public static int count_pages;

    private final CDAClient client=CDAClient
            .builder()
            .setToken(ACCESS_TOKEN)
            .setSpace(SPACE_ID)
            .setEnvironment("master")
            .build();

    final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
            getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_layout);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        textView = findViewById(R.id.activity_manual_tvPage);
        arrayList=new ArrayList<>();

        manual_key = getIntent().getStringExtra("manual_type");

        indicator = findViewById(R.id.page_indicator);
        indicator.setupWithViewPager(viewPager, true);

        indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                textView.setText((indicator.getSelectedTabPosition()+1) + " of "+ indicator.getTabCount());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (arrayList.isEmpty()) {

        //    FLAG5 = false;
         //   FLAG8 = false;
            client
                    .observe(CDAEntry.class)
                    .withContentType(manual_key)
                    .all()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<CDAArray>() {
                        @Override
                        public void accept(CDAArray entries) {

                            arrayList = new ArrayList<>();
                            for (final CDAResource cdaResource: entries.items()){

                                final CDAEntry entry = (CDAEntry) cdaResource;

                                double id = entry.getField("id");
                                String step = entry.getField("step");
                                String type = entry.getField("type");
                                String title = entry.getField("title");
                                CDAAsset asset = entry.getField("image");
                                String url = "";
                                if (asset!= null){
                                    url = asset.url();
                                }
                                String desc = entry.getField("desc");

                                Log.e("This", step);
                                arrayList.add(arrayList.size(), new DataLyo(id, step, type, title, url, desc));
                            }

                            fragments.clear();

                            Collections.sort(arrayList, new Comparator<DataLyo>() {
                                @Override
                                public int compare(DataLyo lyo, DataLyo lyo1) {
                                    return Double.valueOf(lyo.getId()).compareTo(Double.valueOf(lyo1.getId()));
                                }
                            });
                            count_pages = arrayList.size();
                            for (int i=0; i<arrayList.size(); i++){
                                DataLyo dataLyo = arrayList.get(i);
                                Log.e("This 2", dataLyo.getStep());
                                fragments.add(LyoLayoutFragment.newInstance(dataLyo.getId(), dataLyo.getStep(), dataLyo.getType(), dataLyo.getTitle(), dataLyo.getUrl(), dataLyo.getDesc()));
                            }
                            screenSlidePagerAdapter.notifyDataSetChanged();
                            viewPager.setAdapter(screenSlidePagerAdapter);
                        }
                    });
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 4 && !FLAG5 && manual_key.equals(getString(R.string.filter_batch))){
                    viewPager.setPagingEnabled(false);
                }else if (position == 7 && !FLAG8 && manual_key.equals(getString(R.string.filter_batch))){
                    viewPager.setPagingEnabled(false);
                }else {
                    viewPager.setPagingEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case SWIPE_UP:
                Intent intent = new Intent(ManualActivity.this, MenuActivity.class);
                intent.putExtra(MENU_KEY, R.menu.menu_call);
                startActivityForResult(intent, REQUEST_CODE );
                return true;

            case TAP:
                if (manual_key.equals(getString(R.string.filter_batch)) && (viewPager.getCurrentItem() == 4 || viewPager.getCurrentItem() == 7)){
                    Intent cameraIntent = new Intent(this.getApplicationContext(), CameraActivity.class);
                    cameraIntent.putExtra("step", viewPager.getCurrentItem()+1);
                    startActivity(cameraIntent);
                }
                return true;

              case SWIPE_FORWARD:
              if (manual_key.equals(getString(R.string.filter_batch)) && viewPager.getCurrentItem()==4 && !FLAG5){
                    Toast.makeText(this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                    return true;
                }else if (manual_key.equals(getString(R.string.filter_batch)) && viewPager.getCurrentItem()==7 && !FLAG8){
                    Toast.makeText(this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                    return true;
                }else {
                    return super.onGesture(gesture);
                }

            default:
                return super.onGesture(gesture);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            switch (id) {
                case R.id.bVideoCall2:
                    Intent intent = new Intent(this.getApplicationContext(), VideoPrefsActivity.class);
                    intent.putExtra("step", arrayList.get(viewPager.getCurrentItem()).getStep());
                    intent.putExtra("manual", manual_key);
                    intent.putExtra("manual_name", "General");
                    intent.putExtra("user", "admin@gmail.com");

                    startActivity(intent);
                    break;
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


    }
}
