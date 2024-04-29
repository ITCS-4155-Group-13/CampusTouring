package com.example.campustouring;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import com.example.campustouring.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.Manifest;

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
        LatLng schoolLocation = new LatLng(35.3071, -80.7352);
        LatLng southWest = new LatLng(35.3040, -80.7400);
        LatLng northEast = new LatLng(35.3100, -80.7300);
        LatLngBounds bounds = new LatLngBounds(southWest, northEast);
        googleMap.setLatLngBoundsForCameraTarget(bounds);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(schoolLocation, 16));
        buildMasterPointList(googleMap);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.empty_map_style));
        googleMap.setInfoWindowAdapter(new CustomInfoWindow(getContext()));

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true); // Show the user's location on the map
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // Add a marker for the user's location
                    googleMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .title("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    // Move the camera to the user's location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
                }
            });
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Bundle args = new Bundle();
                args.putParcelable("locationCoordinates", latLng);
                Navigation.findNavController(requireView()).navigate(
                        R.id.action_MapFragment_to_LocationInputFragment,
                        args
                );
            }
        });
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("locationDetails", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Retrieve location details from the result bundle
                String name = result.getString("name");
                String shortName = result.getString("shortName");
                String description = result.getString("description");
                double latitude = result.getDouble("latitude", 0.0);
                double longitude = result.getDouble("longitude", 0.0);

                // Handle the received location details (e.g., display marker on the map)
                if (name != null && shortName != null && description != null) {
                    // Add a marker on the map with the received location details
                    LatLng location = new LatLng(latitude, longitude);
                    gMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(name)
                            .snippet(shortName + ": " + description));
                }
            }
        });
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


    public void buildMasterPointList(@NonNull GoogleMap googleMap){
        List<String[]> MasterList = new ArrayList<>();

        try (CSVReader reader = new CSVReader (new InputStreamReader(getResources().openRawResource(R.raw.master)), '~')) {
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
        placeMasterPoints(MasterList, googleMap);
    }
    public void placeMasterPoints(List<String[]> MasterList, GoogleMap googleMap ){
        for(String[] Location : MasterList) {
            String name = Location[1];
            String snippet = Location[0];
            double lat = Double.parseDouble(Location[4]);
            double log = Double.parseDouble(Location[5]);
            LatLng coords = new LatLng(lat,log);

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