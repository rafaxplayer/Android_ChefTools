package rafaxplayer.cheftools.Globalclasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import rafaxplayer.cheftools.Globalclasses.models.Escandallo;
import rafaxplayer.cheftools.Globalclasses.models.Escandallo_Product;
import rafaxplayer.cheftools.Globalclasses.models.Menu;
import rafaxplayer.cheftools.Globalclasses.models.Orders;
import rafaxplayer.cheftools.Globalclasses.models.Recipe;
import rafaxplayer.cheftools.Globalclasses.models.Stocks;
import rafaxplayer.cheftools.Globalclasses.models.Supplier;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;

public class GlobalUttilities {

    public static final String PATH_BACKUPS = Environment.getExternalStorageDirectory() + "/ChefTools_Backup/";
    public static final String PATH_IMAGES_RECIPES = Environment.getExternalStorageDirectory() + "/Android/data/rafaxplayer.cheftools/files/Pictures/";
    public static final int SELECT_PICTURE = 1;
    public static final int SELECT_PHOTO = 2;

    public static final int CONTACT_SELECT = 4;
    public static final int PERMISSION_REQUEST = 10001;
    public static final String CALCULATOR_PACKAGE_2 = "com.sec.android.app.popupcalculator";
    public static final String CALCULATOR_CLASS_2 = "com.sec.android.app.popupcalculator.Calculator";
    public static final String CALCULATOR_PACKAGE = "com.android.calculator2";
    public static final String CALCULATOR_CLASS = "com.android.calculator2.Calculator";


    public static Boolean backup(Context con) {
        boolean ret;

        File dbFile = con.getDatabasePath(DBHelper.DATABASE_NAME);

        try {

            FileInputStream fis = new FileInputStream(dbFile);
            String time = getTime();
            String outFileName = PATH_BACKUPS + DBHelper.DATABASE_NAME + "_" + time;

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
                Toast.makeText(con, con.getString(R.string.backup_cretaed), Toast.LENGTH_LONG).show();
                if (backup_images(con, time)) {
                    Toast.makeText(con, con.getString(R.string.backup_images_cretaed), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(con, con.getString(R.string.backup_error), Toast.LENGTH_LONG).show();
                ret = false;
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            Toast.makeText(con, "No existe la BD", Toast.LENGTH_LONG).show();
            ret = false;
        } catch (Exception ex) {

            ex.printStackTrace();
            Toast.makeText(con, con.getString(R.string.backup_error), Toast.LENGTH_LONG).show();
            ret = false;
        }


        return ret;
    }

    private static Boolean backup_images(Context con, String time) {
        boolean ret;
        try {

            copyDirectoryOneLocationToAnotherLocation(new File(PATH_IMAGES_RECIPES), new File(PATH_BACKUPS + "/" + time));
            ret = true;
        } catch (IOException ex) {
            Toast.makeText(con, "Error al copiar directorio", Toast.LENGTH_SHORT).show();
            ret = false;
        }

        return ret;
    }

    private static Boolean restore_backup_images(Context con, String filename) {

        boolean ret;
        String folderImagesPath = PATH_BACKUPS + filename.replace(DBHelper.DATABASE_NAME + "_", "");
        if (new File(folderImagesPath).exists()) {
            try {

                copyDirectoryOneLocationToAnotherLocation(new File(folderImagesPath), new File(PATH_IMAGES_RECIPES));
                ret = true;

            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(con, "Error al copiar directorio", Toast.LENGTH_SHORT).show();
                ret = false;
            }

        } else {
            Toast.makeText(con, "No hay directorio de imagenes", Toast.LENGTH_SHORT).show();
            ret = false;
        }

        return ret;

    }

    public static Boolean backupRestore(Context con, String filename) {
        boolean ret;
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

            restore_backup_images(con, filename);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(con, con.getString(R.string.dialog_backup_restore_error), Toast.LENGTH_LONG).show();
            ret = false;
        } catch (Exception ex) {

            ex.printStackTrace();
            Toast.makeText(con, con.getString(R.string.dialog_backup_restore_error), Toast.LENGTH_LONG).show();
            ret = false;
        }
        return ret;

    }

    public static Uri backup_image_recipe(Context con, Uri imageFile) {

        Uri ret;
        File mFile = new File(uriGetPath(con, imageFile));
        try {

            FileInputStream fis = new FileInputStream(mFile);
            Log.e("exists", String.valueOf(mFile.getName()));
            String outFileName = PATH_IMAGES_RECIPES + "Recipe_" + getTime();
            Log.e("outfile", outFileName);
            FileOutputStream output = new FileOutputStream(outFileName);

            FileChannel inChannel = fis.getChannel();
            FileChannel outChannel = output.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            fis.close();
            output.close();

            File back = new File(outFileName);
            Log.e("outfile exists", String.valueOf(back.exists()));
            if (back.exists()) {
                ret = Uri.fromFile(back);
                Toast.makeText(con, "Ok image saved", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(con, "Error image save", Toast.LENGTH_LONG).show();
                ret = null;

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(con, "No existe la imagen", Toast.LENGTH_LONG).show();
            ret = null;


        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(con, "Error image", Toast.LENGTH_LONG).show();
            ret = null;

        }

        return ret;
    }

    private static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private static String uriGetPath(Context con, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = con.getContentResolver().query(uri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    public static String shareEscandalloText(Context con, Escandallo esc, ArrayList<Escandallo_Product> products) {

        StringBuilder str = new StringBuilder();
        str.append(esc.getName());
        str.append("\n");
        str.append(String.format("%s: %s", con.getString(R.string.date), esc.getFecha()));
        str.append("\n");
        str.append("-=-=-=-=-=-=-=-=-=-=");
        str.append("\n");
        str.append(con.getString(R.string.products));
        str.append("\n");
        str.append("--------------------");
        str.append("\n");

        for (Escandallo_Product pr : products) {
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
        str.append("Shared with ").append(con.getString(R.string.app_name));

        return str.toString();
    }

    public static String shareDataText(Context con, Object obj) {
        StringBuilder str = new StringBuilder();
        if (obj instanceof Recipe) {
            str.append(((Recipe) obj).getName());
            str.append("\n");
            str.append("-=-=-=-=-=-=-=-=-=-=");
            str.append("\n");
            str.append(con.getString(R.string.recipe_ingredients)).append(" :");
            str.append("\n");
            str.append(((Recipe) obj).getIngredients());
            str.append("\n");
            str.append(con.getString(R.string.recipe_elaboration)).append(" :");
            str.append(((Recipe) obj).getElaboration());
            str.append("\n");
            if (!((Recipe) obj).getUrl().isEmpty()) {

                str.append(con.getString(R.string.recipe_url)).append(" :");
                str.append(((Recipe) obj).getUrl());
            }
            str.append("\n");
        } else if (obj instanceof Menu) {
            str.append(((Menu) obj).getName());
            str.append("\n");
            str.append("-=-=-=-=-=-=-=-=-=-=");
            str.append("\n");
            str.append(con.getString(R.string.menu_entrantes)).append(" :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getEntrantes());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.menu_primeros)).append(" :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getPrimeros());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.menu_segundos)).append(" :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getSegundos());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.menu_postres)).append(" :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Menu) obj).getPostre());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.comments)).append(" :");
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
            str.append(con.getString(R.string.phone)).append(" :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Supplier) obj).getTelefono());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.address)).append(" :");
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            str.append(((Supplier) obj).getDireccion());
            str.append("\n");
            str.append("\n");
            str.append(con.getString(R.string.provider_category)).append(" :");
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
            str.append(con.getString(R.string.comments)).append(" :");
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
            str.append(con.getString(R.string.order_list));
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
            str.append(con.getString(R.string.comments)).append(" :");
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
            str.append(con.getString(R.string.stock_lis));
            str.append("\n");
            str.append("--------------------");
            str.append("\n");
            SqliteWrapper sql = new SqliteWrapper(con);
            sql.open();

            String products = sql.getProductListToString(((Stocks) obj).getId(), DBHelper.TABLE_INVENTARIOS_LISTAS, DBHelper.INVENTARIO_ID);

            str.append(products);


            str.append("\n");

        }

        str.append("Shared with ").append(con.getString(R.string.app_name));

        return str.toString();
    }

    public static void shareIntenttext(Activity act, String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);

        act.startActivity(Intent.createChooser(shareIntent, act.getString(R.string.share_recipe_use)));
    }

    private static String getTime() {
        return String.valueOf(new Date().getTime());
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss", Locale.getDefault());
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

            if (arr.get(i).get("Name").equals(str)) {

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

        StringBuilder rFinal = new StringBuilder();
        boolean after = false;
        int i = 0, up = 0, decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);
            if (t != '.' && after == false) {
                up++;
                if (up > MAX_BEFORE_POINT) return rFinal.toString();
            } else if (t == '.') {
                after = true;
            } else {
                decimal++;
                if (decimal > MAX_DECIMAL)
                    return rFinal.toString();
            }
            rFinal.append(t);
            i++;
        }
        return rFinal.toString();
    }

    public static String FormatDecimal(double decimal) {

        return String.format(Locale.CANADA, "%.2f", decimal);
    }

    public static boolean checkPermission(Context con, String permission) {

        int result = con.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;

    }

    public static Uri getBmpUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
