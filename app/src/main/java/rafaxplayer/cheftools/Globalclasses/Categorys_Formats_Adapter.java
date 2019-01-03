package rafaxplayer.cheftools.Globalclasses;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.cocosw.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.HashMap;

import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;

/**
 * Created by rafaxplayer on 06/07/2015.
 */
public class Categorys_Formats_Adapter extends RecyclerView.Adapter<Categorys_Formats_Adapter.ViewHolder> {

    private SqliteWrapper sql;
    private ArrayList<HashMap<String, Object>> mDataset;
    private String Table;
    private Context con;
    private Activity act;

    public Categorys_Formats_Adapter(Activity con, ArrayList<HashMap<String,Object>> myDataset, String sTable, SqliteWrapper sqlite) {
        this.mDataset = myDataset;
        this.mDataset.remove(0);
        this.Table = sTable;
        this.con = con;
        this.act = con;
        sql = sqlite;

    }

    @Override
    public Categorys_Formats_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_simple, parent, false);

        Categorys_Formats_Adapter.ViewHolder vh = new Categorys_Formats_Adapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(Categorys_Formats_Adapter.ViewHolder viewHolder, int i) {
        viewHolder.imglist.setImageResource(R.drawable.config);
        viewHolder.mTextView.setText((String) mDataset.get(i).get("Name"));
        viewHolder.mTextView2.setText(String.valueOf(mDataset.get(i).get("ID")));
        viewHolder.mTextView2.setVisibility(View.GONE);
        viewHolder.mTextView3.setVisibility(View.GONE);
        viewHolder.delete.setVisibility(View.GONE);
        viewHolder.edit.setVisibility(View.GONE);
    }

    public void additem(String Name) {

        if (!sql.CheckIsDataAlreadyInDBorNot(Table, DBHelper.NAME, Name)) {
            long id = sql.InsertSimpleData(Table, Name);
            if (id > 0) {
                Toast.makeText(con, con.getString(R.string.dlgsucces_saved), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(con, con.getString(R.string.dlgerror_dataexist), Toast.LENGTH_LONG).show();
        }


    }

    public void deleteItem(int pos) {
        if (!sql.IsOpen()) {
            sql.open();
        }

        int count = sql.DeleteWithId((int) mDataset.get(pos).get("ID"), Table);

        if (count > 0) {
            mDataset.remove(pos);
            Toast.makeText(con, "Ok , item deleted", Toast.LENGTH_LONG).show();
        }

        notifyItemRemoved(pos);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public TextView mTextView2;
        public TextView mTextView3;
        public ImageView imglist;
        public ImageButton edit;
        public ImageButton delete;

        public ViewHolder(View v) {
            super(v);
            imglist = v.findViewById(R.id.imageList);
            mTextView = v.findViewById(R.id.text1);
            mTextView2 = v.findViewById(R.id.text2);
            mTextView3 = v.findViewById(R.id.text3);
            delete = v.findViewById(R.id.ButtonDeleteProduct);
            edit = v.findViewById(R.id.ButtonEditProduct);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            new BottomSheet.Builder(act).title(con.getString(R.string.menu_action_items)).sheet(R.menu.menu_action_categorys_formats).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case R.id.action_delete:
                            new MaterialDialog.Builder(con)
                                    .title(R.string.menu_delete_item)
                                    .content(con.getString(R.string.deleteitemmsg).replace("###", mDataset.get(ViewHolder.this.getLayoutPosition()).get("Name").toString()))
                                    .theme(Theme.LIGHT)
                                    .positiveText("Ok")
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

                            break;
                    }
                }
            }).show();

        }
    }

}
