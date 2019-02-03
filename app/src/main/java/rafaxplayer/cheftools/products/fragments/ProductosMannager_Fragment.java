package rafaxplayer.cheftools.products.fragments;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Product;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;


public class ProductosMannager_Fragment extends DialogFragment {
    private static String TAG = ProductosMannager_Fragment.class.getSimpleName();
    @BindView(R.id.list_items)
    RecyclerView ListProducts;
    @BindView(R.id.spinnerCategory2)
    Spinner catsSpinner2;
    @BindView(R.id.newproduct)
    FloatingActionButton addNewProduct;
    private OnSelectedCallback mCallback;
    private Spinner catsSpinner;
    private Spinner formatsSpinner;
    private Spinner provSpinner;
    private SqliteWrapper sql;
    private EditText editName;
    private int productID;
    private ArrayList<HashMap<String, Object>> arrCats;
    private ArrayList<HashMap<String, Object>> arrFormats;
    private ArrayList<HashMap<String, Object>> arrSuppliers;
    private boolean modeSelect;
    private final int TYPECATEGORY = 1;
    private final int TYPENAME = 2;
    private boolean firstShowSpinner;
    private MaterialDialog dialogNewProduct;

    public static ProductosMannager_Fragment newInstance(int id, boolean modeSelect) {
        ProductosMannager_Fragment f = new ProductosMannager_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putBoolean("modeselect", modeSelect);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProductosMannager_Fragment.this.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        sql = new SqliteWrapper(getActivity());
        sql.open();
        int catId = 0;
        int ID = 0;
        int formatId = 0;
        int provId = 0;
        firstShowSpinner = true;
        if (getArguments() != null) {

            this.productID = getArguments().getInt("id");
            this.modeSelect = getArguments().getBoolean("modeselect");
        }
        this.setRetainInstance(true);

        try {
            mCallback = (OnSelectedCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_productos_mannager, container, false);
        ButterKnife.bind(this, v);

        ListProducts.setHasFixedSize(true);
        ListProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        ListProducts.setItemAnimator(new DefaultItemAnimator());


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogNewProduct = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.new_product_dlg, true)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        if (productID > 0) {
                            displayWithId(productID);
                        }
                        GlobalUttilities.ocultateclado(getActivity(), editName);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!sql.IsOpen()) {
                            sql.open();
                        }
                        String cat = ((HashMap<String, Object>) catsSpinner.getSelectedItem()).get("Name").toString();
                        String format = ((HashMap<String, Object>) formatsSpinner.getSelectedItem()).get("Name").toString();
                        String supplier = ((HashMap<String, Object>) provSpinner.getSelectedItem()).get("Name").toString();
                        int catid = (int) ((HashMap<String, Object>) catsSpinner.getSelectedItem()).get("ID");
                        int formatid = (int) ((HashMap<String, Object>) formatsSpinner.getSelectedItem()).get("ID");
                        int supplierid = (int) ((HashMap<String, Object>) provSpinner.getSelectedItem()).get("ID");
                        if (!(editName.getText().length() > 0)) {

                            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                            GlobalUttilities.animateView(getActivity(), editName);
                            return;
                        }
                        if (productID > 0) {

                            Product pro = new Product();
                            pro.setName(editName.getText().toString());
                            pro.setId(productID);
                            pro.setFormatoname(format);
                            pro.setCategoryname(cat);
                            pro.setSuppliername(supplier);
                            pro.setCategoryid(catid);
                            pro.setFormatoid(formatid);
                            pro.setSupplierid(supplierid);
                            //Log.e("proID",String.valueOf(supplierid));
                            long count = sql.UpdateWithId(pro, productID);
                            if (count > 0) {

                                Toast.makeText(getActivity(), getString(R.string.dlgok_update), Toast.LENGTH_LONG).show();
                                refreshPannel();

                            }

                        } else {


                            if (sql.CheckIsDataAlreadyInDBorNot(DBHelper.TABLE_PRODUCTOS, DBHelper.NAME, editName.getText().toString())) {

                                Toast.makeText(getActivity(), getString(R.string.dlgerror_dataexist), Toast.LENGTH_LONG).show();

                                return;
                            }

                            long id = sql.addProduct(editName.getText().toString(), catid, formatid, supplierid);
                            if (id != -1) {
                                Toast.makeText(getActivity(), "Ok Procduct saved", Toast.LENGTH_LONG).show();
                                refreshPannel();
                                onResume();
                            } else {
                                Toast.makeText(getActivity(), "Error no saved", Toast.LENGTH_LONG).show();
                            }

                        }
                        GlobalUttilities.ocultateclado(getActivity(), editName);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })

                .build();
        editName = dialogNewProduct.getCustomView().findViewById(R.id.editnameproduct);
        catsSpinner = dialogNewProduct.getCustomView().findViewById(R.id.spinnerCategory);
        provSpinner = dialogNewProduct.getCustomView().findViewById(R.id.spinnerSupplier);
        formatsSpinner = dialogNewProduct.getCustomView().findViewById(R.id.spinnerFormat);
        addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNewProduct.show();
            }
        });


        catsSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null || firstShowSpinner) {
                    String cat = ((TextView) view.findViewById(R.id.spinnertext)).getText().toString();
                    ProductsAdapter.FilesFilter f = ((ProductsAdapter.FilesFilter) ((ProductsAdapter) ListProducts.getAdapter()).getFilter());
                    f.addtype(TYPECATEGORY);
                    f.filter(cat);
                    //Log.d(TAG,cat);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;

        Rect displayRectangle = new Rect();
        Window window = getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        int dialogWidth = (int) (displayRectangle.width() * 0.9f);
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_lists, menu);
        //menu.findItem(R.id.refresh).setVisible(false);
        menu.findItem(R.id.newelement).setVisible(false);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.search:

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.search)

                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Text to search...", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                if (input.length() > 0) {
                                    ProductsAdapter.FilesFilter f = ((ProductsAdapter.FilesFilter) ((ProductsAdapter) ListProducts.getAdapter()).getFilter());
                                    f.addtype(TYPENAME);
                                    f.filter(input.toString());
                                }

                                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                dialog.dismiss();
                            }

                        }).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sql.IsOpen()) {
            sql.open();
        }
        firstShowSpinner = false;
        arrCats = sql.getFormatsOrCategorysData(DBHelper.TABLE_PRODUCTOS_CATEGORY);
        SimpleAdapter Adaptercats = new SimpleAdapter(getActivity(), arrCats, R.layout.spinnerrow, new String[]{"ID", "Name"}, new int[]{R.id.iddata, R.id.spinnertext});
        catsSpinner.setAdapter(Adaptercats);

        catsSpinner2.setAdapter(Adaptercats);

        arrFormats = sql.getFormatsOrCategorysData(DBHelper.TABLE_PRODUCTOS_FORMATO);
        SimpleAdapter AdapterFormats = new SimpleAdapter(getActivity(), arrFormats, R.layout.spinnerrow, new String[]{"ID", "Name"}, new int[]{R.id.iddata, R.id.spinnertext});
        formatsSpinner.setAdapter(AdapterFormats);

        arrSuppliers = sql.getFormatsOrCategorysData(DBHelper.TABLE_PROVEEDORES);
        SimpleAdapter AdapterProv = new SimpleAdapter(getActivity(), arrSuppliers, R.layout.spinnerrow, new String[]{"ID", "Name"}, new int[]{R.id.iddata, R.id.spinnertext});
        provSpinner.setAdapter(AdapterProv);

        if (this.productID != 0) {

            displayWithId(this.productID);
        }

        List<Product> lstpr = (List<Product>) (Object) sql.getAllObjects("Product", null);

        ListProducts.setAdapter(new ProductsAdapter(lstpr));
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (lstpr.size() < 1) {
            GlobalUttilities.animateView(getActivity(), addNewProduct);
        }
    }

    private void refreshPannel() {
        editName.setText("");
        catsSpinner.setSelection(0);
        formatsSpinner.setSelection(0);
        provSpinner.setSelection(0);
        this.productID = 0;

    }

    private void displayWithId(int id) {
        if (!sql.IsOpen()) {
            sql.open();
        }
        modeSelect = false;
        Product pro = (Product) sql.SelectWithId("Product", DBHelper.TABLE_PRODUCTOS, id);
        if (pro != null && id > 0) {

            editName.setText(pro.getName());
            catsSpinner.setSelection(GlobalUttilities.SpinnergetIndex(arrCats, pro.getCategoryname()));
            formatsSpinner.setSelection(GlobalUttilities.SpinnergetIndex(arrFormats, pro.getFormatoname()));
            provSpinner.setSelection(GlobalUttilities.SpinnergetIndex(arrSuppliers, pro.getSuppliername()));
            this.productID = id;
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        sql.close();
        modeSelect = false;
    }

    public interface OnSelectedCallback {
        void onSelect(int pid);
    }

    public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> implements Filterable {

        Boolean searchResultsok;
        private List<Product> mDataset;
        private final List<Product> listorigin;
        private FilesFilter filefilter;


        // Adapter's Constructor
        ProductsAdapter(List<Product> myDataset) {
            mDataset = myDataset;
            listorigin = myDataset;
            this.searchResultsok = false;
        }

        void deleteItem(int pos) {

            int count = sql.DeleteWithId(mDataset.get(pos).getId(), DBHelper.TABLE_PRODUCTOS);

            if (count > 0) {
                mDataset.remove(pos);
                Toast.makeText(getActivity(), getString(R.string.productdeleted), Toast.LENGTH_LONG).show();
            }

            notifyItemRemoved(pos);
        }

        @NonNull
        @Override
        public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
            // Create a new view by inflating the row item xml.
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_dialogs, parent, false);

            // Set the view to the ViewHolder


            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.sName.setText(mDataset.get(position).getName());
            holder.sCat.setText(mDataset.get(position).getCategoryname());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        @Override
        public Filter getFilter() {

            if (filefilter == null) {
                filefilter = new FilesFilter();
            }
            return filefilter;

        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final ImageButton edit;
            final ImageButton delete;
            final TextView sName;
            final TextView sCat;

            ViewHolder(View v) {
                super(v);
                edit = v.findViewById(R.id.ButtonEdit);
                delete = v.findViewById(R.id.ButtonDelete);
                sName = v.findViewById(R.id.text1);
                sCat = v.findViewById(R.id.text2);

                v.setOnClickListener(this);
                delete.setOnClickListener(this);
                edit.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.ButtonDelete) {

                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.deleteproducttitle)
                            .content(R.string.deleteproductmsg)
                            .positiveText(R.string.yes)

                            .negativeText(R.string.cancel)

                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    deleteItem(ViewHolder.this.getLayoutPosition());
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {

                                    dialog.dismiss();
                                }
                            })
                            .show();
                }

                if (v.getId() == R.id.ButtonEdit) {
                    productID = mDataset.get(ViewHolder.this.getLayoutPosition()).getId();
                    dialogNewProduct.show();
                    //displayWithId(((Product) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());

                }
                if (v.getId() == R.id.item_product) {
                    int id = (mDataset.get(ViewHolder.this.getLayoutPosition())).getId();
                    if (modeSelect) {
                        if (mCallback != null) {
                            mCallback.onSelect(id);
                            getDialog().dismiss();
                        }
                    }
                }
                v.setSelected(true);

            }


        }

        private class FilesFilter extends Filter {
            int type;
            String strSearch;

            void addtype(int type) {
                this.type = type;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = mDataset;
                    results.count = mDataset.size();
                } else {

                    List<Product> nFilesList = new ArrayList<>();

                    for (Product p : listorigin) {
                        strSearch = type == TYPECATEGORY ? p.getCategoryname() : p.getName();
                        if (strSearch.toLowerCase().contains(constraint.toString().toLowerCase()))
                            nFilesList.add(p);
                    }

                    results.values = nFilesList;
                    results.count = nFilesList.size();

                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    mDataset = listorigin;
                    Toast.makeText(getActivity(), getString(R.string.noresults), Toast.LENGTH_LONG).show();
                    searchResultsok = false;
                } else {
                    mDataset = (ArrayList<Product>) results.values;
                    searchResultsok = true;
                }
                notifyDataSetChanged();
            }
        }
    }
}


