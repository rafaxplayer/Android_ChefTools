package rafaxplayer.cheftools.escandallos.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Escandallo;
import rafaxplayer.cheftools.Globalclasses.models.Escandallo_Product;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;


public class EscandalloNewEdit_Fragment extends Fragment {
    @BindView(R.id.list_items)
    RecyclerView listProducts;
    @BindView(R.id.texttotal)
    TextView texttotal;
    @BindString(R.string.product_cost)
    String productCost;
    @BindString(R.string.cost_total)
    String costTotal;
    @BindView(R.id.escandalloName)
    EditText editName;

    @OnClick(R.id.fab)
    public void submit(View view) {
        if (TextUtils.isEmpty(editName.getText())) {
            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();

        } else {
            dialogNewProduct.show();
        }
    }

    private MaterialDialog dialogNewProduct;
    private EditText editnameProduct;
    private EditText editcostuni;
    private EditText editquantity;
    private Spinner spinerFormat;
    private TextView textviewuni;
    private TextView textFormat2;
    private TextView textCosteProducto;
    private double dataUni = 1000;
    private int ID;
    private SqliteWrapper sql;

    public EscandalloNewEdit_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EscandalloNewEdit_Fragment newInstance(int id) {

        EscandalloNewEdit_Fragment fragment = new EscandalloNewEdit_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();
        if (getArguments() != null) {
            this.ID = getArguments().getInt("id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_escandallo_new_edit, container, false);
        ButterKnife.bind(this, v);
        dialogNewProduct = createDilaogNewProduct();
        listProducts.setHasFixedSize(true);
        listProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        listProducts.setItemAnimator(new DefaultItemAnimator());
        listProducts.setAdapter(new EscandalloNewEdit_Fragment.RecyclerAdapter(new ArrayList<Escandallo_Product>()));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        sql.open();

        if (this.ID != 0) {
            displayWithId(this.ID);
        } else {
            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.new_escandallo));
        }

    }



    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        MenuItem share = menu.findItem(R.id.action_save);
        share.setTitle(R.string.save_cost);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    private void save() {

        if (!sql.IsOpen()) {
            sql.open();
        }
        ArrayList<Escandallo_Product> list = ((RecyclerAdapter) listProducts.getAdapter()).getProducts();

        Escandallo esc = new Escandallo();
        esc.setName(editName.getText().toString());
        esc.setFecha( GlobalUttilities.getDateTime());
        String cost = texttotal.getText().toString().replace(costTotal, "").replace("€", "");

        if (cost.length() > 0)
            esc.setCostetotal(Double.valueOf(cost));

        if (TextUtils.isEmpty(editName.getText())) {
            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
            return;
        }

        if (!(list.size() > 0)) {
            Toast.makeText(getActivity(), getString(R.string.dlgerror_emptyproducts), Toast.LENGTH_LONG).show();
            return;
        }

        if (this.ID == 0) {

            long id = sql.InsertObject(esc);
            if (id != -1) {

                for(Escandallo_Product pr : list){
                    pr.setEscandalloid((int)id);
                    sql.InsertObject(pr);
                }

                new MaterialDialog.Builder(getActivity())
                        .title(getString(R.string.dlgsucces_saved))
                        .content(getString(R.string.dlgnew_saved))
                        .positiveText(R.string.yes)

                        .negativeText(R.string.not)

                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                refresh();
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                getActivity().onBackPressed();
                                dialog.dismiss();

                            }
                        })
                        .show();
            }
        } else {

            long count = sql.UpdateWithId(esc, this.ID);
            if (count > 0) {

                sql.DeleteWithValue(DBHelper.ESCANDALLO_ID,String.valueOf(this.ID),DBHelper.TABLE_ESCANDALLOS_PRODUCTS);
                for(Escandallo_Product pr : list){
                    pr.setEscandalloid((int)this.ID);
                    sql.InsertObject(pr);
                }

                Toast.makeText(getActivity(), getString(R.string.dlgok_update), Toast.LENGTH_LONG).show();
                if ((getActivity().getSupportFragmentManager().findFragmentByTag("escandallodetalle")) != null) {
                    (getActivity().getSupportFragmentManager().findFragmentByTag("escandallodetalle")).onResume();
                }
                getActivity().onBackPressed();
            }

        }

    }

    private void refresh() {
        editName.setText("");
        editquantity.setText("");
        ((RecyclerAdapter) listProducts.getAdapter()).clear();
        texttotal.setText("");

    }

    private ArrayList<HashMap<String, Object>> generateSpinnerDta() {
        ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> mp = new HashMap<String, Object>();
        mp.put("Cant", 1000);
        mp.put("Uni", "Gr");
        mp.put("Name", "KG");
        lst.add(mp);
        HashMap<String, Object> mp2 = new HashMap<String, Object>();
        mp2.put("Cant", 1000);
        mp2.put("Uni", "Cl");
        mp2.put("Name", "Ltr");
        lst.add(mp2);
        HashMap<String, Object> mp3 = new HashMap<String, Object>();
        mp3.put("Cant", 1);
        mp3.put("Uni", "Uni");
        mp3.put("Name", "Uni");
        lst.add(mp3);
        return lst;
    }

    private MaterialDialog createDilaogNewProduct() {
        return new MaterialDialog.Builder(getActivity())
                .customView(R.layout.new_product_escandallo_dlg, true)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .autoDismiss(false)
                .showListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        View view = dialogNewProduct.getCustomView();
                        editnameProduct = ButterKnife.findById(view, R.id.editnameproduct);
                        editnameProduct.setText("");
                        editcostuni = ButterKnife.findById(view, R.id.editcostporuni);
                        editcostuni.setText("");
                        editcostuni.addTextChangedListener(new TextWatcher() {
                            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                            }

                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                            }

                            public void afterTextChanged(Editable arg0) {
                                String str = editcostuni.getText().toString();
                                if (str.isEmpty()) return;
                                String str2 = GlobalUttilities.PerfectDecimal(str, 5, 2);

                                if (!str2.equals(str)) {
                                    editcostuni.setText(str2);
                                    int pos = editcostuni.getText().length();
                                    editcostuni.setSelection(pos);
                                }
                            }
                        });
                        editquantity = ButterKnife.findById(view, R.id.editunicant);
                        editquantity.setText("");
                        spinerFormat = ButterKnife.findById(view, R.id.spinnerFormat);
                        textCosteProducto = ButterKnife.findById(view, R.id.textcosteProduct);
                        final ArrayList<HashMap<String, Object>> spinnerFormatsData = generateSpinnerDta();
                        final SimpleAdapter AdapterProv = new SimpleAdapter(getActivity(), spinnerFormatsData, R.layout.spinnerrow, new String[]{"Cant", "Name"}, new int[]{R.id.iddata, R.id.spinnertext});
                        spinerFormat.setAdapter(AdapterProv);
                        spinerFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String newText = getString(R.string.product_cost).replace("#", ((TextView) view.findViewById(R.id.spinnertext)).getText().toString());
                                textviewuni.setText(newText);
                                textFormat2.setText((String) spinnerFormatsData.get(position).get("Uni"));
                                dataUni = Double.valueOf(((TextView) view.findViewById(R.id.iddata)).getText().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        textviewuni = ButterKnife.findById(view, R.id.textViewUni);
                        textFormat2 = ButterKnife.findById(view, R.id.textFormat2);
                        editcostuni = ButterKnife.findById(view, R.id.editcostporuni);
                        String newText = productCost.replace("#", "KG");
                        textviewuni.setText(newText);
                        textCosteProducto.setText(costTotal);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        if (TextUtils.isEmpty(editnameProduct.getText().toString())) {
                            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                            return;

                        }
                        if (TextUtils.isEmpty(editcostuni.getText().toString())) {
                            Toast.makeText(getActivity(), getString(R.string.dlgerror_costuni), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (TextUtils.isEmpty(editquantity.getText().toString())) {
                            Toast.makeText(getActivity(), getString(R.string.dlgerror_quantity), Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {

                            Double result = Double.valueOf(editquantity.getText().toString()) * Double.valueOf(editcostuni.getText().toString()) / Double.valueOf(dataUni);
                            String textResult = String.format("%s %s", costTotal, GlobalUttilities.FormatDecimal(result));
                            textCosteProducto.setText(textResult);

                            Escandallo_Product escpr = new Escandallo_Product();
                            escpr.setProductoname(editnameProduct.getText().toString());
                            escpr.setCostforuni(editcostuni.getText().toString());
                            escpr.setCantidad(editquantity.getText().toString());
                            escpr.setFormato(textFormat2.getText().toString());
                            escpr.setCoste(Double.valueOf(GlobalUttilities.FormatDecimal(result)));


                            ((EscandalloNewEdit_Fragment.RecyclerAdapter) listProducts.getAdapter()).addItem(escpr);
                            Double sum = ((EscandalloNewEdit_Fragment.RecyclerAdapter) listProducts.getAdapter()).calculatecostetotal();
                            texttotal.setText(String.format("%s %s%s", costTotal, GlobalUttilities.FormatDecimal(sum), "€"));
                            dialog.dismiss();

                        } catch (NumberFormatException ex) {
                            Log.e("Error : ", ex.getMessage());

                        } catch (Exception e) {
                            Log.e("Error : ", e.getMessage());
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

    }


    public class RecyclerAdapter extends RecyclerView.Adapter<EscandalloNewEdit_Fragment.RecyclerAdapter.ViewHolder> {

        public ArrayList<Escandallo_Product> mDataset;

        public RecyclerAdapter(ArrayList<Escandallo_Product> myDataset) {

            mDataset = myDataset;
        }

        public void clear() {
            this.mDataset.clear();
            notifyDataSetChanged();
        }

        public void deleteItem(int pos) {

            mDataset.remove(pos);

            Toast.makeText(getActivity(), "Ok , item deleted", Toast.LENGTH_LONG).show();

            notifyItemRemoved(pos);
        }

        public void addItem(Escandallo_Product escPr) {

            mDataset.add(escPr);

            notifyDataSetChanged();
            Toast.makeText(getActivity(), "Ok, Product added", Toast.LENGTH_LONG).show();

        }

        public ArrayList<Escandallo_Product> getProducts() {
            return mDataset;
        }

        public double calculatecostetotal() {

            double sum = 0;
            try {
                for (int i = 0; i < mDataset.size(); i++) {

                    sum = sum + mDataset.get(i).getCoste();
                }
            } catch (Exception e) {
                Log.e("error :", e.getMessage());
            }

            return Double.valueOf(sum);
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_simple, parent, false);

            RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(EscandalloNewEdit_Fragment.RecyclerAdapter.ViewHolder viewHolder, int i) {
            Escandallo_Product escPr = mDataset.get(i);
            viewHolder.txtProd.setText(escPr.getProductoname());
            viewHolder.txtCantidad.setText(String.format("%s%s", escPr.getCantidad(), escPr.getFormato()));
            viewHolder.txtCoste.setText(String.format("%s€", String.valueOf(escPr.getCoste())));
        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public ImageButton delButton;
            public TextView txtProd;
            public TextView txtCantidad;
            public TextView txtCoste;

            public ViewHolder(View v) {
                super(v);
                delButton = v.findViewById(R.id.ButtonDeleteProduct);
                v.findViewById(R.id.ButtonEditProduct).setVisibility(View.GONE);
                txtProd = v.findViewById(R.id.text1);
                txtCantidad = v.findViewById(R.id.text2);
                txtCoste = v.findViewById(R.id.text3);
                delButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ButtonDeleteProduct) {
                    deleteItem(EscandalloNewEdit_Fragment.RecyclerAdapter.ViewHolder.this.getLayoutPosition());
                    double sum = calculatecostetotal();
                    texttotal.setText(String.format("%s %s%s", costTotal, String.valueOf(sum), "€"));
                }

            }
        }
    }

    private void displayWithId(int id) {

        if (!sql.IsOpen()) {
            sql.open();
        }
        Escandallo esc = (Escandallo) sql.SelectWithId("Escandallo", DBHelper.TABLE_ESCANDALLOS, id);
        if(esc != null){
            editName.setText(esc.getName());
            texttotal.setText(String.format("%s %s%s", costTotal, esc.getCostetotal(), "€"));
            ArrayList<Escandallo_Product> listpr = (ArrayList<Escandallo_Product>) (Object)sql.getProductListWithListId("Escandallo_product",esc.getId());
            listProducts.setAdapter(new RecyclerAdapter(listpr));
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
