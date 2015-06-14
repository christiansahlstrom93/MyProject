package project.alpha.projecta;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class Settings extends Activity {
    private String roman;

    private MediaPlayer media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        fillRoman();
        media = MediaPlayer.create(this, R.raw.music);
        media.start();
        TextView tv = (TextView) findViewById(R.id.textView7);
        Animation translatebu = AnimationUtils.loadAnimation(this, R.anim.animation);
        tv.setText(roman);
        tv.startAnimation(translatebu);

    }

    void fillRoman() {
        roman = "\nCashnet är framtaget för att jämföra bankers sparkonto och hjälpa privatpersoner och företag att hitta den bästa sparformen.\n\n" +
                "I dagens läge med väldigt låg ränta på dem flesta håll så kan det bli svårt att få ut det bästa av sitt sparande." +
                " Cashnet är utrustad med algoritmer som tar fram och jämför räntor hos storbanker och även dem mindre aktörerna på marknaden just för att DU ska kunna få så bra ränta som möjligt.\n\n" +
                "" +
                "Välj i listan bland alla våra banker och jämför deras konton sinsemellan för att hitta just det kontot som passar dig. Fyll i hur mycket och hur länge du vill spara och få fram exakt hur mycket tillväxt du får av just det valda kontot." +
                "" +
                "\n\n";
    }

    @Override
    public void onDestroy() {

        media.stop();
        super.onDestroy();

    }
}
