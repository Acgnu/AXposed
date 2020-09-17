package org.acgnu.third.oplus;

import android.app.Fragment;
import android.app.FragmentManager;
import android.preference.Preference;
import org.acgnu.xposed.R;

public class OPlusFragmenClickListener implements Preference.OnPreferenceClickListener {
    private OPlusPassCodeFragment mOPlusPassCodeFragment;
    private Fragment prevFragment;
    private FragmentManager manager;

    public OPlusFragmenClickListener(FragmentManager manager, Fragment prevFragment){
        this.manager = manager;
        this.prevFragment = prevFragment;
    }
    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (null == mOPlusPassCodeFragment) {
            mOPlusPassCodeFragment = new OPlusPassCodeFragment();
            mOPlusPassCodeFragment.setPrevFragment(prevFragment);
        }
        manager.beginTransaction().replace(R.id.settings_container, mOPlusPassCodeFragment).commit();
//        manager.beginTransaction().hide(prevFragment).show(mOPlusPassCodeFragment).commit();
        return false;
    }
}
