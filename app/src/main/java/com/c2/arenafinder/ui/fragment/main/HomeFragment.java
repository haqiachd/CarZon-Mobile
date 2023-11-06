package com.c2.arenafinder.ui.fragment.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.c2.arenafinder.R;
import com.c2.arenafinder.api.retrofit.RetrofitClient;
import com.c2.arenafinder.data.local.LogApp;
import com.c2.arenafinder.data.local.LogTag;
import com.c2.arenafinder.data.model.AktivitasModel;
import com.c2.arenafinder.data.model.HomeInfoModel;
import com.c2.arenafinder.data.model.JenisLapanganModel;
import com.c2.arenafinder.data.model.ReferensiModel;
import com.c2.arenafinder.data.response.BerandaResponse;
import com.c2.arenafinder.ui.activity.DetailedActivity;
import com.c2.arenafinder.ui.activity.MainActivity;
import com.c2.arenafinder.ui.activity.SubMainActivity;
import com.c2.arenafinder.ui.adapter.AktivitasFirstAdapter;
import com.c2.arenafinder.ui.adapter.HomeInfoAdapter;
import com.c2.arenafinder.ui.adapter.JenisLapanganAdapter;
import com.c2.arenafinder.ui.adapter.VenueFirstAdapter;
import com.c2.arenafinder.ui.adapter.VenueSecondAdapter;
import com.c2.arenafinder.ui.adapter.VenueThirdAdapter;
import com.c2.arenafinder.ui.custom.BottomNavCustom;
import com.c2.arenafinder.util.AdapterActionListener;
import com.c2.arenafinder.util.ArenaFinder;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private int prevScroll = 0;
    private boolean isShown = false;

    private ArrayList<TextView> dots;
    private ArrayList<HomeInfoModel> homeInfoModels;
    private ViewPager2 homeInfoPager;
    private ScrollView scrollView;

    private SwipeRefreshLayout refreshLayout;

    private View btnVallBaru, btnVallRekomendasi, btnVallAktivitas, btnVallLokasi;

    private MaterialCardView menuAlur, menuKomunitas, menuTrolley, menuBooking;

    private LinearLayout homeDots, venueBaruLayout, venueRekomendasiLayout, aktivitasLayout, venueLokasiLayout;

    private RecyclerView jenisLapangan, venueBaruRecycler, buatKamuRecycler, aktivitasRecycler, venueTerdekatRecycler;

    private boolean scrollable = false;

    public void initViews(View view) {

        refreshLayout = view.findViewById(R.id.mho_swipe);
        menuAlur = view.findViewById(R.id.mho_menu_alur);
        menuKomunitas = view.findViewById(R.id.mho_menu_komunitas);
        menuTrolley = view.findViewById(R.id.mho_menu_trolley);
        menuBooking = view.findViewById(R.id.mho_menu_booking);

        btnVallBaru = view.findViewById(R.id.mho_vall_baru);
        btnVallRekomendasi = view.findViewById(R.id.mho_vall_rekomendasi);
        btnVallAktivitas = view.findViewById(R.id.mho_vall_aktivitas);
        btnVallLokasi = view.findViewById(R.id.mho_vall_lokasi);

        venueBaruLayout = view.findViewById(R.id.mho_venue_baru_layout);
        venueRekomendasiLayout = view.findViewById(R.id.mho_venue_rekomendasi_layout);
        aktivitasLayout = view.findViewById(R.id.mho_aktivitas_layout);
        venueLokasiLayout = view.findViewById(R.id.mho_venue_lokasi_layout);

        scrollView = view.findViewById(R.id.mho_scroll);
        jenisLapangan = view.findViewById(R.id.home_recycler_jenis);
        venueBaruRecycler = view.findViewById(R.id.mho_recycler_sedang_kosong);
        buatKamuRecycler = view.findViewById(R.id.mho_recycler_venue_baru);
        aktivitasRecycler = view.findViewById(R.id.mho_recycler_aktivitas_seru);
        venueTerdekatRecycler = view.findViewById(R.id.mho_recycler_venue_terdekat);
        homeInfoPager = view.findViewById(R.id.mho_top_education);
        homeDots = view.findViewById(R.id.mho_dots);
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LogApp.info(requireContext(), "first");
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchData();
                        refreshLayout.setRefreshing(false);
                    }
                }, 1500L);
            }
        });

        MainActivity.bottomNav.setDeactivatedOnFrame(BottomNavCustom.ITEM_HOME);

        fetchData();
        adapterLapangan();
        onClickGroups();

        MainActivity.bottomNav.setOnActionHomeOnFrame(new Runnable() {
            @Override
            public void run() {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, 0); // Mengatur posisi ke atas (0, 0)
                    }
                });
            }
        });

        scrollView.setOnTouchListener((v, event) -> {
            LogApp.info(requireContext(), LogTag.LIFEFCYLE, "ScrollView Listener");
            return !scrollable;
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int currentScroll = scrollView.getScrollY();

                if (currentScroll < 400) {
                    MainActivity.bottomNav.hideSecondIcon(BottomNavCustom.ITEM_HOME);
                } else if (currentScroll < 700) {
//                    MainActivity.bottomNav.showSecondIcon(BottomNavCustom.ITEM_HOME, R.drawable.ic_second_icon_def);
                } else if (currentScroll < 2100) {
//                    MainActivity.bottomNav.showSecondIcon(BottomNavCustom.ITEM_HOME, R.drawable.ic_logo_google);
                } else {
                    MainActivity.bottomNav.hideSecondIcon(BottomNavCustom.ITEM_HOME);
                }

            }
        });

        showPager();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            MainActivity.bottomNav.closeAnimation(BottomNavCustom.ITEM_HOME);
            scrollable = true;
        }, 100);

        pagerAction();

    }

    private void onClickGroups() {

        menuAlur.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Alur", Toast.LENGTH_SHORT).show();
        });

        menuKomunitas.setOnClickListener(v -> {
            startActivity(
                    new Intent(requireContext(), SubMainActivity.class)
                            .putExtra(SubMainActivity.FRAGMENT, SubMainActivity.MENU_COMMUNITY)
            );
        });

        menuTrolley.setOnClickListener(v -> {
            startActivity(
                    new Intent(requireContext(), SubMainActivity.class)
                            .putExtra(SubMainActivity.FRAGMENT, SubMainActivity.MENU_TROLLEY)
            );
        });

        menuBooking.setOnClickListener(v -> {
            startActivity(
                    new Intent(requireContext(), SubMainActivity.class)
                            .putExtra(SubMainActivity.FRAGMENT, SubMainActivity.MENU_BOOKING)
            );
        });

        btnVallBaru.setOnClickListener(v -> {
            startActivity(
                    new Intent(requireActivity(), SubMainActivity.class)
                            .putExtra(SubMainActivity.FRAGMENT, SubMainActivity.VIEW_ALL)
            );
        });

        btnVallRekomendasi.setOnClickListener(v -> {
            startActivity(
                    new Intent(requireActivity(), SubMainActivity.class)
                            .putExtra(SubMainActivity.FRAGMENT, SubMainActivity.VIEW_ALL)
            );
        });

        btnVallAktivitas.setOnClickListener(v -> {
            startActivity(
                    new Intent(requireActivity(), SubMainActivity.class)
                            .putExtra(SubMainActivity.FRAGMENT, SubMainActivity.VIEW_ALL)
            );
        });

        btnVallLokasi.setOnClickListener(v -> {
            startActivity(
                    new Intent(requireActivity(), SubMainActivity.class)
                            .putExtra(SubMainActivity.FRAGMENT, SubMainActivity.VIEW_ALL)
            );
        });

    }

    private void showPager() {
        homeInfoModels = new ArrayList<>();
        homeInfoModels.add(new HomeInfoModel("test/ic_home_viewpager_test.png", ""));
        homeInfoModels.add(new HomeInfoModel("test/venue-1.png", ""));
        homeInfoModels.add(new HomeInfoModel("test/venue-2.png", ""));
        homeInfoModels.add(new HomeInfoModel("test/venue-3.png", ""));
        HomeInfoAdapter infoAdapter = new HomeInfoAdapter(homeInfoModels);

        homeInfoPager.setAdapter(infoAdapter);
        dots = new ArrayList<>();
        addDotsTextView();
    }

    private void addDotsTextView() {
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_bold);
        // add dots sesuai jumlah gambar
        for (int i = 0; i < homeInfoModels.size(); i++){
            dots.add(new TextView(requireActivity()));
            dots.get(i).setText("•");
            dots.get(i).setTextSize(getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp));
            dots.get(i).setTypeface(typeface);
            dots.get(i).setPadding(0, 0, 3, 0);
            homeDots.addView(dots.get(i));
        }
    }

    private void setSelectedColor(int posisi){
        for (int i = 0; i < dots.size(); i++){
            if (i == posisi){
                dots.get(i).setTextColor(ContextCompat.getColor(requireContext(), R.color.whero_purple));
            }else {
                dots.get(i).setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
            }
        }
    }

    private void changePosisi(int posisi){
        homeInfoPager.setCurrentItem(posisi);
        setSelectedColor(posisi);
    }

    private void pagerAction(){

        homeInfoPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                changePosisi(position);
            }
        });

    }

    private void fetchData(){

        RetrofitClient.getInstance().homePage().enqueue(new Callback<BerandaResponse>() {
            @Override
            public void onResponse(Call<BerandaResponse> call, Response<BerandaResponse> response) {

                if (response.body() != null && response.body().getStatus().equalsIgnoreCase(RetrofitClient.SUCCESSFUL_RESPONSE)){
                    LogApp.info(requireContext(), LogTag.RETROFIT_ON_RESPONSE, "ON RESPONSE");
                    BerandaResponse.Data data = response.body().getData();

                    // get data models
                    ArrayList<ReferensiModel> venueBaru = data.getVenueBaru();
                    ArrayList<ReferensiModel> venueRekomendasi = data.getVenueRekomendasi();
                    ArrayList<AktivitasModel> aktivitasBaru = data.getAktivitasSeru();
                    ArrayList<ReferensiModel> venueLokasi = data.getVenueLokasi();

                    if (venueBaru.size() == 0 && venueRekomendasi.size() == 0 && aktivitasBaru.size() == 0 && venueLokasi.size() == 0){
                        Toast.makeText(requireContext(), "SEMUA DATA NULL", Toast.LENGTH_SHORT).show();
                    }else {
                        // show recyclerview
                        showVenueBaru(venueBaru);
                        showVenueRekomendasi(venueRekomendasi);
                        showAktivitasSeru(aktivitasBaru);
                        showVenueLokasi(venueLokasi);
                    }

                }else {
                    Toast.makeText(requireContext(), "FAILURE " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    handlerNullData();
                }

            }

            @Override
            public void onFailure(Call<BerandaResponse> call, Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                handlerNullData();
            }
        });

    }

    private void handlerNullData(){
        venueBaruLayout.setVisibility(View.GONE);
        aktivitasLayout.setVisibility(View.GONE);
        venueRekomendasiLayout.setVisibility(View.GONE);
        aktivitasLayout.setVisibility(View.GONE);
        venueLokasiLayout.setVisibility(View.GONE);
    }

    private void showVenueBaru(ArrayList<ReferensiModel> models){

        if (models.size() <= 0) {
            venueBaruLayout.setVisibility(View.GONE);
        } else {
            LogApp.error(this, LogTag.LIFEFCYLE, "DATA -> " + models.size());
            venueBaruRecycler.setAdapter(new VenueFirstAdapter(
                    requireContext(), models, new AdapterActionListener() {
                @Override
                public void onClickListener(int position) {
                    startActivity(
                            new Intent(requireActivity(), DetailedActivity.class)
                                    .putExtra(DetailedActivity.FRAGMENT, DetailedActivity.VENUE)
                                    .putExtra(DetailedActivity.ID, Integer.toString(models.get(position).getidVenue()))
                    );
                }
            }, VenueFirstAdapter.DEFAULT
            ));

            ArenaFinder.setRecyclerWidthByItem(requireContext(), venueBaruRecycler, models.size(), R.dimen.card_venue_width_java);
        }

    }

    private void showVenueRekomendasi(ArrayList<ReferensiModel> models){

        if (models.size() <= 0) {
            venueRekomendasiLayout.setVisibility(View.GONE);
        } else {
            LogApp.error(this, LogTag.LIFEFCYLE, "DATA -> " + models.size());
            buatKamuRecycler.setAdapter(new VenueSecondAdapter(
                    requireContext(), models, new AdapterActionListener() {
                @Override
                public void onClickListener(int position) {
                    startActivity(
                            new Intent(requireActivity(), DetailedActivity.class)
                                    .putExtra(DetailedActivity.FRAGMENT, DetailedActivity.VENUE)
                                    .putExtra(DetailedActivity.ID, Integer.toString(models.get(position).getidVenue()))
                    );
                }
            }
            ));

            ArenaFinder.setRecyclerWidthByItem(requireContext(), buatKamuRecycler, models.size(), R.dimen.card_venue_second_width_java);
        }

    }

    private void showAktivitasSeru(ArrayList<AktivitasModel> models){

        if (models.size() <= 0){
            aktivitasLayout.setVisibility(View.GONE);
        }else {
            LogApp.error(this, LogTag.LIFEFCYLE, "DATA -> " + models.size());
            aktivitasRecycler.setAdapter(new AktivitasFirstAdapter(
                    requireContext(), models, new AdapterActionListener() {
                @Override
                public void onClickListener(int position) {
                    // TODO : action
                }
            }
            ));

            ArenaFinder.setRecyclerWidthByItem(requireContext(), aktivitasRecycler, models.size(), R.dimen.card_activity_first_width_java);
        }

    }

    private void showVenueLokasi(ArrayList<ReferensiModel> models){

        if (models.size() <= 0) {
            venueLokasiLayout.setVisibility(View.GONE);
        } else {
            LogApp.error(this, LogTag.LIFEFCYLE, "DATA -> " + models.size());
            venueTerdekatRecycler.setAdapter(new VenueThirdAdapter(
                    requireContext(), models, new AdapterActionListener() {
                @Override
                public void onClickListener(int position) {
                    startActivity(
                            new Intent(requireActivity(), DetailedActivity.class)
                                    .putExtra(DetailedActivity.FRAGMENT, DetailedActivity.VENUE)
                                    .putExtra(DetailedActivity.ID, Integer.toString(models.get(position).getidVenue()))
                    );
                }
            }
            ));

            ArenaFinder.setRecyclerWidthByItem(requireContext(), venueTerdekatRecycler, models.size(), R.dimen.card_venue_third_width_java);
        }

    }


    private void adapterLapangan() {
        ArrayList<JenisLapanganModel>lapanganModels = new ArrayList<>();
        lapanganModels.add(new JenisLapanganModel(R.drawable.ic_lapangan_all, "Semua"));
        lapanganModels.add(new JenisLapanganModel(R.drawable.ic_lapangan_sepak_bola, "Sepak Bola"));
        lapanganModels.add(new JenisLapanganModel(R.drawable.ic_lapangan_badminton, "Bulu Tangkis"));
        lapanganModels.add(new JenisLapanganModel(R.drawable.ic_lapangan_voli, "Bola Voli"));
        lapanganModels.add(new JenisLapanganModel(R.drawable.ic_lapangan_basket, "Bola Basket"));

        JenisLapanganAdapter lapanganAdapter = new JenisLapanganAdapter(requireContext(), lapanganModels);
        jenisLapangan.setAdapter(lapanganAdapter);

    }

}