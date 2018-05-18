package com.prodev.encyclopedia.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prodev.encyclopedia.Config;
import com.prodev.encyclopedia.MainActivity;
import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.adapter.SourceAdapter;
import com.prodev.encyclopedia.dialogs.custom.EditDialog;
import com.prodev.encyclopedia.helper.SourceHelper;
import com.prodev.encyclopedia.tools.FileTools;
import com.simplelib.SimpleFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SourceFragment extends SimpleFragment {
    private SourceHelper sourceHelper;

    private File folder;

    private ViewGroup contentLayout;

    private RecyclerView list;
    private LinearLayoutManager manager;
    private SourceAdapter adapter;

    private FloatingActionButton exportButton;

    public SourceFragment() {
        super(R.layout.source_fragment);

        this.sourceHelper = new SourceHelper();
    }

    @Override
    public void create(View view) {
        if (!requestPermission()) return;

        contentLayout = (ViewGroup) findViewById(R.id.source_fragment_content_layout);

        folder = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        if (!folder.exists()) folder.mkdirs();

        list = (RecyclerView) findViewById(R.id.source_fragment_list);

        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);

        adapter = new SourceAdapter() {
            @Override
            public void onSelect(File file) {
                importFile(file);
            }

            @Override
            public void onClickItem(File file) {

            }

            @Override
            public void onLongClickItem(File file) {
                removeFile(file);
            }
        };
        list.setAdapter(adapter);

        loadFiles();

        exportButton = (FloatingActionButton) findViewById(R.id.source_fragment_export);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportFile();
            }
        });
    }

    public boolean requestPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2222);

                MainActivity.switchToMenuItem(R.id.nav_translate, true);
                return false;
            } else return true;
        } catch (Exception e) {
        }

        return false;
    }

    private void removeFile(final File file) {
        Snackbar snackbar = Snackbar
                .make(contentLayout, getString(R.string.remove_file), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.remove), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            boolean success = file.delete();
                            if (success)
                                Toast.makeText(getActivity(), getString(R.string.removed_file), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }

                        loadFiles();
                    }
                });
        snackbar.show();
    }

    private void loadFiles() {
        ArrayList<File> files = new ArrayList<>(Arrays.asList(folder.listFiles()));

        adapter.clear();
        for (File file : files) {
            if (file.getName().endsWith(Config.FILE_EXTENSION))
                adapter.add(file);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MainActivity.switchToMenuItem(R.id.nav_source, false);
        } catch (Exception e) {
        }
    }

    private void importFile(final File file) {
        if (file.exists()) {
            EditDialog dialog = new EditDialog(getActivity()) {
                @Override
                public void onResult(String text) {
                    importFile(file, text);
                }
            };
            dialog.setTitle(getString(R.string.decryption_key));
            dialog.setHint(getString(R.string.enter_decryption_key));
            dialog.show();
        }
    }

    private void importFile(File file, String key) {
        if (file.exists()) {
            String code = FileTools.readFile(file);

            if (code != null && code.length() > 0) {
                boolean success = sourceHelper.loadSource(code, key);
                if (success)
                    Toast.makeText(getActivity(), getString(R.string.imported_successfully), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), getString(R.string.import_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void exportFile() {
        EditDialog dialog = new EditDialog(getActivity()) {
            @Override
            public void onResult(String text) {
                if (text.length() > 0)
                    exportFile(text);
            }
        };
        dialog.setTitle(getString(R.string.export_words));
        dialog.setHint(getString(R.string.enter_file_name));
        dialog.show();
    }

    private void exportFile(final String fileName) {
        for (File item : adapter.getList()) {
            String itemName = item.getName();
            if (itemName != null && itemName.length() > 0)
                itemName = itemName.substring(0, itemName.length() - Config.FILE_EXTENSION.length());
            if (itemName != null && itemName.length() > 0 && itemName.equalsIgnoreCase(fileName)) {
                Toast.makeText(getActivity(), getString(R.string.file_exists), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        EditDialog dialog = new EditDialog(getActivity()) {
            @Override
            public void onResult(String text) {
                if (text.length() > 0) {
                    String code = sourceHelper.saveAllSource(text);

                    File file = new File(folder, fileName + Config.FILE_EXTENSION);
                    FileTools.saveFile(file, code);

                    loadFiles();
                }
            }
        };
        dialog.setTitle(getString(R.string.cryption_key));
        dialog.setHint(getString(R.string.enter_cryption_key));
        dialog.show();
    }
}
