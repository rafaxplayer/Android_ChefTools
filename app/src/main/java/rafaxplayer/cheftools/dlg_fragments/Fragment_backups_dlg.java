package rafaxplayer.cheftools.dlg_fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.cocosw.bottomsheet.BottomSheet;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.SqliteWrapper;


public class Fragment_backups_dlg extends DialogFragment {
    @BindView(R.id.list_items)
    RecyclerView listItems;
    @BindView(R.id.texttitle)
    TextView texttile;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private SqliteWrapper sql;

    @OnClick(R.id.logo)
    public void back() {
        getActivity().onBackPressed();
    }

    public Fragment_backups_dlg() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_backups_dlg, container, false);
        ButterKnife.bind(this, v);
        texttile.setTextColor(getActivity().getResources().getColor(R.color.textTitletColor));

        listItems.setHasFixedSize(true);
        listItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        listItems.setItemAnimator(new DefaultItemAnimator());
        sql = new SqliteWrapper(getActivity());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.backups)
                        .content(R.string.dlg_backup_question)
                        .positiveText("Ok")
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (GlobalUttilities.checkPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) && GlobalUttilities.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    sql.close();
                                    if (GlobalUttilities.backup(getActivity())) {
                                        new MaterialDialog.Builder(getActivity()).title(R.string.backups)
                                                .content(R.string.backup_path)
                                                .positiveText("Ok").show();

                                    }

                                    onResume();
                                } else {
                                    Toast.makeText(getActivity(), R.string.white_permission, Toast.LENGTH_SHORT).show();
                                }

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
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
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
                if (!files[i].isDirectory()) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", files[i].getName());
                    map.put("path", files[i].getAbsolutePath());
                    String date = files[i].getName().replace("ChefToolsDB_", "");
                    map.put("date", date.contains(":") ? date : new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date(Long.valueOf(date))));
                    listBackups.add(map);
                    if ((i + 1) >= maxBackups) {
                        files[i].delete();
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return listBackups;
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private final ArrayList<HashMap<String, String>> mDataset;

        RecyclerAdapter(ArrayList<HashMap<String, String>> myDataset) {
            mDataset = myDataset;
        }

        void deleteItem(int pos) {

            File fileBackup = new File(mDataset.get(pos).get("path"));
            File folderImagesBackup = new File(mDataset.get(pos).get("path").replace("ChefToolsDB_", ""));

            if (fileBackup.exists()) {
                boolean deleted = fileBackup.delete();
                if (deleted) {
                    mDataset.remove(pos);
                }
                notifyItemRemoved(pos);
            }

            if (folderImagesBackup.exists()) {
                boolean deletedFol = GlobalUttilities.deleteDir(folderImagesBackup);
                if (deletedFol) {
                    Toast.makeText(getActivity(), getString(R.string.backup_images_deleted), Toast.LENGTH_SHORT).show();
                }

            }

        }

        @NonNull
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_backups, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mTextView.setText(R.string.backups);
            holder.mTextView2.setText(getString(R.string.created) + mDataset.get(position).get("date"));
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            final TextView mTextView;
            final TextView mTextView2;

            ViewHolder(View v) {
                super(v);

                mTextView = v.findViewById(R.id.text1);
                mTextView2 = v.findViewById(R.id.text2);
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
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                GlobalUttilities.backupRestore(getActivity(), mDataset.get(ViewHolder.this.getLayoutPosition()).get("name"));
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

                                break;
                            case R.id.action_delete:
                                new MaterialDialog.Builder(getActivity())
                                        .title(R.string.menu_delete_backup)
                                        .content(R.string.delete_backup_question)
                                        .theme(Theme.LIGHT)
                                        .positiveText("Ok")
                                        .negativeText(R.string.cancel)
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

                                break;
                        }
                    }
                }).show();
            }
        }
    }
}
