package rafaxplayer.cheftools.escandallos;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.escandallos.fragments.EscandalloNewEdit_Fragment;

public class EscandallosNewEdit_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, EscandalloNewEdit_Fragment.newInstance(getIntent().getIntExtra("id", 0)), "neweditescandallo")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

        }
    }


    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_template_for_all;
    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_neweditescandallo);
    }
}