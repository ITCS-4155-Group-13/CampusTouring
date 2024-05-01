package com.example.campustouring;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.MapStyleOptions;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private FragmentMapBinding binding;
    private GoogleMap gMap;
    CustomMarkerContract contract;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    gMap.setMyLocationEnabled(true);
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });

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
        contract = new CustomMarkerContract(this.getContext());

        // Load CSV files
        loadCSVFiles();

        // Place points from Database
        placePoints(googleMap);

        printMarkersFromContract();

        //Implement the presentation of the LocationInputFragment on Map Click
        //googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {}

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

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
        // Loading All Default Points From CSV
        boolean firstLine = true;
        try (CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.master)), '~')) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                // Convert CSV data to MarkerEntryObj
                CustomMarkerContract.MarkerEntryObj marker = new CustomMarkerContract.MarkerEntryObj(
                        line[0], line[1], line[2], line[3], line[4], line[5]
                );
                // Save marker to the database using the contract
                contract.saveToDb(marker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Loading All Custom User Points From CSV
        try (CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.users)), '~')) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                // Convert CSV data to MarkerEntryObj
                CustomMarkerContract.MarkerEntryObj marker = new CustomMarkerContract.MarkerEntryObj(
                        line[0], line[1], line[2], line[3], line[4], line[5]
                );
                // Save marker to the database using the contract
                contract.saveToDb(marker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Logging Function to Check Contract Integrity, Delete After Use
    private void printMarkersFromContract() {
        List<HashMap<String, Object>> markerList = contract.readAllFromDb();
        for (HashMap<String, Object> marker : markerList) {
            Log.d("Marker from Contract", "Name: " + marker.get("name") +
                    ", Latitude: " + marker.get("lat") +
                    ", Longitude: " + marker.get("long"));
        }
    }

    public void placePoints(GoogleMap googleMap) {
        List<HashMap<String, Object>> customList = contract.readAllFromDb();

        for (HashMap<String, Object> row : customList) {
            String name = (String) row.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME);
            String snippet = String.valueOf(row.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX));
            double lat = (Double) row.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT);
            double lng = (Double) row.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG);

            LatLng coords = new LatLng(lat, lng);

            googleMap.addMarker(new MarkerOptions()
                    .position(coords)
                    .title(name)
                    .snippet(snippet));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}