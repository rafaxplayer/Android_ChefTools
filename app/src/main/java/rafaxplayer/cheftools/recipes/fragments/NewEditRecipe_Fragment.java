package rafaxplayer.cheftools.recipes.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.IconizedMenu;
import rafaxplayer.cheftools.Globalclasses.Recipe;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.recipes.NewEditRecipe_Activity;


public class NewEditRecipe_Fragment extends Fragment {
    @BindView(R.id.categorys)
    Spinner cats;
    @BindView(R.id.addimage)
    Button addImage;
    @BindView(R.id.imgRecipe)
    ImageView img;
    @BindView(R.id.editname)
    EditText nametxt;
    @BindView(R.id.buttonSave)
    Button save;
    @BindView(R.id.editurl)
    EditText url;
    @BindView(R.id.editingredients)
    EditText ingtxt;
    @BindView(R.id.editelaboracion)
    EditText elatxt;
    @BindView(R.id.scrollView)
    ScrollView scroll;
    @BindView(R.id.imageButtonSearch)
    ImageButton search;

    private IconizedMenu popup;
    private String urlImage;
    private Uri imgUri;
    private int ID;
    private Boolean edit;
    private SqliteWrapper sql;
    private SimpleCursorAdapter genreSpinnerAdapter;
    private ArrayList<String> my_array;

    private ArrayList<HashMap<String, Object>> catsarr;


    public static NewEditRecipe_Fragment newInstance(int recipeid) {
        NewEditRecipe_Fragment f = new NewEditRecipe_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", recipeid);

        f.setArguments(args);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_new_edit, container, false);
        if (v != null) {
            ButterKnife.bind(this, v);

        }
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();

        this.imgUri = null;
        this.edit = false;
        if (getArguments() != null) {
            this.ID = getArguments().getInt("id");

        }
        this.setRetainInstance(true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewEditRecipe_Activity) getActivity()).showWebfRm();

            }
        });
        popup = new IconizedMenu(getActivity(), addImage);
        popup.getMenuInflater()
                .inflate(R.menu.menu_image_recipes, popup.getMenu());
        popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
                                             @Override
                                             public boolean onMenuItemClick(MenuItem item) {
                                                 switch (item.getItemId()) {
                                                     case R.id.action_gallery:
                                                         Intent intent = new Intent();
                                                         intent.setAction(Intent.ACTION_PICK);
                                                         intent.setType("image/*");
                                                         getActivity().startActivityForResult(Intent.createChooser(intent,
                                                                 getString(R.string.selectpicture)), GlobalUttilities.SELECT_PICTURE);
                                                         break;
                                                     case R.id.action_photo:
                                                         Intent inte = new Intent(
                                                                 android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                                         getActivity().startActivityForResult(inte, GlobalUttilities.CAPTURE_ID);
                                                         break;
                                                     case R.id.action_url:


                                                         new MaterialDialog.Builder(getActivity())

                                                                 .inputType(InputType.TYPE_CLASS_TEXT)
                                                                 .input(getString(R.string.urlimage), "", new MaterialDialog.InputCallback() {
                                                                     @Override
                                                                     public void onInput(MaterialDialog dialog, CharSequence input) {
                                                                         setImage(Uri.parse(input.toString()));
                                                                         //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                                                         dialog.dismiss();
                                                                     }

                                                                 }).show();
                                                         break;
                                                     default:
                                                         break;
                                                 }
                                                 return false;
                                             }
                                         }

        );

        addImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popup.show();

                                        }
                                    }

        );


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sql.IsOpen()) {
            sql.open();
        }
        catsarr = sql.getFormatsOrCategorysData(DBHelper.TABLE_RECETAS_CATEGORIA);
        SimpleAdapter my_Adapter = new SimpleAdapter(getActivity(), catsarr, R.layout.spinnerrow, new String[]{"Name"}, new int[]{R.id.spinnertext});
        cats.setAdapter(my_Adapter);

        if (this.ID != 0) {
            displayWithId(this.ID);
        } else {
            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.menu_new_recipe));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_save, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            save();
            return true;
        }
        return onOptionsItemSelected(item);
    }

    private void save() {

        if (!sql.IsOpen()) {
            sql.open();
        }

        String cat = ((HashMap<String, Object>) cats.getSelectedItem()).get("Name").toString();

        Recipe rec = new Recipe(nametxt.getText().toString(),
                imgUri == null ? "null" : imgUri.toString(),
                ingtxt.getText().toString(),
                elatxt.getText().toString(),
                url.getText().toString(),
                cat
        );

        if (this.ID == 0) {
            if (TextUtils.isEmpty(nametxt.getText())) {

                Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();

                return;
            }
            if (sql.CheckIsDataAlreadyInDBorNot(DBHelper.TABLE_RECETAS, DBHelper.NAME, nametxt.getText().toString())) {

                Toast.makeText(getActivity(), getString(R.string.dlgerror_dataexist), Toast.LENGTH_LONG).show();

                return;
            }
            long id = sql.InsertObject(rec);
            if (id != -1) {

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.dlgsucces_saved)
                        .content(R.string.dlgnew_saved)
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

            long count = sql.UpdateWithId(rec, this.ID);
            if (count > 0) {

                Toast.makeText(getActivity(), getString(R.string.dlgok_update), Toast.LENGTH_LONG).show();
                if (((DetalleRecipes_Fragment) getActivity().getSupportFragmentManager().findFragmentByTag("detalle")) != null) {
                    ((DetalleRecipes_Fragment) getActivity().getSupportFragmentManager().findFragmentByTag("detalle")).onResume();
                }
                getActivity().onBackPressed();
            }

        }

        sql.close();

    }

    public void displayWithId(int id) {
        if (!sql.IsOpen()) {
            sql.open();
        }
        Recipe rec = (Recipe) sql.SelectWithId("Recipe", DBHelper.TABLE_RECETAS, id);
        if (rec != null) {
            Picasso.with(getActivity()).load(Uri.parse(rec.getImg().toString()))
                    .resize(getResources().getDimensionPixelOffset(R.dimen.image_dimen_width), getResources().getDimensionPixelOffset(R.dimen.image_dimen_height))
                    .placeholder(R.drawable.item_image_placeholder)
                    .into(img);

            nametxt.setText(rec.getName());

            cats.setSelection(GlobalUttilities.SpinnergetIndex(catsarr, rec.getCategoty()));
            url.setText(rec.getUrl());
            ingtxt.setText(rec.getIngredients());
            elatxt.setText(rec.getElaboration());


            this.ID = id;
            this.imgUri = Uri.parse(rec.getImg().toString());
            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.menu_edit_recipe) + " " + rec.getName());
        }
        sql.close();
    }

    public void setImage(Uri ur) {
        this.imgUri = ur;
        Log.e("Imaguri :", ur.toString());

        Picasso.with(getActivity())
                .load(ur)
                .resize(getResources().getDimensionPixelOffset(R.dimen.image_dimen_width), getResources().getDimensionPixelOffset(R.dimen.image_dimen_height))
                .placeholder(R.drawable.placeholder_recetas)
                .noFade()
                .into(img);

    }

    public void refresh() {
        Picasso.with(getActivity())
                .load(Uri.parse(""))
                .placeholder(R.drawable.item_image_placeholder)
                .noFade()
                .into(this.img);
        imgUri = null;
        nametxt.setText("");
        cats.setSelection(0);
        url.setText("");
        ingtxt.setText("");
        elatxt.setText("");

        this.ID = 0;

    }

    public void setUrl(String url) {

        this.url.setText(url);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
        ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.activity_newedit));
    }

    @Override
    public void onPause() {
        super.onPause();
        sql.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.imgUri = null;
    }
}
