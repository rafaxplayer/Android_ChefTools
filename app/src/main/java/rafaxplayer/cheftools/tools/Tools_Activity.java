package rafaxplayer.cheftools.tools;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Toast;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.dlg_fragments.Format_Categories_Formats_dlgs;
import rafaxplayer.cheftools.dlg_fragments.Fragment_backups_dlg;


public class Tools_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_tools;
    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_tools);
    }

    public void menuClick(View v) {
        switch (v.getId()) {
            case R.id.calculator:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                try {

                    intent.setComponent(new ComponentName(
                        GlobalUttilities.CALCULATOR_PACKAGE,
                        GlobalUttilities.CALCULATOR_CLASS));

                    this.startActivity(intent);
                } catch (ActivityNotFoundException noSuchActivity){
                    intent.setComponent(new ComponentName(
                            GlobalUttilities.CALCULATOR_PACKAGE_2,
                            GlobalUttilities.CALCULATOR_CLASS_2));

                    this.startActivity(intent);
                    //
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Tu dispositivo no tiene calculadora de android estandar", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.calendar:
                Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    i.setData(Uri.parse("content://com.android.calendar/time"));
                    startActivity(i);
                }catch (Exception ex){
                    i.setData(Uri.parse("content://calendar/time"));
                    startActivity(i);
                }

                break;

            case R.id.internet:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")));
                break;
            case R.id.contacts:
                Intent myIntent=new Intent();
                myIntent.setAction(Intent.ACTION_VIEW);
                myIntent.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivity(myIntent);
                break;
            default:
                break;
        }

    }

}
