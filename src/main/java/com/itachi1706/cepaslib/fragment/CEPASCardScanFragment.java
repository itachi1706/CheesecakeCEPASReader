package com.itachi1706.cepaslib.fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itachi1706.cepaslib.R;
import com.itachi1706.cepaslib.activity.ReadingTagActivity;
import com.itachi1706.cepaslib.activity.SupportedCardsActivity;
import com.itachi1706.cepaslib.util.Utils;

import androidx.fragment.app.Fragment;

/**
 * Create the Activity that calls this fragment by defining the following in AndroidManifest.xml
 * <activity android:name="<path>"
 *             android:configChanges="keyboardHidden|orientation"
 *             android:label="<name>"
 *             android:screenOrientation="sensorPortrait"
 *             tools:ignore="AppLinkUrlError">
 *             <intent-filter>
 *                 <action android:name="android.intent.action.VIEW" />
 *                 <action android:name="android.intent.action.EDIT" />
 *                 <action android:name="android.intent.action.PICK" />
 *
 *                 <category android:name="android.intent.category.DEFAULT" />
 *
 *                 <data android:mimeType="vnd.android.cursor.dir/${applicationId}.card" />
 *             </intent-filter>
 *         </activity>
 */
public class CEPASCardScanFragment extends Fragment {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private String[][] mTechLists = new String[][]{
            new String[]{IsoDep.class.getName()},
            new String[]{MifareClassic.class.getName()},
            new String[]{MifareUltralight.class.getName()},
            new String[]{NfcA.class.getName()},
            new String[]{NfcF.class.getName()},
    };

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_main, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            v.getViewTreeObserver().addOnWindowFocusChangeListener(hasFocus -> {
                if (hasFocus) {
                    updateObfuscationNotice(mNfcAdapter != null);
                }
            });
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

        if (mNfcAdapter != null) {
            Utils.checkNfcEnabled(getActivity(), mNfcAdapter);

            Intent intent = new Intent(getActivity(), ReadingTagActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            mPendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
        }

        updateObfuscationNotice(mNfcAdapter != null);

        v.findViewById(R.id.cepas_supported_card_button).setOnClickListener(this::onSupportedCardsClick);
        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

        updateObfuscationNotice(mNfcAdapter != null);
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(getActivity(), mPendingIntent, null, mTechLists);
        }
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateObfuscationNotice(mNfcAdapter != null);
        }
    }*/

    private void updateObfuscationNotice(boolean hasNfc) {
        TextView directions = v.findViewById(R.id.directions);

        if (!hasNfc) {
            directions.setText(R.string.nfc_unavailable);
        } else {
            directions.setText(R.string.directions);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(getActivity());
        }
    }

    public void onSupportedCardsClick(View view) {
        startActivity(new Intent(getActivity(), SupportedCardsActivity.class));
    }
}
