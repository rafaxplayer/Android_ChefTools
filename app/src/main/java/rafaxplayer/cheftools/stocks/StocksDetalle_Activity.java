package rafaxplayer.cheftools.stocks;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;
import rafaxplayer.cheftools.stocks.fragment.StocksDetalle_Fragment;
import rafaxplayer.cheftools.stocks.fragment.StocksNewEdit_Fragment;

public class StocksDetalle_Activity extends BaseActivity implements ProductosMannager_Fragment.OnSelectedCallback{
    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ID = getIntent().getExtras().getInt("id");

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, StocksDetalle_Fragment.newInstance(ID), "detallestock")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        if (!GlobalUttilities.isScreenLarge(getApplicationContext())) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_template_for_all;
    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_stoks);
    }

    public void showMenuEdit(int id) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, StocksNewEdit_Fragment.newInstance(id), "neweditstock");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();


    }


    @Override
    public void onSelect(int pid) {
        StocksNewEdit_Fragment fr = (StocksNewEdit_Fragment) getSupportFragmentManager().findFragmentByTag("neweditstock");
        if (fr != null) {
            fr.displayProductWithId(pid);

        }
    }
}
