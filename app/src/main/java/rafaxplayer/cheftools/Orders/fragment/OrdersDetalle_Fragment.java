package rafaxplayer.cheftools.Orders.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import butterknife.OnClick;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Order_Product;
import rafaxplayer.cheftools.Globalclasses.models.Orders;
import rafaxplayer.cheftools.Orders.OrdersDetalle_Activity;
import rafaxplayer.cheftools.Orders.Orders_Activity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.providers.ProviderDetalle_Activity;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrdersDetalle_Fragment extends Fragment {
    @BindView(R.id.ordernamedetalle)
    TextView orderName;
    @BindView(R.id.textComment)
    TextView orderComment;
    @BindView(R.id.textSupplier)
    TextView orderSupplier;
    @BindView(R.id.list_items)
    RecyclerView listOrder;
    @BindView(R.id.buttonProviderCall)
    ImageButton buttonCall;
    private SqliteWrapper sql;
    private int ID;
    private String supplierTlf = "";

    public static OrdersDetalle_Fragment newInstance(int id) {
        OrdersDetalle_Fragment fr = new OrdersDetalle_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fr.setArguments(args);
        return fr;

    }

    @OnClick(R.id.textSupplier)
    public void view(TextView tv) {
        if (tv.getVisibility() == View.VISIBLE) {
            if (!sql.IsOpen()) {
                sql.open();
            }
            int idSuplier = Integer.valueOf(sql.getSimpleData(ID, DBHelper.PROVEEDOR_ID, DBHelper.TABLE_PEDIDOS));
            Intent in = new Intent(getActivity(), ProviderDetalle_Activity.class);
            in.putExtra("id", idSuplier);
            getActivity().startActivity(in);
        }

    }

    @OnClick(R.id.buttonProviderCall)
    public void submit() {
        if (!TextUtils.isEmpty(supplierTlf)) {
            GlobalUttilities.call(getActivity(), supplierTlf);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders_detalle, container, false);
        ButterKnife.bind(this, v);
        listOrder.setHasFixedSize(true);
        listOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOrder.setItemAnimator(new DefaultItemAnimator());
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
        share.setTitle(R.string.menu_share_order);
        edit.setTitle(R.string.edit_order);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                if (ID != 0) {
                    boolean islayout = (getActivity().getSupportFragmentManager().findFragmentById(R.id.detalleorders) != null);
                    if (getResources().getBoolean(R.bool.dual_pane) && islayout) {
                        ((Orders_Activity) getActivity()).showMenuEdit(ID);
                    } else {
                        ((OrdersDetalle_Activity) getActivity()).showMenuEdit(ID);
                    }

                }

                break;
            case R.id.share:
                if (!sql.IsOpen()) {
                    sql.open();
                }
                if (ID != 0) {
                    Orders ord = (Orders) sql.SelectWithId("Orders", DBHelper.TABLE_PEDIDOS, ID);
                    String sharedStr = GlobalUttilities.shareDataText(getActivity(), ord);
                    GlobalUttilities.shareIntenttext(getActivity(), sharedStr);

                }


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

        Orders ord = (Orders) sql.SelectWithId("Orders", DBHelper.TABLE_PEDIDOS, id);
        if (ord != null) {

            orderName.setText(ord.getName());
            orderComment.setText(ord.getComentario());
            String supplierName = sql.getSimpleData(ord.getSupplierid(), DBHelper.NAME, DBHelper.TABLE_PROVEEDORES);
            buttonCall.setVisibility(TextUtils.isEmpty(supplierName) ? View.GONE : View.VISIBLE);
            orderSupplier.setVisibility(TextUtils.isEmpty(supplierName) ? View.GONE : View.VISIBLE);
            this.supplierTlf = sql.getSimpleData(ord.getSupplierid(), DBHelper.PROVEEDOR_TELEFONO, DBHelper.TABLE_PROVEEDORES);
            orderSupplier.setText(supplierName);
            ArrayList<Order_Product> listProducts = (ArrayList<Order_Product>) (Object) sql.getProductListWithListId("Order_product", id);
            listOrder.setAdapter(new RecyclerAdapter(listProducts));

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

        private final ArrayList<Order_Product> mDataset;

        RecyclerAdapter(ArrayList<Order_Product> myDataset) {
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

            ViewHolder(View v) {
                super(v);
                delbut = v.findViewById(R.id.ButtonDeleteProduct);
                txtProd = v.findViewById(R.id.text1);
                txtCantidad = v.findViewById(R.id.text2);
                txtFormat = v.findViewById(R.id.text3);
            }
        }
    }
}
