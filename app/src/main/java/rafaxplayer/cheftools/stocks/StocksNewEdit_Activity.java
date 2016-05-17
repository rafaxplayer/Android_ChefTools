package rafaxplayer.cheftools.stocks;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;
import rafaxplayer.cheftools.stocks.fragment.StocksNewEdit_Fragment;

public class StocksNewEdit_Activity extends BaseActivity  implements ProductosMannager_Fragment.OnSelectedCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, StocksNewEdit_Fragment.newInstance(getIntent().getIntExtra("id", 0)), "neweditstock")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        if(!GlobalUttilities.isScreenLarge(getApplicationContext())) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_template_for_all;
    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_newedit_stocks);
    }


    @Override
    public void onSelect(int pid) {
        StocksNewEdit_Fragment fr = (StocksNewEdit_Fragment)getSupportFragmentManager().findFragmentByTag("neweditstock");
        if(fr!=null){
            fr.displayProductWithId(pid);

        }
    }
}
