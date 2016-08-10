package rafaxplayer.cheftools.Orders;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Orders.fragment.OrdersDetalle_Fragment;
import rafaxplayer.cheftools.Orders.fragment.OrdersNewEdit_Fragment;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;

public class OrdersDetalle_Activity extends BaseActivity implements ProductosMannager_Fragment.OnSelectedCallback {
    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ID = getIntent().getExtras().getInt("id");

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, OrdersDetalle_Fragment.newInstance(ID), "detalleorder")
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

        return getString(R.string.activity_detalle_orders);
    }

    public void showMenuEdit(int id) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, OrdersNewEdit_Fragment.newInstance(id), "neweditorder");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }


    @Override
    public void onSelect(int pid) {
        OrdersNewEdit_Fragment fr = (OrdersNewEdit_Fragment) getSupportFragmentManager().findFragmentByTag("neweditorder");
        if (fr != null) {
            fr.displayProductWithId(pid);

        }
    }
}
