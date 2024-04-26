package com.example.campustouring;

import android.content.Context;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.campustouring.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.MapStyleOptions;



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
        loadCSVFiles();
        // load csv files


        //place all the points from
        placePoints(MasterList, googleMap);
        //placePoints(UserList, googleMap);

        //may be used later not sure what for
        //googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {}

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.empty_map_style));
        googleMap.setInfoWindowAdapter(new CustomInfoWindow(getContext()));
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Bundle args = new Bundle();
                args.putString("title", marker.getTitle());
                args.putString("snippet", marker.getSnippet());

                Navigation.findNavController(requireView()).navigate(
                        R.id.action_MapFragment_to_MarkerInfoFragment,
                        args
                );
            }
        });
        this.gMap = googleMap;
    }

        public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

            View CustomView;

            public CustomInfoWindow(Context context) {
                CustomView = LayoutInflater.from(context).inflate(R.layout.custom_marker_view, null);
            }

            @Override
            public View getInfoWindow(Marker marker) {
                TextView customTitle = CustomView.findViewById(R.id.customTitleTextView);
                customTitle.setText(marker.getTitle());
                return CustomView;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        }

    public void loadCSVFiles(){
        try (CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.master)), '~')) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                // 'line' contains the data for one row
                // You can process it as needed
                MasterList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.users)), '~')) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                // 'line' contains the data for one row
                // You can process it as needed
                UserList.add(line);

                for (String data : line) {
                    Log.d("CSVReader", data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void placePoints(List<String[]> MasterList, GoogleMap googleMap ){
        Log.d("placePointsMaster", MasterList.toString());
        String[] headers = MasterList.remove(0);
        for(String[] Location : MasterList) {
            String name = Location[2];
            float color =0;
            Log.d("crash point", name);
            String snippet = Location[0];
            double lat = Double.parseDouble(Location[4]);
            double log = Double.parseDouble(Location[5]);

            LatLng cords = new LatLng(lat,log);
            Log.d("cords",cords.toString());
            googleMap.addMarker(new MarkerOptions()
                    .position(cords)
                    .snippet(snippet)
                    .title(name));
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}