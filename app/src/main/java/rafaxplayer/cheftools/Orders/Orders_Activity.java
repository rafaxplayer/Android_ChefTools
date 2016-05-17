package rafaxplayer.cheftools.Orders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Orders.fragment.OrdersDetalle_Fragment;
import rafaxplayer.cheftools.Orders.fragment.OrdersList_Fragment;
import rafaxplayer.cheftools.R;

public class Orders_Activity extends BaseActivity implements OrdersList_Fragment.OnSelectedCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new OrdersList_Fragment(), "listorders");
            ft.commit();
        }
    }
    @Override
    protected int getLayoutResourceId() {
        if(getResources().getBoolean(R.bool.dual_pane)){
            return R.layout.activity_orders;
        }else{
            return R.layout.activity_template_for_all;
        }

    }
    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_orders);
    }

    public void showMenuEdit(int num) {
        Intent in = new Intent(getApplicationContext(), OrdersNewEdit_Activity.class);
        in.putExtra("id", num);
        startActivity(in);
    }

    @Override
    public void onSelect(int id) {

        OrdersDetalle_Fragment frDetalle = (OrdersDetalle_Fragment) getSupportFragmentManager().findFragmentById(R.id.detalleorders);

        if (frDetalle != null && frDetalle.isInLayout()) {
            frDetalle.displayWithId(id);

        } else {

            Intent i = new Intent(this, OrdersDetalle_Activity.class);
            i.putExtra("id", id);
            startActivity(i);
        }
    }


}
