package org.acgnu.third.oplus;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.acgnu.xposed.R;

public class OPlusPassCodeFragment extends Fragment implements View.OnClickListener {
    private Fragment prevFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.oplus_qr_layout, null);
        Button button = (Button) linearLayout.findViewById(R.id.oplus_pass_back);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.settings_container, prevFragment).commit();
            }
        });

        View imageView = linearLayout.findViewById(R.id.iv_qrcode);
        imageView.setOnClickListener(this);
        imageView.callOnClick();
        return linearLayout;
    }

    public Fragment getPrevFragment() {
        return prevFragment;
    }

    public void setPrevFragment(Fragment prevFragment) {
        this.prevFragment = prevFragment;
    }

    @Override
    public void onClick(View view) {
        OPlusPassCodeGenerator.instance().generateAsync((ImageView) view);
    }
}
