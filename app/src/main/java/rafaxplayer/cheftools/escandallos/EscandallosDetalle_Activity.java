package rafaxplayer.cheftools.escandallos;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.escandallos.fragments.EscandalloDetalle_Fragment;
import rafaxplayer.cheftools.escandallos.fragments.EscandalloNewEdit_Fragment;

public class EscandallosDetalle_Activity extends BaseActivity {

    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ID = getIntent().getExtras().getInt("id");

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, EscandalloDetalle_Fragment.newInstance(ID), "detalleescandallo")
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

        return getString(R.string.activity_detalleescandallo);
    }

    public void showMenuEdit(int id) {

        getSupportFragmentManager().beginTransaction()
        .add(R.id.container, EscandalloNewEdit_Fragment.newInstance(id), "neweditescandallo")
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .addToBackStack(null)
        .commit();


    }
}