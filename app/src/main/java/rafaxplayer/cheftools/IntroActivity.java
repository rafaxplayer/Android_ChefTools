package rafaxplayer.cheftools;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroActivity extends AppCompatActivity {
    @BindView(R.id.textIntro1)
    TextView textIntro1;
    @BindView(R.id.textIntro2)
    TextView textIntro2;
    @BindView(R.id.textpro)
    TextView textIntro3;
    @BindView(R.id.background)
    RelativeLayout back;
    @BindView(R.id.textVersion)
    TextView version;

    private ObjectAnimator translateX1;
    private ObjectAnimator translateX2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
       //Log.d("intro", String.format("V%s",BuildConfig.VERSION_NAME));
        version.setText(String.format("V%s",BuildConfig.VERSION_NAME));
        translateX1 = ObjectAnimator.ofFloat(textIntro1, "translationX", -100, 0);
        translateX2 = ObjectAnimator.ofFloat(textIntro2, "translationX", 100, 0);

        final ObjectAnimator fadeout = ObjectAnimator.ofFloat(textIntro3, "Alpha", 0.0f, 1.0f);
        fadeout.setDuration(1000).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(IntroActivity.this, Inicio_Activity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Intent intent = new Intent(IntroActivity.this, Inicio_Activity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        fadeout.setInterpolator(new AccelerateInterpolator());
        translateX1.setInterpolator(new LinearInterpolator());
        translateX1.setDuration(1000);
        translateX2.setInterpolator(new LinearInterpolator());

        translateX2.setDuration(1000).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fadeout.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Intent intent = new Intent(IntroActivity.this, Inicio_Activity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        translateX2.start();
        translateX1.start();

    }


}
