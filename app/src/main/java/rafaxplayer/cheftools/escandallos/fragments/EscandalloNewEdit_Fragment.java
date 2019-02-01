package rafaxplayer.cheftools.escandallos.fragments;

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
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.ArrayList;
import java.util.HashMap;

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
    @BindView(R.id.comments)
    EditText comments;
    @BindString(R.string.product_cost)
    String productCost;
    @BindString(R.string.cost_total)
    String costTotal;
    @BindView(R.id.escandalloName)
    TextView escandalloName;

    @OnClick(R.id.fab)
    public void showDialogProduct(View v) {
        if (this.ID != 0) {
            dialogNewProduct.show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.escandalloName)
    public void showDialogName(View v) {
        dialogNewEscandallo.show();
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
    private MaterialDialog dialogNewEscandallo;
    private EditText newName;
    private EditText newComment;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_escandallo_new_edit, container, false);
        ButterKnife.bind(this, v);
        dialogNewProduct = createDialogNewProduct();
        listProducts.setHasFixedSize(true);
        listProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        listProducts.setItemAnimator(new DefaultItemAnimator());
        listProducts.setAdapter(new EscandalloNewEdit_Fragment.RecyclerAdapter(new ArrayList<Escandallo_Product>()));
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogNewEscandallo = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.new_list_order_dlg, true)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        ((TextView) dialogNewEscandallo.getCustomView().findViewById(R.id.textnewlist)).setText(getString(R.string.menu_new_escandallo));
                        ((LinearLayout) dialogNewEscandallo.getCustomView().findViewById(R.id.providerpanel)).setVisibility(View.GONE);

                        if (ID != 0) {
                            ((EditText) dialogNewEscandallo.getCustomView().findViewById(R.id.editnameorder)).setText(escandalloName.getText().toString());
                            ((EditText) dialogNewEscandallo.getCustomView().findViewById(R.id.editcomment)).setText(comments.getText().toString());
                        }
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.e("ID", String.valueOf(ID));
                        if (!sql.IsOpen()) {
                            sql.open();
                        }
                        newName = dialog.getCustomView().findViewById(R.id.editnameorder);
                        newComment = dialog.getCustomView().findViewById(R.id.editcomment);
                        String newNametext = newName.getText().toString();
                        String newCommenttext = newComment.getText().toString();
                        if (TextUtils.isEmpty(newNametext)) {

                            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                            GlobalUttilities.animateView(getActivity(), newName);

                            return;
                        }

                        Escandallo esc = new Escandallo();
                        esc.setName(newNametext);
                        esc.setComment(newCommenttext);
                        double costtotal = ((RecyclerAdapter) listProducts.getAdapter()).calculatecostetotal();
                        esc.setCostetotal(costtotal);

                        if (ID != 0) {

                            long count = sql.UpdateWithId(esc, ID);

                            if (count > 0) {
                                Toast.makeText(getActivity(), getString(R.string.dlgok_update), Toast.LENGTH_LONG).show();
                            }

                        } else {

                            if (sql.CheckIsDataAlreadyInDBorNot(DBHelper.TABLE_ESCANDALLOS, DBHelper.NAME, newNametext)) {
                                Toast.makeText(getActivity(), getString(R.string.dlgerror_dataexist), Toast.LENGTH_LONG).show();
                                GlobalUttilities.animateView(getActivity(), newName);
                                getActivity().onBackPressed();
                                return;
                            }

                            long ret = sql.InsertObject(esc);

                            if (ret != -1) {
                                ID = (int) ret;
                                Toast.makeText(getActivity(), "Ok, Escandallo guardado con Nombre : " + newNametext, Toast.LENGTH_LONG).show();
                                escandalloName.setText(newNametext);
                                comments.setText(newCommenttext);

                            } else {
                                Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                            }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();
        if (getArguments() != null) {
            this.ID = getArguments().getInt("id");
        } else {

            this.ID = 0;
            dialogNewEscandallo.show();
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        sql.open();

        if (this.ID != 0) {
            displayWithId(this.ID);
        } else {
            dialogNewEscandallo.show();
            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.new_escandallo));
        }

    }


    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_lists, menu);
        menu.findItem(R.id.search).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newelement:
                refresh();
                onResume();

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


    private void refresh() {
        escandalloName.setText(getString(R.string.Escandallo_name));
        ((RecyclerAdapter) listProducts.getAdapter()).clear();
        texttotal.setText(costTotal);
        this.ID = 0;

    }

    private ArrayList<HashMap<String, Object>> generateSpinnerDta() {
        ArrayList<HashMap<String, Object>> lst = new ArrayList<>();
        HashMap<String, Object> mp = new HashMap<>();
        mp.put("Cant", 1000);
        mp.put("Uni", "Gr");
        mp.put("Name", "KG");
        lst.add(mp);
        HashMap<String, Object> mp2 = new HashMap<>();
        mp2.put("Cant", 1000);
        mp2.put("Uni", "Cl");
        mp2.put("Name", "Ltr");
        lst.add(mp2);
        HashMap<String, Object> mp3 = new HashMap<>();
        mp3.put("Cant", 1);
        mp3.put("Uni", "Uni");
        mp3.put("Name", "Uni");
        lst.add(mp3);
        return lst;
    }

    private MaterialDialog createDialogNewProduct() {

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

    private void displayWithId(int id) {

        if (!sql.IsOpen()) {
            sql.open();
        }
        Escandallo esc = (Escandallo) sql.SelectWithId("Escandallo", DBHelper.TABLE_ESCANDALLOS, id);
        if (esc != null) {
            escandalloName.setText(esc.getName());
            texttotal.setText(String.format("%s %s%s", costTotal, esc.getCostetotal(), "€"));
            comments.setText(esc.getComment());
            ArrayList<Escandallo_Product> listpr = (ArrayList<Escandallo_Product>) (Object) sql.getProductListWithListId("Escandallo_product", esc.getId());
            listProducts.setAdapter(new RecyclerAdapter(listpr));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        sql.close();
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

            int count = sql.DeleteWithId(mDataset.get(pos).getId(), DBHelper.TABLE_ESCANDALLOS_PRODUCTS);

            if (count > 0) {
                mDataset.remove(pos);
                Toast.makeText(getActivity(), getString(R.string.productdeleted), Toast.LENGTH_LONG).show();
            }

            notifyItemRemoved(pos);
        }

        public void addItem(Escandallo_Product escPr) {

            if (!sql.IsOpen()) {
                sql.open();
            }

            escPr.setEscandalloid(ID);

            String query = "SELECT * FROM " + DBHelper.TABLE_ESCANDALLOS_PRODUCTS + " WHERE " + DBHelper.NAME + " = '" + escPr.getProductoname() + "' AND " + DBHelper.ESCANDALLO_ID + " = " + escPr.getEscandalloid();
            if (sql.freeQueryExistsorNot(query)) {
                Toast.makeText(getActivity(), getString(R.string.product_exist), Toast.LENGTH_LONG).show();
                return;
            }

            long id = sql.InsertObject(escPr);

            if (id > 0) {
                escPr.setId((int) id);

                mDataset.add(escPr);

                Toast.makeText(getActivity(), "Ok , Product added", Toast.LENGTH_LONG).show();

            }

            notifyDataSetChanged();

        }

        public ArrayList<Escandallo_Product> getProducts() {
            return mDataset;
        }

        public double calculatecostetotal() {

            double coste = 0;
            try {
                for (int i = 0; i < mDataset.size(); i++) {

                    coste = coste + mDataset.get(i).getCoste();
                }
            } catch (Exception e) {
                Log.e("error :", e.getMessage());
            }

            return Double.valueOf(coste);
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
                                    double sum = calculatecostetotal();
                                    texttotal.setText(String.format("%s %s%s", costTotal, String.valueOf(sum), "€"));
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


                }

            }
        }
    }


}
