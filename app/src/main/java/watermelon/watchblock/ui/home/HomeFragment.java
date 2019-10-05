package watermelon.watchblock.ui.home;

import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Locale;

import watermelon.watchblock.R;
import watermelon.watchblock.Submission;

public class HomeFragment extends Fragment
{

    private HomeViewModel homeViewModel;

    TextToSpeech tts;

    private void speak(){
        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                String text = "Juniper hates Kelsie";
                tts.setLanguage(Locale.US);
                if (status == TextToSpeech.SUCCESS){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        String utteranceId = this.hashCode() + "";
                        Bundle params = new Bundle();
                        params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_VOICE_CALL);
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId);
                        System.out.println("I have spoken");
                    }
                }

            }
        });
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        //Start button that initiates the game.
        Button buttonSolo = root.findViewById(R.id.submitTipButton);





        buttonSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Submission BDSM = new Submission();
                speak();

            }
        });


        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                textView.setText(s);
            }
        });
        return root;
    }
}