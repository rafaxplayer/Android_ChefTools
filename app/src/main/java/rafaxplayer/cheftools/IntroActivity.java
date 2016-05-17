package rafaxplayer.cheftools;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;


public class IntroActivity extends AppCompatActivity {
    private TextView textIntro1;
    private TextView textIntro2;
    private TextView textIntro3;
    private final int DURACION_SPLASH = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        textIntro1=(TextView)findViewById(R.id.textIntro1);
        textIntro2=(TextView)findViewById(R.id.textIntro2);
        textIntro3=(TextView)findViewById(R.id.textpro);

        ObjectAnimator translateX1 = ObjectAnimator.ofFloat(textIntro1, "translationX",-100,0);
        ObjectAnimator translateX2 = ObjectAnimator.ofFloat(textIntro2, "translationX",100,0);
        final ObjectAnimator fadeout = ObjectAnimator.ofFloat(textIntro3, "Alpha",0.0f,1.0f);
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
        translateX2.start();
        translateX1.start();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
