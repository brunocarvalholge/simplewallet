package br.com.tolive.simplewallet.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import br.com.tolive.simplewallet.adapter.NavDrawerListAdapter;
import br.com.tolive.simplewallet.constants.Constantes;
import br.com.tolive.simplewallet.model.Entry;
import br.com.tolive.simplewallet.model.NavDrawerItem;


public class MenuActivity extends ActionBarActivity {
    private static final int ICON_SETTINGS = 0;
    private static final int ICON_FILTRO = 1;
    private static final int ICON_NONE = 2;
    private static final int REQUEST_SETTINGS = 0;
    private static final int REQUEST_FILTRO = 1;
    //private static final int NAV_ADD = 0;
    private static final int NAV_LIST = 0;
    public static final int NAV_STORE = 1;
    public static final int NAV_RECOVERY = 2;
    public static final int NAV_SETTINGS = 3;
    private static final int NAV_ABOUT = 4;
    private static final int DEFAULT_VALUE = -1;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
//    private ActionBarDrawerToggle mDrawerToggle;

    //private ActionBar mActionBar;
    int actionBarIcon = ICON_SETTINGS;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private OnFiltroApplyListener mListener;

    private AlertDialog promoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayPromo();

        SharedPreferences sharedPreferences = getSharedPreferences(Constantes.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putBoolean(Constantes.SP_KEY_REMOVE_AD, true);
        //editor.commit();
        boolean removeAd = sharedPreferences.getBoolean(Constantes.SP_KEY_REMOVE_AD, Constantes.SP_REMOVE_AD_DEFAULT);
        if(!removeAd) {
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice("E6E54B90007CAC7A62F9EC7857F3A989")
                    .build();
            AdView adView = (AdView) findViewById(R.id.ad_main);
            adView.loadAd(request);
        } else{
            AdView adView = (AdView) findViewById(R.id.ad_main);
            adView.setVisibility(View.GONE);
        }

        setActionBarIcon();

        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        // adding nav drawer items to array
        // add
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAV_ADD], navMenuIcons.getResourceId(NAV_ADD, DEFAULT_VALUE)));
        // list
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAV_LIST], navMenuIcons.getResourceId(NAV_LIST, DEFAULT_VALUE)));
        // store
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAV_STORE], navMenuIcons.getResourceId(NAV_STORE, DEFAULT_VALUE)));
        // recovery
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAV_RECOVERY], navMenuIcons.getResourceId(NAV_RECOVERY, DEFAULT_VALUE)));
        // recovery
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAV_SETTINGS], navMenuIcons.getResourceId(NAV_SETTINGS, DEFAULT_VALUE)));
        // about
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAV_ABOUT], navMenuIcons.getResourceId(NAV_ABOUT, DEFAULT_VALUE)));


        // Recycle the typed array
        navMenuIcons.recycle();


        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                R.drawable.ic_drawer, //nav menu toggle icon
//                R.string.app_name, // nav drawer open - description for accessibility
//                R.string.app_name // nav drawer close - description for accessibility
//        ){
//            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(mTitle);
//                // calling onPrepareOptionsMenu() to show action bar icons
//                invalidateOptionsMenu();
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
//                // calling onPrepareOptionsMenu() to hide action bar icons
//                invalidateOptionsMenu();
//            }
//        };
//        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
    }

    private void displayPromo() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            Log.d("TAG", "dysplayPromo: " + version);

            if(version.startsWith("1.3.")){
                Log.d("TAG", "version: " + version);
                //Promo: Gastos Simples PRO Release U$ 0.01 \o/
                SharedPreferences sharedPreferences = getSharedPreferences(Constantes.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                //SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putBoolean(Constantes.SP_KEY_PROMO_DIALOG, true);
                //editor.apply();
                boolean showPromoDialog = sharedPreferences.getBoolean(Constantes.SP_KEY_PROMO_DIALOG, Constantes.SP_PROMO_DIALOG_DEFAULT);
                Log.d("TAG", "showPromoDialog: " + showPromoDialog);
                if(showPromoDialog) {
                    Log.d("TAG", "if: ");
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.dialog_promo, null);
                    TextView okButton = (TextView) view.findViewById(R.id.dialog_promo_text_ok);
                    TextView cancelButton = (TextView) view.findViewById(R.id.dialog_promo_text_cancel);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View buttonOk) {
                            final String appPackageName = Constantes.PACKAGE_GASTOS_SIMPLES_PRO;
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            promoDialog.cancel();
                        }
                    });
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View buttonCancel) {
                            promoDialog.cancel();
                        }
                    });
                    dialog.setView(view);
                    dialog.setCustomTitle(inflater.inflate(R.layout.dialog_promo_title, null));
                    promoDialog = dialog.create();
                    promoDialog.show();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constantes.SP_KEY_PROMO_DIALOG, false);
                    editor.apply();
                }
            }
        } catch (Exception e) {
            //
        }
    }

    private void setActionBarIcon() {
//        EntryDAO dao = EntryDAO.getInstance(this);
//        Calendar calendar = Calendar.getInstance();
//        Float gain = dao.getGain(calendar.get(Calendar.MONTH));
//        Float expense = dao.getExpense(calendar.get(Calendar.MONTH));
//
//        SharedPreferences sharedPreferences = getSharedPreferences(Constantes.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        float yellow = sharedPreferences.getFloat(Constantes.SP_KEY_YELLOW, Constantes.SP_YELLOW_DEFAULT);
//        float red = sharedPreferences.getFloat(Constantes.SP_KEY_RED, Constantes.SP_RED_DEFAULT);
//
//        ActionBar actionBar = getActionBar();
//
//        if((gain - expense) < red){
//            actionBar.setIcon(R.drawable.ic_title_red);
//        } else if((gain - expense) < yellow){
//            actionBar.setIcon(R.drawable.ic_title_yellow);
//        } else{
//            actionBar.setIcon(R.drawable.ic_title_green);
//        }
        getSupportActionBar().setIcon(R.drawable.ic_menu_white_36dp);
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
//            case NAV_ADD:
//                fragment = new AddFragment();
//                actionBarIcon = ICON_SETTINGS;
//                break;
            case NAV_LIST:
                fragment = new EntriesListFragmentFragment();
                mListener = (OnFiltroApplyListener) fragment;
                actionBarIcon = ICON_FILTRO;
                break;
            case NAV_STORE:
                fragment = new StoreFragment();
                actionBarIcon = ICON_NONE;
                break;
            case NAV_RECOVERY:
                fragment = new RecoveryFragment();
                actionBarIcon = ICON_NONE;
                break;
            case NAV_SETTINGS:
                fragment = new SettingsFragment();
                actionBarIcon = ICON_NONE;
                break;
            case NAV_ABOUT:
                fragment = new AboutFragment();
                actionBarIcon = ICON_NONE;
                break;
            //case 3:
                //fragment = new GraphFragment();
             //   Toast.makeText(this, "fragment 4", Toast.LENGTH_SHORT).show();
             //   break;

            default:
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            //Log.e("", "Error in creating fragment");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        // Handle action bar actions click
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                openSettings();
//                return true;
            case R.id.action_filtro:
                openFiltro();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(MenuActivity.this, SettingsFragment.class);
        startActivityForResult(intent, REQUEST_SETTINGS);
    }

    private void openFiltro() {
        Intent intent = new Intent(MenuActivity.this, FiltroActivity.class);
        startActivityForResult(intent, REQUEST_FILTRO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SETTINGS){
            if(resultCode == RESULT_OK){
                //TODO
            }
        } else if(requestCode == REQUEST_FILTRO){
            if(resultCode == RESULT_OK){
                if (mListener != null) {
                    mListener.onFiltroApply((ArrayList<Entry>) data.getSerializableExtra(FiltroActivity.EXTRA_KEY_FILTRO_ENTRIES));
                }
            }
        }
    }

    public interface OnFiltroApplyListener {
        public void onFiltroApply(ArrayList<Entry> entries);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items

        switch (actionBarIcon){
            case ICON_SETTINGS:
//                menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
                menu.findItem(R.id.action_filtro).setVisible(false);
                break;
            case ICON_FILTRO:
//                menu.findItem(R.id.action_settings).setVisible(false);
                menu.findItem(R.id.action_filtro).setVisible(true);
                break;
            case ICON_NONE:
//                menu.findItem(R.id.action_settings).setVisible(false);
                menu.findItem(R.id.action_filtro).setVisible(false);
                break;
            default:
//                menu.findItem(R.id.action_settings).setVisible(false);
                menu.findItem(R.id.action_filtro).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        // Pass any configuration change to the drawer toggls
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constantes.INSTANCE_SAVE_MENUACTIVITY_ACTIONBARICON, actionBarIcon);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        actionBarIcon = savedInstanceState.getInt(Constantes.INSTANCE_SAVE_MENUACTIVITY_ACTIONBARICON);
    }

    public void removeAd(){
        AdView adView = (AdView) findViewById(R.id.ad_main);
        adView.setVisibility(View.GONE);
    }
}
