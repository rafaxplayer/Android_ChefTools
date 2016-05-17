package rafaxplayer.cheftools.Globalclasses;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import rafaxplayer.cheftools.R;


public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutResourceId();

    protected abstract String getCustomTitle();

    private Toolbar toolbar;
    private TextView txttitle;
    private ImageView logo;
    private LinearLayout toolbarContent;
    private static String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        logo = (ImageView) findViewById(R.id.logo);
        toolbarContent = (LinearLayout) findViewById(R.id.contenttoolbar);
        txttitle = (TextView) findViewById(R.id.texttitle);
        txttitle.setTypeface(GlobalUttilities.getfont(this, "Days.ttf"));
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);

            if (getCustomTitle() != null) {

                txttitle.setText(getCustomTitle());
            }

        }
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void hideToolbarContent(Boolean hide) {

        toolbarContent.setVisibility(hide ? View.GONE : View.VISIBLE);

        if (hide) {

            getSupportActionBar().hide();

        } else {

            getSupportActionBar().show();

        }

    }

    public void setTittleDinamic(String title) {
        txttitle.setText(title);

    }
}
