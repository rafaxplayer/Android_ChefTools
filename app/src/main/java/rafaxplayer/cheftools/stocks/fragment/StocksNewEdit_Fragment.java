package rafaxplayer.cheftools.stocks.fragment;

import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Product;
import rafaxplayer.cheftools.Globalclasses.models.Stock_Product;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;

public class StocksNewEdit_Fragment extends Fragment {

    @BindView(R.id.textNameOrder)
    TextView NameStock;
    @BindView(R.id.editnameproduct)
    EditText NameProduct;
    @BindView(R.id.editCantidad)
    EditText Cantidadtxt;
    @BindView(R.id.ButtonAddProduct)
    ImageButton addProduct;
    @BindView(R.id.ButtonSaveProduct)
    ImageButton saveProduct;
    @BindView(R.id.list_items)
    RecyclerView StocksList;

    private SqliteWrapper sql;
    private TextView newlisttxt;
    private EditText name;
    private EditText Comentariostxt;
    private EditText EditCantidad;
    private LinearLayout providerPannel;
    private MaterialDialog dialogNewStock;
    private MaterialDialog dialogEditProduct;
    private int ID;
    private Product prod;
    private Stock_Product stProd;
    private String namestock;

    public static StocksNewEdit_Fragment newInstance(int id) {
        StocksNewEdit_Fragment f = new StocksNewEdit_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stocks_new_edit, container, false);
        ButterKnife.bind(this, v);
        StocksList.setHasFixedSize(true);
        StocksList.setLayoutManager(new LinearLayoutManager(getActivity()));
        StocksList.setItemAnimator(new DefaultItemAnimator());
        StocksList.setAdapter(new RecyclerAdapter(new ArrayList<Stock_Product>()));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogNewStock = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.new_list_order_dlg, true)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        newlisttxt = dialogNewStock.getCustomView().findViewById(R.id.textnewlist);
                        newlisttxt.setText(getString(R.string.menu_new_stock));
                        providerPannel = dialogNewStock.getCustomView().findViewById(R.id.providerpanel);
                        providerPannel.setVisibility(View.GONE);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!sql.IsOpen()) {
                            sql.open();
                        }
                        name = dialog.getCustomView().findViewById(R.id.editnameorder);
                        if (sql.CheckIsDataAlreadyInDBorNot(DBHelper.TABLE_PEDIDOS, DBHelper.NAME, name.getText().toString())) {

                            Toast.makeText(getActivity(), getString(R.string.dlgerror_dataexist), Toast.LENGTH_LONG).show();
                            GlobalUttilities.animateView(getActivity(), name);
                            getActivity().onBackPressed();
                            return;
                        }

                        if (!(name.getText().length() > 0)) {

                            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                            GlobalUttilities.animateView(getActivity(), name);
                            return;
                        }

                        Comentariostxt = dialog.getCustomView().findViewById(R.id.editcomment);
                        long ret = sql.newStocksListName(name.getText().toString(), Comentariostxt.getText().toString());
                        if (ret != -1) {
                            ID = (int) ret;
                            namestock = name.getText().toString();
                            Toast.makeText(getActivity(), "Ok, lista guardada con Nombre : " + namestock, Toast.LENGTH_LONG).show();
                            NameStock.setText(namestock);

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

        dialogEditProduct = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.edit_product_escandallo_dlg, true)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        EditCantidad = dialogEditProduct.getCustomView().findViewById(R.id.editcantproduct);
                        if (stProd != null) {
                            EditCantidad.setText(String.valueOf(stProd.getCantidad()));
                        }
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!sql.IsOpen()) {
                            sql.open();
                        }
                        long count = sql.UpdateSimpleData(DBHelper.TABLE_INVENTARIOS_LISTAS, DBHelper.PRODUCTO_CANTIDAD, EditCantidad.getText().toString(), stProd.getID());
                        if (count > 0) {
                            Toast.makeText(getActivity(), "Ok Producto actualizado", Toast.LENGTH_LONG).show();
                            displayWithId(StocksNewEdit_Fragment.this.ID);
                            dialog.dismiss();

                        } else {
                            Toast.makeText(getActivity(), "Ocurrio un error al actualizar", Toast.LENGTH_LONG).show();
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
                ((RecyclerAdapter) StocksList.getAdapter()).addItem(ID, cantidad, prod);
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


        if (getArguments() != null) {
            this.ID = getArguments().getInt("id");

        } else {

            this.ID = 0;
            dialogNewStock.show();
        }
        this.setRetainInstance(true);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sql.IsOpen()) {
            sql.open();
        }


        if (this.ID != 0) {

            displayWithId(this.ID);

        } else {

            dialogNewStock.show();
            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.activity_newedit_stocks));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Menu menu1 = menu;
        inflater.inflate(R.menu.menu_lists, menu);
        menu.findItem(R.id.search).setVisible(false);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.newelement) {

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
            NameProduct.setText(pr.getName());
            this.prod = pr;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void displayWithId(int id) {

        if (!sql.IsOpen()) {
            sql.open();
        }
        String name = sql.getSimpleData(id, DBHelper.NAME, DBHelper.TABLE_INVENTARIOS);
        NameStock.setText(name);
        ArrayList<Stock_Product> listProducts = (ArrayList<Stock_Product>) (Object) sql.getProductListWithListId("Stock_product", id);
        if (listProducts.size() > 0) {
            StocksList.setAdapter(new RecyclerAdapter(listProducts));
        }

    }

    private void refresh() {

        NameProduct.setText("");
        StocksList.setAdapter(new RecyclerAdapter(new ArrayList<Stock_Product>()));
        this.ID = 0;

        dialogNewStock.show();

    }

    private void resetProduct() {
        NameProduct.setText("");
        Cantidadtxt.setText("");

    }

    @Override
    public void onPause() {
        super.onPause();
        sql.close();
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private final ArrayList<Stock_Product> mDataset;

        RecyclerAdapter(ArrayList<Stock_Product> myDataset) {
            mDataset = myDataset;
        }

        void deleteItem(int pos) {

            int count = sql.DeleteWithId((mDataset.get(pos)).getID(), DBHelper.TABLE_INVENTARIOS_LISTAS);

            if (count > 0) {
                mDataset.remove(pos);
                Toast.makeText(getActivity(), getString(R.string.productdeleted), Toast.LENGTH_LONG).show();
            }

            notifyItemRemoved(pos);
        }

        void addItem(int listID, int cantidad, Product pr) {

            if (!sql.IsOpen()) {
                sql.open();
            }
            String query = "SELECT * FROM " + DBHelper.TABLE_INVENTARIOS_LISTAS
                    + " WHERE " + DBHelper.INVENTARIO_ID
                    + " = " + listID + " AND "
                    + DBHelper.PRODUCTO_ID + " = "
                    + pr.getId();

            if (sql.freeQueryExistsorNot(query)) {
                Toast.makeText(getActivity(), getString(R.string.product_exist), Toast.LENGTH_LONG).show();
                return;
            }

            Stock_Product orPr = new Stock_Product();
            orPr.setCantidad(cantidad);
            orPr.setProductoId(pr.getId());
            orPr.setFormatoid(pr.getFormatoid());
            orPr.setCategoriaid(pr.getCategoryid());

            long id = sql.addProductListStock(listID, pr.getId(), cantidad, pr.getCategoryid(), pr.getFormatoid());

            if (id > 0) {
                orPr.setID((int) id);
                mDataset.add(orPr);
                Toast.makeText(getActivity(), "Ok , Product added", Toast.LENGTH_LONG).show();
                resetProduct();

            }

            notifyDataSetChanged();
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
            String productName = sql.getSimpleData((mDataset.get(i)).getProductoId(), DBHelper.NAME, DBHelper.TABLE_PRODUCTOS);
            String formatName = sql.getSimpleData((mDataset.get(i)).getProductoId(), DBHelper.PRODUCTO_FORMATO_NAME, DBHelper.TABLE_PRODUCTOS);

            viewHolder.txtProd.setText(productName);
            viewHolder.txtCantidad.setText(String.valueOf((mDataset.get(i)).getCantidad()));
            viewHolder.txtFormat.setText(formatName);
        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.ButtonDeleteProduct)
            ImageButton delButton;
            @BindView(R.id.ButtonEditProduct)
            ImageButton editButton;
            @BindView(R.id.text1)
            TextView txtProd;
            @BindView(R.id.text2)
            TextView txtCantidad;
            @BindView(R.id.text3)
            TextView txtFormat;

            ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
                delButton.setOnClickListener(this);
                editButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ButtonDeleteProduct) {

                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.deleteproducttitle)
                            .content(R.string.deleteproductmsg)
                            .theme(Theme.LIGHT)
                            .positiveText(R.string.yes)
                            .negativeText(R.string.not)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    deleteItem(ViewHolder.this.getLayoutPosition());
                                    dialog.dismiss();
                                }
                            })

                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    dialog.dismiss();
                                }
                            })

                            .show();
                    return;
                }
                if (v.getId() == R.id.ButtonEditProduct) {
                    stProd = mDataset.get(ViewHolder.this.getLayoutPosition());
                    dialogEditProduct.show();
                    Toast.makeText(getActivity(), String.valueOf(mDataset.get(ViewHolder.this.getLayoutPosition()).getID()), Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
