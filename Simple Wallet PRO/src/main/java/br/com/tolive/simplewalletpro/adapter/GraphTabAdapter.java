package br.com.tolive.simplewalletpro.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import br.com.tolive.simplewalletpro.app.GraphFragment;
import br.com.tolive.simplewalletpro.app.GraphFragmentTab;
import br.com.tolive.simplewalletpro.model.Category;

/**
 * Created by Bruno on 14/08/2014.
 */
public class GraphTabAdapter extends FragmentStatePagerAdapter {

    public static final String BUNDLE_KEY_TAB_TYPE = "type";
    public static final String BUNDLE_KEY_TAB_MONTH = "month";

    private List<Integer> mTypes;
    private int month;

    public GraphTabAdapter(FragmentManager fm, List<Integer> types, int month) {
        super(fm);
        this.mTypes = types;
        this.month = month;
    }

    @Override
    public Fragment getItem(int pos) {
        Fragment graphTab = new GraphFragmentTab();
        Bundle type = new Bundle();
        type.putInt(BUNDLE_KEY_TAB_TYPE, mTypes.get(pos));
        type.putInt(BUNDLE_KEY_TAB_MONTH, month);
        graphTab.setArguments(type);
        return graphTab;
    }

    @Override
    public int getCount() {
        return mTypes.size();
    }
}
