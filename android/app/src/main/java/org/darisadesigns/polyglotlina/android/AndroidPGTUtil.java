package org.darisadesigns.polyglotlina.android;

import android.content.Context;
import android.content.res.Configuration;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import org.darisadesigns.polyglotlina.PGTUtil;

public class AndroidPGTUtil extends PGTUtil {

    public void fixOptionsMenuTextColor(Menu menu, Context context) {
        /* Hack to change text color, otherwise it stays white on white */
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(item.getTitle());
            int color = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_YES) == 0 ?
                    R.color.design_default_color_on_secondary : R.color.design_default_color_on_primary;
            s.setSpan(new ForegroundColorSpan(context.getColor(color)), 0, s.length(), 0);
            item.setTitle(s);
        }
    }
}
