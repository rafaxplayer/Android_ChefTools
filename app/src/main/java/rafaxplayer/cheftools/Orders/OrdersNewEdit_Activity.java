package rafaxplayer.cheftools.Orders;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Orders.fragment.OrdersNewEdit_Fragment;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;
import rafaxplayer.cheftools.R;

public class OrdersNewEdit_Activity extends BaseActivity  implements ProductosMannager_Fragment.OnSelectedCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, OrdersNewEdit_Fragment.newInstance(getIntent().getIntExtra("id", 0)), "neweditorder")
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

        return getString(R.string.activity_newedit_orders);
    }


    @Override
    public void onSelect(int pid) {
        OrdersNewEdit_Fragment fr = (OrdersNewEdit_Fragment)getSupportFragmentManager().findFragmentByTag("neweditorder");
        if(fr!=null){
            fr.displayProductWithId(pid);
            //onBackPressed();
        }
    }
}
