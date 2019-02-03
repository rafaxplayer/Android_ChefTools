package rafaxplayer.cheftools.providers;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.providers.fragment.ProviderDetalle_Fragment;
import rafaxplayer.cheftools.providers.fragment.ProviderNewEdit_Fragment;

public class ProviderDetalle_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int ID = getIntent().getExtras().getInt("id");
        Log.e("ID", String.valueOf(ID));
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ProviderDetalle_Fragment.newInstance(ID), "detalleprov")
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

        return getString(R.string.activity_detallesproveedor);
    }

    public void showMenuEdit(int id) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, ProviderNewEdit_Fragment.newInstance(id), "neweditprov");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();


    }


}
