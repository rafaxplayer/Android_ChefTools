package rafaxplayer.cheftools.Globalclasses;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;

public class GlobalUttilities {

    public static final String PATH_BACKUPS = Environment.getExternalStorageDirectory() + "/ChefTools_Backup/";
    public static final int SELECT_PICTURE = 1;
    public static final int CAPTURE_ID = 2;
    public static final int RECIPE_WITH_CAPTURE = 3;
    public static final int WEBURL_MODE_SEARCHIMG = 1;
    public static final int WEBURL_MODE_SEARCHRECIPE = 2;
    public static final String CALCULATOR_PACKAGE_2 = "com.sec.android.app.popupcalculator";
    public static final String CALCULATOR_CLASS_2 = "com.sec.android.app.popupcalculator.Calculator";
    public static final String CALCULATOR_PACKAGE = "com.android.calculator2";
    public static final String CALCULATOR_CLASS = "com.android.calculator2.Calculator";

    public static Boolean backup(Context con) {
        boolean ret;

        File dbFile = con.getDatabasePath(DBHelper.DATABASE_NAME);

        try {

            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = PATH_BACKUPS + DBHelper.DATABASE_NAME + "_" + GlobalUttilities.getDateTime();

            OutputStream output = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
            File back = new File(outFileName);
            if (back.exists()) {
                ret = true;
                Toast.makeText(con, "Ok Backup Created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(con, "Error Backup", Toast.LENGTH_LONG).show();
                ret = false;
            }
        } catch (FileNotFoundException e) {

            Toast.makeText(con, "No existe la BD", Toast.LENGTH_LONG).show();
            ret = false;
        } catch (Exception ex) {


            Toast.makeText(con, "Error Backup", Toast.LENGTH_LONG).show();
            ret = false;
        }


        return ret;
    }

    public static Boolean backupRestore(Context con, String filename) {
        Boolean ret;
        try {

            File dbFile = new File(PATH_BACKUPS + filename);

            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = "/data/data/rafaxplayer.cheftools/databases/" + DBHelper.DATABASE_NAME;

            OutputStream output = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
            ret = true;
            Toast.makeText(con, con.getString(R.string.dialog_backup_restore_ok), Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e) {

            Toast.makeText(con, con.getString(R.string.dialog_backup_restore_error), Toast.LENGTH_LONG).show();
            ret = false;
        } catch (Exception ex) {


            Toast.makeText(con, con.getString(R.string.dialog_backup_restore_error), Toast.LENGTH_LONG).show();
            ret = false;
        }
        return ret;

    }

    public static String shareEscandalloText(Context con, Escandallo esc) {
        StringBuilder str = new StringBuilder();
        str.append(esc.getName());
        str.append("\n");
        str.append(String.format("%s: %s", con.getString(R.string.date), esc.getDate()));
        str.append("\n");
        str.append("-=-=-=-=-=-=-=-=-=-=");
        str.append("\n");
        str.append(con.getString(R.string.products));
        str.append("\n");
        str.append("--------------------");
        str.append("\n");

        for (Escandallo_Product pr : esc.getProducts()) {
            str.append(pr.getProductoname());
            str.append("\n");
            str.append(String.format("%s: %s%s", con.getString(R.string.product_uni), pr.getCantidad(), pr.getFormato()));
            str.append("\n");
            str.append(String.format("%s %s€", con.getString(R.string.product_cost2), String.valueOf(pr.getCoste())));
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
        }

        str.append(String.format("%s %s€", con.getString(R.string.cost_total), esc.getCostetotal()));
        str.append("\n");
        str.append("\n");
        str.append("Shared with " + con.getString(R.string.app_name));

        return str.toString();
    }

    public static String shareDataText(Context con, Object obj) {
        StringBuilder str = new StringBuilder();
        if (obj instanceof Recipe) {
            str.append(((Recipe) obj).getName());
            str.append("\n");
            str.append("-=-=-=-=-=-=-=-=-=-=");
            str.append("\n");
            str.append(con.getString(R.string.recipe_ingredients) + " :");
            str.append("\n");
            str.append(((Recipe) obj).getIngredients());
            str.append("\n");
            str.append(con.getString(R.string.recipe_elaboration) + " :");
            str.append(((Recipe) obj).getElaboration());
            str.append("\n");
            if (!((Recipe) obj).getUrl().isEmpty()) {

                str.append(con.getString(R.string.recipe_url) + " :");
                str.append(((Recipe) obj).getUrl());
            }
            str.append("\n");
        } else if (obj instanceof Menu) {
            str.append(((Menu) obj).getName());
            str.append("\n");
            str.append("-=-=-=-=-=-=-=-=-=-=");
            str.append("\n");
            str.append(con.getString(R.string.menu_entrantes) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getEntrantes());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.menu_primeros) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getPrimeros());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.menu_segundos) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getSegundos());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.menu_postres) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getPostre());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.comments) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getComentario());
            str.append("\n");
            str.append("\n");
        } else if (obj instanceof Supplier) {
            str.append(((Supplier) obj).getName());
            str.append("\n");
            str.append("-=-=-=-=-=-=-=-=-=-=");
            str.append("\n");
            str.append(con.getString(R.string.phone) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Supplier) obj).getTelefono());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.address) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Supplier) obj).getDireccion());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.provider_category) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Supplier) obj).getCategoria());
            str.append("\n");
            str.append("\n");
            str.append("\n");

        } else if (obj instanceof Orders) {
            str.append(((Orders) obj).getName());
            str.append("\n");
            str.append("-=-=-=-=-=-=-=-=-=-=");
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.comments) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Orders) obj).getComentario());
            str.append("\n");
            str.append("\n");
            str.append("\n");
            str.append(((Orders) obj).getFecha());
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.order_list) + "");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            SqliteWrapper sql = new SqliteWrapper(con);
            sql.open();

            String products = sql.getProductListToString(((Orders) obj).getId(), DBHelper.TABLE_PEDIDOS_LISTAS, DBHelper.PEDIDO_ID);
            //Log.e("tostring",products);
            str.append(products);

            str.append("\n");

        } else if (obj instanceof Stocks) {
            str.append(((Stocks) obj).getName());
            str.append("\n");
            str.append("-=-=-=-=-=-=-=-=-=-=");
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.comments) + " :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Stocks) obj).getComentario());
            str.append("\n");
            str.append("\n");
            str.append("\n");
            str.append(((Stocks) obj).getFecha());
            str.append("\n");
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.stock_lis) + "");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            SqliteWrapper sql = new SqliteWrapper(con);
            sql.open();

            String products = sql.getProductListToString(((Stocks) obj).getId(), DBHelper.TABLE_INVENTARIOS_LISTAS, DBHelper.INVENTARIO_ID);

            str.append(products);


            str.append("\n");

        }

        str.append("Shared with " + con.getString(R.string.app_name));

        return str.toString();
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void setDialogFragmentSize(DialogFragment dlg) {
        Rect displayRectangle = new Rect();
        Window window = dlg.getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        int dialogWidth = (int) (displayRectangle.width() * 0.99f);
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        dlg.getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    public static void call(Context con, String telf) {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telf));
        con.startActivity(intent);
    }

    public static void sendEmail(Context con, String emailAddres) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddres});

        con.startActivity(intent);
    }

    public static int SpinnergetIndex(ArrayList<HashMap<String, Object>> arr, String str) {

        for (int i = 0; i < arr.size(); i++) {
            //Log.e("pruevaindex", String.valueOf(((HashMap<String, Object>) arr.get(i)).get("Name")));
            if (((HashMap<String, Object>) arr.get(i)).get("Name").equals(str)) {

                return i;
            }
        }
        return 0;

    }

    public static void ocultateclado(Context con, EditText ed) {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
    }

    public static void animateView(Context con, View v) {
        Animation animAlpha = AnimationUtils.loadAnimation(con, R.anim.anim_fadeout);
        animAlpha.setRepeatMode(Animation.REVERSE);
        v.startAnimation(animAlpha);
    }

    public static Typeface getfont(Context con, String font) {
        return Typeface.createFromAsset(con.getAssets(), "fonts/" + font);
    }

    public static boolean isScreenLarge(Context con) {
        final int screenSize = con.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL) {
        if (str.charAt(0) == '.') str = "0" + str;
        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0, up = 0, decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);
            if (t != '.' && after == false) {
                up++;
                if (up > MAX_BEFORE_POINT) return rFinal;
            } else if (t == '.') {
                after = true;
            } else {
                decimal++;
                if (decimal > MAX_DECIMAL)
                    return rFinal;
            }
            rFinal = rFinal + t;
            i++;
        }
        return rFinal;
    }
}
