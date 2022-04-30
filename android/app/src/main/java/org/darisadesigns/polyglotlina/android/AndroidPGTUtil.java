package org.darisadesigns.polyglotlina.android;

import android.content.Context;
import android.content.res.Configuration;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;

import org.darisadesigns.polyglotlina.PGTUtil;

public class AndroidPGTUtil extends PGTUtil {

    public boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_YES) != 0;
    }

    public void fixOptionsMenuTextColor(Menu menu, Context context) {
        /* Hack to change text color, otherwise it stays white on white */
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(item.getTitle());
            int color = isNightMode(context) ?
                    R.color.design_default_color_on_primary : R.color.design_default_color_on_secondary;
            s.setSpan(new ForegroundColorSpan(context.getColor(color)), 0, s.length(), 0);
            item.setTitle(s);
        }
    }

    public void fixAutocompleteBackground(SearchView searchView, Context context) {
        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        if (autoCompleteTextView != null && !isNightMode(context)) {
            autoCompleteTextView.setDropDownBackgroundDrawable(context.getDrawable(R.drawable.abc_popup_background_mtrl_mult));
        }
    }
}
