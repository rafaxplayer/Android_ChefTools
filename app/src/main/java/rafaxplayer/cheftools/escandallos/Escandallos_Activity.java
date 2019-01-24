package rafaxplayer.cheftools.escandallos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.escandallos.fragments.EscandalloDetalle_Fragment;
import rafaxplayer.cheftools.escandallos.fragments.EscandalloList_Fragment;


public class Escandallos_Activity extends BaseActivity implements EscandalloList_Fragment.OnSelectedCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new EscandalloList_Fragment(), "listescandallos");
            ft.commit();
        }

    }

    @Override
    protected int getLayoutResourceId() {
        if (getResources().getBoolean(R.bool.dual_pane)) {
            return R.layout.activity_escandallos;
        } else {
            return R.layout.activity_template_for_all;
        }

    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_escandallos);
    }

    public void showMenuEdit(int num) {
        Intent in = new Intent(getApplicationContext(), EscandallosNewEdit_Activity.class);
        in.putExtra("id", num);
        startActivity(in);
    }


    @Override
    public void onSelect(int id) {

        EscandalloDetalle_Fragment frDetalle = (EscandalloDetalle_Fragment) getSupportFragmentManager().findFragmentById(R.id.detalleescandallo);

        if (frDetalle != null && frDetalle.isInLayout()) {
            frDetalle.displayWithId(id);
        } else {
            Intent i = new Intent(this, EscandallosDetalle_Activity.class);
            i.putExtra("id", id);
            startActivity(i);
        }
    }
}
