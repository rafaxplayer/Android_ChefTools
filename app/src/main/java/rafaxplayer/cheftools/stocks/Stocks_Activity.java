package rafaxplayer.cheftools.stocks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.stocks.fragment.StocksDetalle_Fragment;
import rafaxplayer.cheftools.stocks.fragment.StocksList_Fragment;

public class Stocks_Activity extends BaseActivity implements StocksList_Fragment.OnSelectedCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new StocksList_Fragment(), "stokslist");
            ft.commit();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        if(getResources().getBoolean(R.bool.dual_pane)){
            return R.layout.activity_stocks;
        }else{
            return R.layout.activity_template_for_all;
        }
    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_stoks);
    }
    public void showMenuEdit(int num) {
        Intent in = new Intent(getApplicationContext(), StocksNewEdit_Activity.class);
        in.putExtra("id", num);
        startActivity(in);
    }
    @Override
    public void onSelect(int id) {
        StocksDetalle_Fragment frDetalle = (StocksDetalle_Fragment) getSupportFragmentManager().findFragmentById(R.id.detallestocks);

        if (frDetalle != null && frDetalle.isInLayout()) {
            frDetalle.displayWithId(id);

        } else {

            Intent i = new Intent(this, StocksDetalle_Activity.class);
            i.putExtra("id", id);
            startActivity(i);
        }

    }
}
