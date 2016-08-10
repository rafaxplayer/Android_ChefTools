package rafaxplayer.cheftools.Orders.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.Order_Product;
import rafaxplayer.cheftools.Globalclasses.Product;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;
import rafaxplayer.cheftools.providers.ProviderNewEdit_Activity;

public class OrdersNewEdit_Fragment extends Fragment {
    private SqliteWrapper sql;
    private Menu menu;
    @BindView(R.id.editnameproduct)
    EditText NameProduct;
    @BindView(R.id.editCantidad)
    EditText Cantidadtxt;
    @BindView(R.id.textNameOrder)
    TextView NameOrder;
    @BindView(R.id.ButtonAddProduct)
    ImageButton addProduct;
    @BindView(R.id.ButtonSaveProduct)
    ImageButton saveProduct;
    @BindView(R.id.list_items)
    RecyclerView OrdersList;

    private ImageButton addSupplierOrder;
    private Spinner suppliersSpinner;
    private EditText name;
    private EditText Comentariostxt;
    private MaterialDialog dialogNewOrder;
    private int ID;
    private int supplierId;
    private Product prod;
    private String nameorder;
    private ArrayList<HashMap<String, Object>> arrSuppliers;

    public static OrdersNewEdit_Fragment newInstance(int id) {
        OrdersNewEdit_Fragment f = new OrdersNewEdit_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders_new_edit, container, false);
        ButterKnife.bind(this, v);
        OrdersList.setHasFixedSize(true);
        OrdersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        OrdersList.setItemAnimator(new DefaultItemAnimator());
        OrdersList.setAdapter(new RecyclerAdapter(new ArrayList<Order_Product>()));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogNewOrder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.new_list_order_dlg, true)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!sql.IsOpen()) {
                            sql.open();
                        }
                        name = ButterKnife.findById(dialog.getCustomView(), R.id.editnameorder);
                        if (sql.CheckIsDataAlreadyInDBorNot(DBHelper.TABLE_PEDIDOS, DBHelper.NAME, name.getText().toString())) {

                            Toast.makeText(getActivity(), getString(R.string.dlgerror_dataexist), Toast.LENGTH_LONG).show();
                            GlobalUttilities.animateView(getActivity(), name);
                            getActivity().onBackPressed();
                            return;
                        }

                        if (TextUtils.isEmpty(name.getText())) {

                            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                            GlobalUttilities.animateView(getActivity(), name);
                            return;
                        }
                        Comentariostxt = ButterKnife.findById(dialog.getCustomView(), R.id.editcomment);
                        long ret = sql.newOrdersListName(name.getText().toString(), supplierId, Comentariostxt.getText().toString());
                        if (ret != -1) {
                            ID = (int) ret;
                            nameorder = name.getText().toString();
                            Toast.makeText(getActivity(), "Ok, lista guardada con Nombre : " + nameorder, Toast.LENGTH_LONG).show();
                            NameOrder.setText(nameorder);

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                        }
                    }
                })

                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        getActivity().onBackPressed();
                    }
                })
                .build();

        suppliersSpinner = ButterKnife.findById(dialogNewOrder.getCustomView(), R.id.spinnerSuppliers);
        addSupplierOrder = ButterKnife.findById(dialogNewOrder.getCustomView(), R.id.ButtonAddSupplier);
        suppliersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                supplierId = Integer.valueOf(((TextView) view.findViewById(R.id.iddata)).getText().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addSupplierOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProviderNewEdit_Activity.class));

            }
        });
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ID > 0) {
                    ProductosMannager_Fragment newFragment = ProductosMannager_Fragment.newInstance(0, true);
                    newFragment.show(getActivity().getSupportFragmentManager(), "dialogproductos");
                } else {

                    Toast.makeText(getActivity(), "Debes crear una lista antes de nada", Toast.LENGTH_LONG).show();

                }

            }
        });
        saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(ID > 0)) {
                    Toast.makeText(getActivity(), "Debes crear una lista antes de nada", Toast.LENGTH_LONG).show();

                    return;
                }
                if (TextUtils.isEmpty(NameProduct.getText())) {
                    Toast.makeText(getActivity(), "Debes buscar un producto", Toast.LENGTH_LONG).show();
                    GlobalUttilities.animateView(getActivity(), addProduct);
                    return;
                }
                if (TextUtils.isEmpty(Cantidadtxt.getText())) {

                    Toast.makeText(getActivity(), "Introduce una cantidad de producto", Toast.LENGTH_LONG).show();
                    GlobalUttilities.animateView(getActivity(), Cantidadtxt);
                    return;
                }

                int cantidad = Integer.parseInt(Cantidadtxt.getText().toString());

                ((RecyclerAdapter) OrdersList.getAdapter()).addItem(0, ID, cantidad, prod);
                GlobalUttilities.ocultateclado(getActivity(), Cantidadtxt);
            }

        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();
        supplierId = 0;

        if (getArguments() != null) {

            this.ID = getArguments().getInt("id");

        } else {

            this.ID = 0;
            dialogNewOrder.show();
        }
        this.setRetainInstance(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sql.IsOpen()) {
            sql.open();
        }
        arrSuppliers = sql.getFormatsOrCategorysData(DBHelper.TABLE_PROVEEDORES);
        SimpleAdapter AdapterProv = new SimpleAdapter(getActivity(), arrSuppliers, R.layout.spinnerrow, new String[]{"ID", "Name"}, new int[]{R.id.iddata, R.id.spinnertext});
        suppliersSpinner.setAdapter(AdapterProv);


        if (this.ID != 0) {

            displayWithId(this.ID);

        } else {

            dialogNewOrder.show();
            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.new_orderslist));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        inflater.inflate(R.menu.menu_orders_list, menu);
        menu.findItem(R.id.search).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.neworder) {

            refresh();
            onResume();

            return true;
        }
        return onOptionsItemSelected(item);
    }


    public void displayProductWithId(int id) {

        if (!sql.IsOpen()) {
            sql.open();
        }
        try {
            Product pr = (Product) sql.SelectWithId("Product", DBHelper.TABLE_PRODUCTOS, id);
            NameProduct.setText(pr.getName().toString());
            this.prod = pr;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void displayWithId(int id) {

        if (!sql.IsOpen()) {
            sql.open();
        }
        String name = sql.getSimpleData(id, DBHelper.NAME, DBHelper.TABLE_PEDIDOS);
        NameOrder.setText(name.toString());
        ArrayList<Order_Product> listProducts = sql.getProductListOrder(id);
        if (listProducts.size() > 0) {
            OrdersList.setAdapter(new RecyclerAdapter(listProducts));
        }

    }

    public void refresh() {

        NameProduct.setText("");

        OrdersList.setAdapter(new RecyclerAdapter(new ArrayList<Order_Product>()));
        this.ID = 0;
        supplierId = 0;
        dialogNewOrder.show();

    }

    public void resetProduct() {
        NameProduct.setText("");
        Cantidadtxt.setText("");

    }

    @Override
    public void onPause() {
        super.onPause();
        sql.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private ArrayList<Order_Product> mDataset;

        public RecyclerAdapter(ArrayList<Order_Product> myDataset) {
            mDataset = myDataset;
        }

        public void deleteItem(int pos) {

            int count = sql.DeleteWithId(((Order_Product) mDataset.get(pos)).getID(), DBHelper.TABLE_PEDIDOS_LISTAS);

            if (count > 0) {
                mDataset.remove(pos);
                Toast.makeText(getActivity(), "Ok , item deleted", Toast.LENGTH_LONG).show();
            }
            //notifyItemInserted(mDataset.size() - 1);
            notifyItemRemoved(pos);
        }

        public void addItem(int pos, int listID, int cantidad, Product pr) {

            if (!sql.IsOpen()) {
                sql.open();
            }
            String query = "SELECT * FROM " + DBHelper.TABLE_PEDIDOS_LISTAS + " WHERE " + DBHelper.PEDIDO_ID + " = " + listID + " AND " + DBHelper.PRODUCTO_ID + " = " + pr.getId();
            if (sql.freeQueryExistsorNot(query)) {
                Toast.makeText(getActivity(), getString(R.string.product_exist), Toast.LENGTH_LONG).show();
                return;
            }

            Order_Product orPr = new Order_Product();
            orPr.setListaId(listID);
            orPr.setCantidad(cantidad);
            orPr.setProductoId(pr.getId());
            orPr.setFormatoid(pr.getFormatoid());
            orPr.setCategoriaid(pr.getCategoryid());

            long id = sql.addProductListOrder(listID, pr.getId(), cantidad, pr.getCategoryid(), pr.getFormatoid());

            if (id > 0) {
                orPr.setID((int) id);
                mDataset.add(orPr);

                Toast.makeText(getActivity(), "Ok , Product added", Toast.LENGTH_LONG).show();
                resetProduct();
            }

            notifyDataSetChanged();

        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_simple, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int i) {
            if (!sql.IsOpen()) {
                sql.open();
            }
            String productName = sql.getSimpleData(((Order_Product) mDataset.get(i)).getProductoId(), DBHelper.NAME, DBHelper.TABLE_PRODUCTOS);
            String formatName = sql.getSimpleData(((Order_Product) mDataset.get(i)).getProductoId(), DBHelper.PRODUCTO_FORMATO_NAME, DBHelper.TABLE_PRODUCTOS);

            viewHolder.txtProd.setText(productName);
            viewHolder.txtCantidad.setText(String.valueOf(((Order_Product) mDataset.get(i)).getCantidad()));
            viewHolder.txtFormat.setText(formatName);
        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.ButtonDeleteProduct)
            ImageButton delButton;
            @BindView(R.id.text1)
            TextView txtProd;
            @BindView(R.id.text2)
            TextView txtCantidad;
            @BindView(R.id.text3)
            TextView txtFormat;

            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);

                delButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ButtonDeleteProduct) {
                    deleteItem(ViewHolder.this.getLayoutPosition());
                }

            }
        }
    }
}