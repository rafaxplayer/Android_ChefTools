package rafaxplayer.cheftools.products.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rafaxplayer.cheftools.Orders.fragment.OrdersNewEdit_Fragment;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.Product;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.recipes.NewEditRecipe_Activity;


public class ProductosMannager_Fragment extends DialogFragment {

    private OnSelectedCallback mCallback;
    private RecyclerView ListProducts;
    private FloatingActionButton addNewProduct;
    private Spinner catsSpinner;
    private Spinner catsSpinner2;
    private Spinner formatsSpinner;
    private Spinner provSpinner;
    private SqliteWrapper sql;
    private EditText editName;
    private int productID;
    private int CatId;
    private int ID;
    private int formatId;
    private int provId;
    private ArrayList<HashMap<String, Object>> arrCats;
    private ArrayList<HashMap<String, Object>> arrFormats;
    private ArrayList<HashMap<String, Object>> arrSuppliers;
    private boolean modeSelect;

    private int TYPECATEGORY = 1;
    private int TYPENAME = 2;
    private boolean firstShowSpinner;
    private MaterialDialog dialogNewProduct;
    private static String TAG = ProductosMannager_Fragment.class.getSimpleName();
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
        CatId = 0;
        ID = 0;
        formatId = 0;
        provId = 0;
        firstShowSpinner=true;
        if (getArguments() != null) {

            this.productID = getArguments().getInt("id");
            this.modeSelect = getArguments().getBoolean("modeselect");
        }
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_productos_mannager, container, false);


        ListProducts = (RecyclerView) v.findViewById(R.id.list_items);
        ListProducts.setHasFixedSize(true);
        ListProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        ListProducts.setItemAnimator(new DefaultItemAnimator());
        catsSpinner2 = (Spinner) v.findViewById(R.id.spinnerCategory2);
        addNewProduct = (FloatingActionButton) v.findViewById(R.id.newproduct);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
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

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                        dialog.dismiss();

                    }
                })
                .build();
        editName = (EditText) dialogNewProduct.getCustomView().findViewById(R.id.editnameproduct);
        catsSpinner = (Spinner) dialogNewProduct.getCustomView().findViewById(R.id.spinnerCategory);
        provSpinner = (Spinner) dialogNewProduct.getCustomView().findViewById(R.id.spinnerSupplier);
        formatsSpinner = (Spinner) dialogNewProduct.getCustomView().findViewById(R.id.spinnerFormat);
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
                    String cat=((TextView) view.findViewById(R.id.spinnertext)).getText().toString();
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

        inflater.inflate(R.menu.menu_list_recipes, menu);
        //menu.findItem(R.id.refresh).setVisible(false);
        menu.findItem(R.id.newrecipe).setVisible(false);


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
                            public void onInput(MaterialDialog dialog, CharSequence input) {
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
        firstShowSpinner=false;
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

    public void displayWithId(int id) {
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
        //sql.close();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnSelectedCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }


    }

    public interface OnSelectedCallback {
        public void onSelect(int pid);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onPause() {
        super.onPause();
        sql.close();
        modeSelect = false;
    }

    public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> implements Filterable {

        public Boolean searchResultsok;
        private List<Product> mDataset;
        private List<Product> listorigin;
        private FilesFilter filefilter;


        // Adapter's Constructor
        public ProductsAdapter(List<Product> myDataset) {
            mDataset = myDataset;
            listorigin = myDataset;
            this.searchResultsok = false;

        }


        public void deleteItem(int pos) {

            int count = sql.DeleteWithId(((Product) mDataset.get(pos)).getId(), DBHelper.TABLE_PRODUCTOS);

            if (count > 0) {
                mDataset.remove(pos);
                Toast.makeText(getActivity(), "Ok , Product deleted", Toast.LENGTH_LONG).show();
            }

            notifyItemRemoved(pos);
        }


        @Override
        public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
            // Create a new view by inflating the row item xml.
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_dialogs, parent, false);

            // Set the view to the ViewHolder
            ViewHolder holder = new ViewHolder(v);


            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.sName.setText(((Product) mDataset.get(position)).getName());

            holder.sCat.setText(((Product) mDataset.get(position)).getCategoryname());

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
            public ImageButton edit;
            public ImageButton delete;
            public TextView sName;
            public TextView sCat;

            int ID;

            public ViewHolder(View v) {
                super(v);
                edit = (ImageButton) v.findViewById(R.id.ButtonEdit);
                delete = (ImageButton) v.findViewById(R.id.ButtonDelete);
                sName = (TextView) v.findViewById(R.id.text1);
                sCat = (TextView) v.findViewById(R.id.text2);

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
                    productID = ((Product) mDataset.get(ViewHolder.this.getLayoutPosition())).getId();
                    dialogNewProduct.show();
                    //displayWithId(((Product) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());

                }
                if (v.getId() == R.id.item_product) {
                    int id = ((Product) mDataset.get(ViewHolder.this.getLayoutPosition())).getId();
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

            public void addtype(int type) {
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

                    List<Product> nFilesList = new ArrayList<Product>();

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


