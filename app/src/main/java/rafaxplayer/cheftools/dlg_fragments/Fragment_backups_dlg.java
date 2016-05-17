package rafaxplayer.cheftools.dlg_fragments;


import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.cocosw.bottomsheet.BottomSheet;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.SqliteWrapper;


public class Fragment_backups_dlg extends DialogFragment {
    private RecyclerView listItems;
    private LinearLayout empty;
    private TextView emptytxt;
    private TextView texttile;
    private ActionMode mActionMode;
    private FloatingActionButton fab;
    private SqliteWrapper sql;
    private Boolean itemsFound;

    public Fragment_backups_dlg() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_backups_dlg, container, false);
        texttile = (TextView) v.findViewById(R.id.texttitle);

        texttile.setTextColor(getActivity().getResources().getColor(R.color.textTitletColor));
        listItems = (RecyclerView) v.findViewById(R.id.list_items);
        listItems.setHasFixedSize(true);
        listItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        listItems.setItemAnimator(new DefaultItemAnimator());
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        fab.attachToRecyclerView(listItems, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {

            }

            @Override
            public void onScrollUp() {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.backups)
                        .content(R.string.dlg_backup_question)
                        .positiveText("Ok")
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                GlobalUttilities.backup(getActivity());
                                onResume();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        })


                        .show();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        texttile.setText(R.string.backups);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;
        GlobalUttilities.setDialogFragmentSize(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!GlobalUttilities.isScreenLarge(getActivity())) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<HashMap<String, String>> backList = loadbackups();
        listItems.setAdapter(new RecyclerAdapter(backList));
    }

    private ArrayList<HashMap<String, String>> loadbackups() {
        int maxBackups = 10;
        ArrayList<HashMap<String, String>> listBackups = new ArrayList<>();
        try {
            File f = new File(GlobalUttilities.PATH_BACKUPS);
            File[] files = f.listFiles();
            Collections.sort(Arrays.asList(files), Collections.reverseOrder());
            for (int i = 0; i < files.length; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                Date lastModDate = new Date(files[i].lastModified());
                map.put("name", files[i].getName().toString());
                map.put("path", files[i].getAbsolutePath());
                map.put("date", files[i].getName().toString().replace("ChefToolsDB_", ""));
                listBackups.add(map);
                if ((i + 1) >= maxBackups) {
                    files[i].delete();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return listBackups;
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private ArrayList<HashMap<String, String>> mDataset;

        public RecyclerAdapter(ArrayList<HashMap<String, String>> myDataset) {
            mDataset = myDataset;
        }

        public void deleteItem(int pos) {
            File file = new File(mDataset.get(pos).get("path"));
            boolean deleted = file.delete();
            if (deleted) {
                mDataset.remove(pos);
            }
            notifyItemRemoved(pos);
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_backups, parent, false);
            RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(R.string.backups);
            holder.mTextView2.setText(getString(R.string.created) + mDataset.get(position).get("date"));
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public ImageView img;
            public TextView mTextView;
            public TextView mTextView2;

            public ViewHolder(View v) {
                super(v);
                img = (ImageView) v.findViewById(R.id.imageList);
                mTextView = (TextView) v.findViewById(R.id.text1);
                mTextView2 = (TextView) v.findViewById(R.id.text2);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(getActivity()).title(getString(R.string.actions_backup)).sheet(R.menu.menu_action_backups).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.action_restore:
                                new MaterialDialog.Builder(getActivity())
                                        .title(R.string.menu_restore_backup)
                                        .content(R.string.restore_backups_summary)
                                        .theme(Theme.LIGHT)
                                        .positiveText("Ok")
                                        .negativeText(R.string.cancel)
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                GlobalUttilities.backupRestore(getActivity(), mDataset.get(ViewHolder.this.getLayoutPosition()).get("name"));
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();

                                break;
                            case R.id.action_delete:
                                new MaterialDialog.Builder(getActivity())
                                        .title(R.string.menu_delete_backup)
                                        .content(R.string.delete_backup_question)
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
}
