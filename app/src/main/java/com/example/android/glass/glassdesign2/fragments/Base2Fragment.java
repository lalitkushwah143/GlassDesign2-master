package com.example.android.glass.glassdesign2.fragments;

import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.menu.MenuActivity;

import static android.app.Activity.RESULT_OK;

public class Base2Fragment extends Fragment implements OnSingleTapUpListener {

    /**
     * Key for obtaining menu value from fragment arguments.
     */
    protected static final String MENU_KEY = "menu_key";

    /**
     * Default value for menu obtained from fragment arguments.
     */
    protected static final int MENU_DEFAULT_VALUE = 0;

    /**
     * Request code for starting activity for result. This value doesn't have any special meaning.
     */
    protected static final int REQUEST_CODE = 205;

    @Override
    public void onSingleTapUp() {
        if (getArguments() != null) {
            int menu = getArguments().getInt(MENU_KEY, MENU_DEFAULT_VALUE);
            if (menu != MENU_DEFAULT_VALUE) {
                Intent intent = new Intent(getActivity(), MenuActivity.class);
                intent.putExtra(MENU_KEY, menu);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    /**
     * Code for a response to selected menu item should be placed inside of this method.
     *
     * @param requestCode is a code which should match the {@link Base2Fragment#REQUEST_CODE}
     * @param resultCode is a code set by the {@link com.example.android.glass.glassdesign2.menu.MenuActivity}
     * @param data is a id passed by the {@link MenuActivity#EXTRA_MENU_ITEM_ID_KEY} key. Refers to
     * the selected menu option
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            String selectedOption = "";
            switch (id) {
                case R.id.add:
                    selectedOption = getString(R.string.add);
                    break;
                case R.id.save:
                    selectedOption = getString(R.string.save);
                    break;
                case R.id.delete:
                    selectedOption = getString(R.string.delete);
                    break;
            }
            Toast.makeText(getActivity(), selectedOption + " option selected.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
