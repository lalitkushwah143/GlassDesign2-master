package com.example.android.glass.glassdesign2;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataAbbreviation;
import com.example.android.glass.glassdesign2.data.DataAttach;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class AbbreviationActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tvPage;
    private TextView tvData, tvTitle, tvTime;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference, updateRef;
    private ArrayList<DataAbbreviation> arrayList = new ArrayList<>();
    private AbbreAdapter adapter;
    private int currentMenuItemIndex;
    private TabLayout tabLayout;

    private String title, key, update_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abbreviation);

        tvData = findViewById(R.id.activity_abbre_tvData);
        tvTitle = findViewById(R.id.activity_abbre_tvTitle);
        tvTime = findViewById(R.id.activity_abbre_tvTime);
        recyclerView = findViewById(R.id.activity_abbre_rcView);
        tvPage = findViewById(R.id.activity_abbre_tvPage);

        firestore = FirebaseFirestore.getInstance();

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        key = getIntent().getStringExtra("key");

        if (TextUtils.isEmpty(key)){
            Toast.makeText(this, "No key", Toast.LENGTH_SHORT).show();
            Log.e("Approval", "No key");
            finish();
        }else {
            //  indicator.setupWithViewPager(viewPager, true);
            // viewPager.setAdapter(screenSliderPagerAdapter);

            adapter = new AbbreAdapter(this, arrayList);
            SnapHelper snapHelper = new PagerSnapHelper();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            snapHelper.attachToRecyclerView(recyclerView);
         //   final int color = ContextCompat.getColor(AbbreviationActivity.this, R.color.design_yellow2);
         //   final int color2 = ContextCompat.getColor(AbbreviationActivity.this, R.color.color_white);
        //    recyclerView.addItemDecoration(new DotsIndicatorDecoration(10, 4 * 4, 10, color2, color));

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    final View foundView = snapHelper.findSnapView(layoutManager);
                    if (foundView == null) {
                        tvPage.setText(" ");
                        return;
                    }
                    currentMenuItemIndex = layoutManager.getPosition(foundView);
                    tvPage.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                }
            });


            firestore.collection("DQNewReport").document(key).collection("content").document("abbreviations").collection("details")
                    .orderBy("index")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("shorts") != null  && snapshot.getData().get("full") != null && snapshot.getData().get("index") != null) {

                                    arrayList.add(new DataAbbreviation(snapshot.getId(), snapshot.getData().get("shorts").toString(), snapshot.getData().get("full").toString(), snapshot.get("index", Integer.class)));
                                    adapter.notifyDataSetChanged();
                                    Log.e("sample", snapshot.getData().toString());
                                }else {
                                    Log.e("Safety", "Missing Parameters");
                                }
                            }
                            if (arrayList.size() == 0){
                                //  viewPager.setVisibility(View.GONE);
                                // indicator.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                tvData.setVisibility(View.VISIBLE);
                                tvPage.setVisibility(View.GONE);
                            }else {
                                //  viewPager.setVisibility(View.VISIBLE);
                                // indicator.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                tvData.setVisibility(View.GONE);
                                tvPage.setVisibility(View.VISIBLE);
                                tvPage.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                            }
                        }
                    });
            //  screenSliderPagerAdapter.notifyDataSetChanged();


        }
    }

    public class AbbreAdapter extends RecyclerView.Adapter<AbbreAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataAbbreviation> list=new ArrayList<>();

        public AbbreAdapter(Context context, ArrayList<DataAbbreviation> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public AbbreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_abbre, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull AbbreAdapter.ViewHolder holder, final int position) {

            holder.tvRev.setText(list.get(position).getFull());
            holder.tvDesc.setText(list.get(position).getShorts());

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDesc, tvRev;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRev = itemView.findViewById(R.id.list_abbre_tvRevision);
                tvDesc = itemView.findViewById(R.id.list_abbre_tvAbbre);
            }
        }
    }

    public class DotsIndicatorDecoration extends RecyclerView.ItemDecoration {

        private final int indicatorHeight;
        private final int indicatorItemPadding;
        private final int radius;

        private final Paint inactivePaint = new Paint();
        private final Paint activePaint = new Paint();

        public DotsIndicatorDecoration(int radius, int padding, int indicatorHeight, @ColorInt int colorInactive, @ColorInt int colorActive) {
            float strokeWidth = Resources.getSystem().getDisplayMetrics().density * 1;
            this.radius = radius;
            inactivePaint.setStrokeCap(Paint.Cap.ROUND);
            inactivePaint.setStrokeWidth(strokeWidth);
            inactivePaint.setStyle(Paint.Style.FILL);
            inactivePaint.setAntiAlias(true);
            inactivePaint.setColor(colorInactive);

            activePaint.setStrokeCap(Paint.Cap.ROUND);
            activePaint.setStrokeWidth(strokeWidth);
            activePaint.setStyle(Paint.Style.FILL);
            activePaint.setAntiAlias(true);
            activePaint.setColor(colorActive);

            this.indicatorItemPadding = padding;
            this.indicatorHeight = indicatorHeight;
        }

        @Override
        public void onDrawOver(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
            super.onDrawOver(c, parent, state);

            final RecyclerView.Adapter adapter = parent.getAdapter();

            if (adapter == null) {
                return;
            }

            int itemCount = adapter.getItemCount();

            // center horizontally, calculate width and subtract half from center
            float totalLength = this.radius * 2 * itemCount;
            float paddingBetweenItems = Math.max(0, itemCount - 1) * indicatorItemPadding;
            float indicatorTotalWidth = totalLength + paddingBetweenItems;
            float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2f;

            // center vertically in the allotted space
            float indicatorPosY = parent.getHeight() - indicatorHeight / 2f;

            drawInactiveDots(c, indicatorStartX, indicatorPosY, itemCount);

            final int activePosition;

            if (parent.getLayoutManager() instanceof GridLayoutManager) {
                activePosition = ((GridLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
            } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
                activePosition = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
            } else {
                // not supported layout manager
                return;
            }

            if (activePosition == RecyclerView.NO_POSITION) {
                return;
            }

            // find offset of active page if the user is scrolling
            final View activeChild = parent.getLayoutManager().findViewByPosition(activePosition);
            if (activeChild == null) {
                return;
            }

            drawActiveDot(c, indicatorStartX, indicatorPosY, activePosition);
        }

        private void drawInactiveDots(Canvas c, float indicatorStartX, float indicatorPosY, int itemCount) {
            // width of item indicator including padding
            final float itemWidth = this.radius * 2 + indicatorItemPadding;

            float start = indicatorStartX + radius;
            for (int i = 0; i < itemCount; i++) {
                c.drawCircle(start, indicatorPosY, radius, inactivePaint);
                start += itemWidth;
            }
        }

        private void drawActiveDot(Canvas c, float indicatorStartX, float indicatorPosY,
                                   int highlightPosition) {
            // width of item indicator including padding
            final float itemWidth = this.radius * 2 + indicatorItemPadding;
            float highlightStart = indicatorStartX + radius + itemWidth * highlightPosition;
            c.drawCircle(highlightStart, indicatorPosY, radius, activePaint);
        }

        @Override
        public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = indicatorHeight;
        }
    }
}