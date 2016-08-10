package rafaxplayer.cheftools.Globalclasses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rafaxplayer.cheftools.R;


public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutResourceId();

    protected abstract String getCustomTitle();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.contenttoolbar)
    LinearLayout toolbarContent;
    @BindView(R.id.texttitle)
    TextView txttitle;

    @OnClick(R.id.logo)
    public void back() {
        onBackPressed();
    }

    private static String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
        txttitle.setTypeface(GlobalUttilities.getfont(this, "Days.ttf"));
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);

            if (getCustomTitle() != null) {

                txttitle.setText(getCustomTitle());
            }

        }

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
