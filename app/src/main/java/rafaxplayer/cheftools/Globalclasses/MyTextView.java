package rafaxplayer.cheftools.Globalclasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context con) {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(con.getAssets(),
                    "fonts/Days.ttf");
            this.setTypeface(tf);
        }

    }

}