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
import com.example.campustouring.databinding.FragmentSettingsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MarkerInfoFragment extends Fragment {

    private FragmentMarkerInfoBinding binding;
    private Button editButton;
    private Button deleteButton;
    private boolean isEditMode = false;
    private String[] location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMarkerInfoBinding.inflate(inflater, container, false);
        editButton = binding.editButton;
        deleteButton = binding.deleteButton;
        updateButtonState();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    saveChangesToCSV();
                }
                isEditMode = !isEditMode;
                updateButtonState();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLineFromCSV(Integer.parseInt(location[0]));
                getParentFragmentManager().popBackStack();
            }
        });

        return binding.getRoot();
    }

    private void updateButtonState() {
        if (isEditMode) {
            editButton.setText(R.string.saveText);
            editButton.setBackgroundColor(Color.parseColor("#005035"));

            binding.editTitleText.setText(location[1]);
            binding.editTitleText.setVisibility(View.VISIBLE);
            binding.editSubTitleText.setText(location[2]);
            binding.editSubTitleText.setVisibility(View.VISIBLE);
            binding.editDescriptionText.setText(location[3]);
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

    private void saveChangesToCSV() {
        try (InputStreamReader inputStreamReader = new InputStreamReader(getResources().openRawResource(R.raw.master));
             CSVReader reader = new CSVReader(inputStreamReader, '~');
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput("temp.csv", getContext().MODE_PRIVATE));
             CSVWriter writer = new CSVWriter(outputStreamWriter, '~')) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line[0].equals(location[0])) {
                    // Log the current values
                    Log.d("MarkerInfoFragment", "Current values - Title: " + line[1] + ", Subtitle: " + line[2] + ", Description: " + line[3]);

                    // Update values from EditText fields
                    line[1] = binding.editTitleText.getText().toString();
                    line[2] = binding.editSubTitleText.getText().toString();
                    line[3] = binding.editDescriptionText.getText().toString();

                    // Log the updated values
                    Log.d("MarkerInfoFragment", "Updated values - Title: " + line[1] + ", Subtitle: " + line[2] + ", Description: " + line[3]);
                }
                writer.writeNext(line);
            }

            // Delete old file and rename the new file
            getContext().deleteFile("master.csv");
            getContext().getFileStreamPath("temp.csv").renameTo(getContext().getFileStreamPath("master.csv"));

            Toast.makeText(getContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving changes!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteLineFromCSV(int indexToDelete) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(getResources().openRawResource(R.raw.master));
             CSVReader reader = new CSVReader(inputStreamReader, '~');
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput("temp.csv", getContext().MODE_PRIVATE));
             CSVWriter writer = new CSVWriter(outputStreamWriter, '~')) {

            String[] line;
            int currentIndex = 0;

            while ((line = reader.readNext()) != null) {
                if (currentIndex != indexToDelete) {
                    writer.writeNext(line);
                }
                currentIndex++;
            }

            // Delete old file and rename the new file
            getContext().deleteFile("master.csv");
            getContext().getFileStreamPath("temp.csv").renameTo(getContext().getFileStreamPath("master.csv"));

            Toast.makeText(getContext(), "Line deleted successfully!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error deleting line!", Toast.LENGTH_SHORT).show();
        }
    }


    public String[] searchCSV(String locationIndex) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.master)), '~')) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line[0].equals(locationIndex)) {
                    return line;
                }
            }
            return new String[0]; // Return an empty array
        } catch (IOException e) {
            // Handle the exception (e.g., log an error message)
            e.printStackTrace();
            return new String[0]; // Return an empty array
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve marker details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String snippet = args.getString("snippet", "");
            location = searchCSV(snippet);
            binding.titleTextView.setText(location[1]);
            binding.subTitleTextView.setText(location[2]);
            binding.descriptionTextView.setText(location[3]);

            /** Implement once edit and delete have functionality
            if (location.length > 6 && "1".equals(location[6])) {
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            }*/
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}