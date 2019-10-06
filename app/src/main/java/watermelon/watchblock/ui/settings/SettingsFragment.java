package watermelon.watchblock.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import watermelon.watchblock.R;

public class SettingsFragment extends Fragment
{
    SharedPreferences sharedpreferences;
    TextView crimeRadiusLabel;
    TextView timeLabel;
    int progressBar = 10;
    public static final String CRIME_RADIUS = "10";
    public static final String TIME_WINDOW = "30";
    public static final String IS_CHECKED = "notifications";

    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);



        SeekBar seekBar = root.findViewById(R.id.distanceSeekBar);
        crimeRadiusLabel = root.findViewById(R.id.crimeRadiusLabel);
        crimeRadiusLabel.setText("Crime Radius: " + seekBar.getProgress() + " miles");

        SeekBar timeBar = root.findViewById(R.id.timeSeekBar);
        timeLabel = root.findViewById(R.id.timeWindow);
        timeLabel.setText("Crime Time Window: " + timeBar.getProgress() + " minutes");

        final Switch notificationSwitch = root.findViewById(R.id.allowNotifications);


        sharedpreferences = this.getActivity().getSharedPreferences("mainprefs", Context.MODE_PRIVATE);

        if (sharedpreferences.contains(TIME_WINDOW)) {
            timeLabel.setText("Crime Time Window: " + sharedpreferences.getString(TIME_WINDOW, "") + " minutes");
            timeBar.setProgress(Integer.parseInt(sharedpreferences.getString(TIME_WINDOW, "")));
        }


        if (sharedpreferences.contains(CRIME_RADIUS)) {
            crimeRadiusLabel.setText("Crime Radius: " + sharedpreferences.getString(CRIME_RADIUS, "") + " miles");
            seekBar.setProgress(Integer.parseInt(sharedpreferences.getString(CRIME_RADIUS, "")));
        }

        if(sharedpreferences.contains(IS_CHECKED)) {
            notificationSwitch.setChecked(sharedpreferences.getBoolean(IS_CHECKED, false));
        }



        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(IS_CHECKED, isChecked);
                editor.apply();
                //TODO: ungrey out time window
            }
        });


        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeLabel.setText("Crime Time Window: " + seekBar.getProgress() + " minutes");
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(TIME_WINDOW, String.valueOf(seekBar.getProgress()));
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                crimeRadiusLabel.setText("Crime Radius: " + seekBar.getProgress() + " miles");
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(CRIME_RADIUS, String.valueOf(seekBar.getProgress()));
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final Button editProfile = root.findViewById(R.id.editProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }
}