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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap gMap;

    private List<String[]> MasterList = new ArrayList<>();
    private List<String[]> UserList = new ArrayList<>();


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

        // load
        loadCSVFiles(googleMap);

        placePoints(MasterList, googleMap);
        placePoints(UserList, googleMap);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                buildNewUserPoint(latLng);

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }

        });
        this.gMap = googleMap;


    }
    public void loadCSVFiles(@NonNull GoogleMap googleMap){
        //List<String[]> MasterList = new ArrayList<>();
        //List<String[]> UserList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.master)))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                // 'line' contains the data for one row
                // You can process it as needed
                MasterList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.users)))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                // 'line' contains the data for one row
                // You can process it as needed
                UserList.add(line);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Master",MasterList.toString());

        //placeMasterPoints(MasterList, googleMap);
        //placeMasterPoints(UserList, googleMap);
    }
    public void placePoints(List<String[]> MasterList, GoogleMap googleMap ){

        for(String[] Location : MasterList) {
            String name = Location[2];

            for (String data : Location) {
                Log.d("Location", data);
            }
            float color =0;
            switch (Location[6]) {
                case "1":
                    color = 120; break;
                case "2":
                    color= 210; break;
                case "3":
                    color= 60; break;
                case "4":
                    color = 180; break;
                case "5":
                    color = 30; break;
                default:
                    color =0;
            }

            double lat = Double.parseDouble(Location[4]);
            double log = Double.parseDouble(Location[5]);

            LatLng cords = new LatLng(lat,log);
            Log.d("cords",cords.toString());
            googleMap.addMarker(new MarkerOptions()
                    .position(cords)
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
        }
    }

    public void buildNewUserPoint( LatLng latlng){

        //do some code to get the user data
        Log.d("usersaved","adduser point fnc reached");
        //key value
        //pull last val from USerList and get its key val. make new key val +1
        int keyIndex =0;
        if(UserList.size()==0){
            keyIndex =1;
        }
        else {
            int lastIndex = UserList.size() - 1;
            keyIndex = Integer.parseInt(UserList.get(lastIndex)[0]) + 1;
        }

        //location name
        String Name = "Test point";
        String ShortName =Name;
        //description
        String Descrition ="Basic test point from near sanford hall";
        //lat
        double lat =latlng.latitude;
        //lng
        double lng = latlng.longitude;
        //(possibley) location type
        String L_type ="0";

        // add to
        //C:\Users\joesu\AndroidStudioProjects\CampusTouring\app\src\main\res\raw\UserPoints.csv
        String[] data1 = {String.valueOf(keyIndex), Name,ShortName, Descrition, String.valueOf(lat), String.valueOf(lng),L_type};
        Log.d("User Points", data1.toString());
        String filePath = "CampusTouring\\app\\src\\main\\res\\raw\\UserPoints.csv";
        try {
            // Create FileWriter object with file path
            FileWriter fileWriter = new FileWriter(filePath);

            // Create CSVWriter object with FileWriter
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Write data to CSV file
            csvWriter.writeNext(data1);
            csvWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}