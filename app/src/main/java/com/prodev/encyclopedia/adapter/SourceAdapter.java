package com.prodev.encyclopedia.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.prodev.encyclopedia.Config;
import com.prodev.encyclopedia.R;
import com.simplelib.adapter.SimpleRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;

public abstract class SourceAdapter extends SimpleRecyclerAdapter<File> {
    public SourceAdapter() {
    }

    public SourceAdapter(ArrayList<File> list) {
        super(list);
    }

    @Override
    public View createHolder(ViewGroup viewGroup, int i) {
        return inflateLayout(viewGroup, R.layout.source_item);
    }

    @Override
    public void bindHolder(ViewHolder viewHolder, final File file, int i) {
        TextView fileView = (TextView) viewHolder.findViewById(R.id.source_item_file);
        TextView pathView = (TextView) viewHolder.findViewById(R.id.source_item_path);

        ImageButton importButton = (ImageButton) viewHolder.findViewById(R.id.source_item_import);

        try {
            String fileText = file.getName();
            String pathText = file.getPath();

            if (fileText != null && fileText.length() > 0 && fileText.endsWith(Config.FILE_EXTENSION))
                fileText = fileText.substring(0, fileText.length() - Config.FILE_EXTENSION.length());

            fileView.setText(fileText);
            pathView.setText(pathText);
        } catch (Exception e) {
        }

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.exists())
                    onSelect(file);
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(file);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongClickItem(file);
                return false;
            }
        });
    }

    public abstract void onSelect(File file);

    public abstract void onClickItem(File file);
    public abstract void onLongClickItem(File file);
}
