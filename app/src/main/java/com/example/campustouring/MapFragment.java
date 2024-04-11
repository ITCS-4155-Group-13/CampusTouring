package com.example.campustouring;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.campustouring.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap gMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.id_map, mapFragment).commit();
        mapFragment.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(35.3071, -80.7352);
        LatLng southWest = new LatLng(35.3040, -80.7400);
        LatLng northEast = new LatLng(35.3100, -80.7300);
        LatLngBounds bounds = new LatLngBounds(southWest, northEast);
        googleMap.setLatLngBoundsForCameraTarget(bounds);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        googleMap.clear();
        buildMasterPointList(googleMap);
        this.gMap = googleMap;
    }
    public void buildMasterPointList(@NonNull GoogleMap googleMap){

        String csvFile = "master.csv";
        List<String[]> MasterList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.master)))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                // 'line' contains the data for one row
                // You can process it as needed
                MasterList.add(line);
                for (String data : line) {
                    Log.d("CSVReader", data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Master",MasterList.toString());

        placeMasterPoints(MasterList, googleMap);
    }
    public void placeMasterPoints(List<String[]> MasterList, GoogleMap googleMap ){
       LatLng location = new LatLng(35.30293891,-80.73356588);

        for(String[] Location : MasterList) {
            String name = Location[2];
            for (String data : Location) {
                Log.d("Location", data);
            }

            double lat = Double.parseDouble(Location[4]);
            double log = Double.parseDouble(Location[5]);
            LatLng coords = new LatLng(lat,log);
            Log.d("cords",coords.toString());
            googleMap.addMarker(new MarkerOptions()
                    .position(coords)
                    .title(name));
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}