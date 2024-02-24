package com.example.campustouring;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.campustouring.databinding.FragmentSecondBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SecondFragment extends Fragment implements OnMapReadyCallback {

    private FragmentSecondBinding binding;
    private GoogleMap gMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.id_map, mapFragment).commit();
        mapFragment.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(35.3071, -80.7352);
        googleMap.addMarker(new MarkerOptions().position(location).title("UNC Charlotte"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        this.gMap = googleMap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}