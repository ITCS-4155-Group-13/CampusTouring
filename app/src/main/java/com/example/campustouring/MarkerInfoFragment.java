package com.example.campustouring;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.campustouring.databinding.FragmentMarkerInfoBinding;
import java.util.HashMap;


public class MarkerInfoFragment extends Fragment {

    private FragmentMarkerInfoBinding binding;
    private Button editButton;
    private Button deleteButton;
    private boolean isEditMode = false;
    private String locationIndex;
    private CustomMarkerContract markerContract;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMarkerInfoBinding.inflate(inflater, container, false);
        editButton = binding.editButton;
        deleteButton = binding.deleteButton;
        updateButtonState();

        markerContract = new CustomMarkerContract(getContext());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    updateMarkerInfoInDatabase();
                }
                isEditMode = !isEditMode;
                updateButtonState();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMarkerFromDatabase(locationIndex);
                getParentFragmentManager().popBackStack();
            }
        });

        return binding.getRoot();
    }

    private void updateButtonState() {
        if (isEditMode) {
            editButton.setText(R.string.saveText);
            editButton.setBackgroundColor(Color.parseColor("#005035"));

            binding.editTitleText.setText(binding.titleTextView.getText().toString());
            binding.editTitleText.setVisibility(View.VISIBLE);
            binding.editSubTitleText.setText(binding.subTitleTextView.getText().toString());
            binding.editSubTitleText.setVisibility(View.VISIBLE);
            binding.editDescriptionText.setText(binding.descriptionTextView.getText().toString());
            binding.editDescriptionText.setVisibility(View.VISIBLE);
            binding.scrollEditDescriptionText.setVisibility(View.VISIBLE);

            binding.titleTextView.setVisibility(View.INVISIBLE);
            binding.subTitleTextView.setVisibility(View.INVISIBLE);
            binding.descriptionTextView.setVisibility(View.INVISIBLE);
            binding.scrollDescriptionTextView.setVisibility(View.INVISIBLE);
        } else {
            editButton.setText(R.string.editText);
            editButton.setBackgroundColor(Color.parseColor("#A49665"));

            binding.editTitleText.setVisibility(View.INVISIBLE);
            binding.editSubTitleText.setVisibility(View.INVISIBLE);
            binding.editDescriptionText.setVisibility(View.INVISIBLE);
            binding.scrollEditDescriptionText.setVisibility(View.VISIBLE);

            binding.titleTextView.setVisibility(View.VISIBLE);
            binding.subTitleTextView.setVisibility(View.VISIBLE);
            binding.descriptionTextView.setVisibility(View.VISIBLE);
            binding.scrollDescriptionTextView.setVisibility(View.VISIBLE);
        }
    }

    private void updateMarkerInfoInDatabase() {
        String title = binding.editTitleText.getText().toString();
        String subtitle = binding.editSubTitleText.getText().toString();
        String description = binding.editDescriptionText.getText().toString();

        // Retrieve existing marker information
        HashMap<String, Object> existingMarker = markerContract.readSingleFromDb(locationIndex);

        // Ensure existing latitude and longitude are preserved
        String latitude = existingMarker.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT).toString();
        String longitude = existingMarker.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG).toString();
        // Ensure existing default marker status is preserved
        String isDefaultMarker = existingMarker.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER).toString();

        // Create updated marker object
        CustomMarkerContract.MarkerEntryObj updatedMarker = new CustomMarkerContract.MarkerEntryObj(locationIndex, title, subtitle, description, latitude, longitude, isDefaultMarker);

        // Update marker information in the database
        markerContract.saveToDb(updatedMarker);

        // Update UI with the new information
        binding.titleTextView.setText(title);
        binding.subTitleTextView.setText(subtitle);
        binding.descriptionTextView.setText(description);

        Toast.makeText(getContext(), "Marker info updated successfully!", Toast.LENGTH_SHORT).show();
    }

    private void deleteMarkerFromDatabase(String localIndex) {
        markerContract.deleteOneFromDb(localIndex);
        Toast.makeText(getContext(), "Marker deleted successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve marker details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String snippet = args.getString("snippet", "");
            locationIndex = snippet;
            HashMap<String, Object> marker = markerContract.readSingleFromDb(snippet);

            if (!marker.isEmpty()) {
                // Print the contents of the marker HashMap
                Log.d("Marker HashMap", "Marker HashMap: " + marker);

                binding.titleTextView.setText(marker.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME).toString());
                binding.subTitleTextView.setText(marker.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME).toString());
                binding.descriptionTextView.setText(marker.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK).toString());

                String isDefaultMarker = marker.get(CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER).toString();
                if ("1".equals(isDefaultMarker)) {
                    editButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                }
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}