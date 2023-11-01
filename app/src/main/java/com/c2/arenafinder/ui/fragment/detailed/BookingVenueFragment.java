package com.c2.arenafinder.ui.fragment.detailed;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.c2.arenafinder.R;
import com.c2.arenafinder.data.model.DatePickerModel;
import com.c2.arenafinder.data.model.JadwalPickerModel;
import com.c2.arenafinder.data.model.VenueBookingModel;
import com.c2.arenafinder.ui.adapter.DatePickerAdapter;
import com.c2.arenafinder.ui.adapter.JadwalPickerAdapter;
import com.c2.arenafinder.ui.adapter.VenueBookingAdapter;
import com.c2.arenafinder.util.AdapterActionListener;
import com.c2.arenafinder.util.ArenaFinder;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BookingVenueFragment extends Fragment {

    private static final String ARG_ID = "id";

    private String id;

    private VenueBookingAdapter venueBookingAdapter;

    private RecyclerView recyclerDate, recyclerVenue;

    public BookingVenueFragment() {
        // Required empty public constructor
    }

    private void initViews(View view) {
        recyclerDate = view.findViewById(R.id.fbv_recycler_date);
        recyclerVenue = view.findViewById(R.id.fbv_recycler_lapangan);
    }


    public static BookingVenueFragment newInstance(String id) {
        BookingVenueFragment fragment = new BookingVenueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_venue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        ArenaFinder.setStatusBarColor(requireActivity(), ArenaFinder.WHITE_STATUS_BAR, R.color.white, true);

        showDatePicker();
        showBookingVenue();
        updateBottomNav(0);
    }

    private void showDatePicker() {

        ArrayList<DatePickerModel> models = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfDayOfWeek = new SimpleDateFormat("E", Locale.getDefault());
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM", Locale.getDefault());

        // Mendapatkan tanggal saat ini
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            // Format tanggal menjadi "yyyy-MM-dd"
            String currentDate = sdf.format(calendar.getTime());

            // Mengambil nama hari dan format "dd MMM" dari tanggal saat ini
            String dayOfWeek = sdfDayOfWeek.format(calendar.getTime());
            String formattedDate = sdfDate.format(calendar.getTime());

            // Membuat objek DatePickerModel dan menambahkannya ke dalam list
            models.add(new DatePickerModel(formattedDate, dayOfWeek, currentDate, false));

            // Menambah satu hari ke tanggal saat ini
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        DatePickerAdapter pickerAdapter = new DatePickerAdapter(requireContext(), models, new AdapterActionListener() {
            @Override
            public void onClickListener(int position) {
                Toast.makeText(requireContext(), models.get(position).getDateMonth(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerDate.setAdapter(pickerAdapter);

    }

    private void showBookingVenue(){

        ArrayList<JadwalPickerModel> pickerModels = new ArrayList<>();
        pickerModels.add(new JadwalPickerModel(1, "07:00 - 08:00", 75000, false,false));
        pickerModels.add(new JadwalPickerModel(1, "08:00 - 09:00", 75000, false,false));
        pickerModels.add(new JadwalPickerModel(1, "09:00 - 10:00", 75000, false,false));
        pickerModels.add(new JadwalPickerModel(1, "10:00 - 11:00", 75000, false,false));
        pickerModels.add(new JadwalPickerModel(1, "11:00 - 12:00", 75000, false,false));
        pickerModels.add(new JadwalPickerModel(1, "12:00 - 13:00", 75000, false,false));

        ArrayList<JadwalPickerModel> pickerModels1 = new ArrayList<>();
        pickerModels1.add(new JadwalPickerModel(1, "10:00 - 11:00", 75000, false,false));
        pickerModels1.add(new JadwalPickerModel(1, "11:00 - 12:00", 75000, false,false));
        pickerModels1.add(new JadwalPickerModel(1, "12:00 - 13:00", 75000, false,false));


        ArrayList<VenueBookingModel> bookingModels = new ArrayList<>();

        bookingModels.add(new VenueBookingModel(
                1, "", "Lapangan 1", "11 Slot Kosong", pickerModels

        ));
//        bookingModels.add(new VenueBookingModel(
//                2, "", "Lapangan 2", "12 Slot Kosong", pickerModels1
//
//        ));

        this.venueBookingAdapter = new VenueBookingAdapter(requireContext(), bookingModels, new AdapterActionListener() {
            @Override
            public void onClickListener(int position) {
                updateBottomNav(venueBookingAdapter.getSelectedItem());
            }
        });

        recyclerVenue.setAdapter(venueBookingAdapter);

    }

    private void updateBottomNav(int item){
        if (getActivity() != null){
            TextView txtTop, txtData, txtRight;
            MaterialButton button;
            txtTop = getActivity().findViewById(R.id.dtld_nav_txt_top);
            txtRight = getActivity().findViewById(R.id.dtld_nav_txt_right);
            txtData = getActivity().findViewById(R.id.dtld_nav_txt_data);
            button = getActivity().findViewById(R.id.dtld_nav_button);

            DecimalFormat decimalFormat = new DecimalFormat("#,###");

            txtTop.setText("Total Harga");
            txtRight.setText(" dari " + item + " Jadwal");
            txtData.setText("Rp. " + (decimalFormat.format(75_000 * item)));
            button.setText("PESAN");
        }
    }

}