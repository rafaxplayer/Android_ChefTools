package rafaxplayer.cheftools.stocks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Stock_Product;
import rafaxplayer.cheftools.Globalclasses.models.Stocks;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.stocks.StocksDetalle_Activity;
import rafaxplayer.cheftools.stocks.Stocks_Activity;

/**
 * A placeholder fragment containing a simple view.
 */
public class StocksDetalle_Fragment extends Fragment {
    @BindView(R.id.stocknamedetalle)
    TextView stockName;
    @BindView(R.id.textComment)
    TextView stockComment;
    @BindView(R.id.list_items)
    RecyclerView listStock;
    private SqliteWrapper sql;
    private int ID;

    public static StocksDetalle_Fragment newInstance(int id) {
        StocksDetalle_Fragment fr = new StocksDetalle_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fr.setArguments(args);
        return fr;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stocks_detalle, container, false);
        ButterKnife.bind(this, v);
        listStock.setHasFixedSize(true);
        listStock.setLayoutManager(new LinearLayoutManager(getActivity()));
        listStock.setItemAnimator(new DefaultItemAnimator());
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            displayWithId(getArguments().getInt("id"));
            this.ID = getArguments().getInt("id");
        }
        setHasOptionsMenu(!(this.ID == 0));
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detalle, menu);
        MenuItem share = menu.findItem(R.id.share);
        MenuItem edit = menu.findItem(R.id.edit);
        share.setTitle(R.string.menu_share_stock);
        edit.setTitle(R.string.menu_edit_stock);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                if (ID != 0) {
                    boolean islayout = (getActivity().getSupportFragmentManager().findFragmentById(R.id.detallestocks) != null);
                    if (getResources().getBoolean(R.bool.dual_pane) && islayout) {
                        ((Stocks_Activity) getActivity()).showMenuEdit(ID);
                    } else {
                        ((StocksDetalle_Activity) getActivity()).showMenuEdit(ID);
                    }

                }

                break;
            case R.id.share:
                if (!sql.IsOpen()) {
                    sql.open();
                }
                if (ID != 0) {
                    Stocks stock = (Stocks) sql.SelectWithId("Stocks", DBHelper.TABLE_INVENTARIOS, ID);

                    String sharedStr = GlobalUttilities.shareDataText(getActivity(), stock);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("*/*");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, sharedStr);

                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_recipe_use)));
                }
                //Toast.makeText(getActivity(), "Share", Toast.LENGTH_LONG).show();

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayWithId(int id) {

        if (!sql.IsOpen()) {
            sql.open();
        }

        Stocks ord = (Stocks) sql.SelectWithId("Stocks", DBHelper.TABLE_INVENTARIOS, id);
        if (ord != null) {

            stockName.setText(ord.getName());
            stockComment.setText(ord.getComentario());
            ArrayList<Stock_Product> listProducts = (ArrayList<Stock_Product>) (Object) sql.getProductListWithListId("Stock_product", id);

            listStock.setAdapter(new RecyclerAdapter(listProducts));


        }
        this.ID = id;
        setHasOptionsMenu(true);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (sql.IsOpen()) {
            sql.close();
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private final ArrayList<Stock_Product> mDataset;

        RecyclerAdapter(ArrayList<Stock_Product> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_simple, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder viewHolder, int i) {
            if (!sql.IsOpen()) {
                sql.open();
            }
            String productName = sql.getSimpleData(mDataset.get(i).getProductoId(), DBHelper.NAME, DBHelper.TABLE_PRODUCTOS);
            String formatName = sql.getSimpleData(mDataset.get(i).getProductoId(), DBHelper.PRODUCTO_FORMATO_NAME, DBHelper.TABLE_PRODUCTOS);
            viewHolder.delbut.setVisibility(View.GONE);
            viewHolder.editbut.setVisibility(View.GONE);
            viewHolder.txtProd.setText(productName);
            viewHolder.txtCantidad.setText(String.valueOf(mDataset.get(i).getCantidad()));
            viewHolder.txtFormat.setText(formatName);
        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            final TextView txtProd;
            final TextView txtCantidad;
            final TextView txtFormat;
            final ImageButton delbut;
            final ImageButton editbut;

            ViewHolder(View v) {
                super(v);
                delbut = v.findViewById(R.id.ButtonDeleteProduct);
                editbut = v.findViewById(R.id.ButtonEditProduct);
                txtProd = v.findViewById(R.id.text1);
                txtCantidad = v.findViewById(R.id.text2);
                txtFormat = v.findViewById(R.id.text3);
            }
        }
    }
}
