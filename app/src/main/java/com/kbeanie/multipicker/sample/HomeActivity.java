package com.kbeanie.multipicker.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kbeanie.multipicker.sample.adapters.DemosAdapter;

/**
 * Created by kbibek on 2/18/16.
 */
public class HomeActivity extends AbActivity implements AdapterView.OnItemClickListener {

    private int[] IMAGE_OPTIONS = {DemosAdapter.IMAGE_PICKER_ACTIVITY, DemosAdapter.IMAGE_PICKER_FRAGMENT, DemosAdapter.IMAGE_PICKER_SUPPORT_FRAGMENT};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ListView lvDemoTypes = (ListView) findViewById(R.id.lvDemoTypes);
        lvDemoTypes.setAdapter(new DemosAdapter(this));
        lvDemoTypes.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == DemosAdapter.PICKER_CONTACT) {
            launchRequiredActivity(DemosAdapter.CONTACT_PICKER_ACTIVITY);
        } else if (id == DemosAdapter.PICKER_MEDIA) {
            launchRequiredActivity(DemosAdapter.MEDIA_PICKER_ACTIVITY);
        } else {
            showOptionsDialog((int) id);
        }
    }

    private void showDemo(Intent intent) {
        startActivity(intent);
    }

    private void showOptionsDialog(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose example type");
        CharSequence[] options = new CharSequence[3];
        options[0] = "Activity";
        options[1] = "Fragment";
        options[2] = "Support Fragment";
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchRequiredActivity(getWhich(id, which));
            }
        });

        builder.create().show();
    }

    private void launchRequiredActivity(int which) {
        Intent intent = null;
        switch (which) {
            case DemosAdapter.IMAGE_PICKER_ACTIVITY:
                intent = new Intent(this, ImagePickerActivity.class);
                break;
            case DemosAdapter.IMAGE_PICKER_FRAGMENT:
                intent = new Intent(this, ImagePickerFragmentActivity.class);
                break;
            case DemosAdapter.IMAGE_PICKER_SUPPORT_FRAGMENT:
                intent = new Intent(this, ImagePickerSupportFragmentActivity.class);
                break;
        }

        if (intent != null) {
            showDemo(intent);
        }
    }

    private int getWhich(int id, int index) {
        switch (id) {
            case 1:
                return IMAGE_OPTIONS[index];
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_storage) {
            showStorageSettingsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showStorageSettingsMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cache Location");
        CharSequence[] cacheLocations = new CharSequence[4];
        cacheLocations[0] = "Ext Storage - App Directory";
        cacheLocations[1] = "Ext Storage - Public Directory";
        cacheLocations[2] = "Ext Storage - Cache Directory";
        cacheLocations[3] = "Internal - App Directory";

        builder.setSingleChoiceItems(cacheLocations, preferences.getCacheLocation(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                preferences.setCacheLocation(which);
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}