package com.example.mygallery.videofile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import androidx.annotation.NonNull;
/*import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.design.widget.CoordinatorLayout.LayoutParams;*/
import android.app.AlertDialog.Builder;
/*import android.support.v7.widget.GridLayoutManager;*/
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mygallery.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class SelectAlbumBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetAlbumsAdapter adapter;
    private boolean canGoBack = false;
    private TextView currentFolderPath;
    private boolean exploreMode = false;
    private LinearLayout exploreModePanel;
    private ArrayList<File> folders;
    private ImageView imgExploreMode;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == 5) {
                SelectAlbumBottomSheet.this.dismiss();
            }
        }

        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };
    private OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View view) {
            String path = view.findViewById(R.id.name_folder).getTag().toString();
            if (SelectAlbumBottomSheet.this.isExploreMode()) {
                SelectAlbumBottomSheet.this.displayContentFolder(new File(path));
            } else {
                SelectAlbumBottomSheet.this.selectAlbumInterface.folderSelected(path);
            }
        }
    };
    private SelectAlbumInterface selectAlbumInterface;

    private String title;

    public interface SelectAlbumInterface {
        void folderSelected(String str);
    }

    class BottomSheetAlbumsAdapter extends Adapter<BottomSheetAlbumsAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView folderCount;
            TextView folderName;
            ImageView imgFolder;

            ViewHolder(View itemView) {
                super(itemView);
                this.folderName = (TextView) itemView.findViewById(R.id.name_folder);
                this.folderCount = (TextView) itemView.findViewById(R.id.count_folder);
                this.imgFolder = (ImageView) itemView.findViewById(R.id.folder_icon_bottom_sheet_item);
            }
        }

        BottomSheetAlbumsAdapter() {
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_folder_bottom_sheet_item, parent, false);
            v.setOnClickListener(SelectAlbumBottomSheet.this.onClickListener);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            File f = (File) SelectAlbumBottomSheet.this.folders.get(position);
            String[] list = f.list();
            int count = list == null ? 0 : list.length;
            holder.folderName.setText(f.getName());
            holder.folderName.setTag(f.getPath());
        }

        public int getItemCount() {
            return SelectAlbumBottomSheet.this.folders.size();
        }
    }



    @SuppressLint("RestrictedApi")
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.select_folder_bottom_sheet, null);
        RecyclerView mRecyclerView = (RecyclerView) contentView.findViewById(R.id.folders);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        this.adapter = new BottomSheetAlbumsAdapter();
        mRecyclerView.setAdapter(this.adapter);
        this.exploreModePanel = (LinearLayout) contentView.findViewById(R.id.explore_mode_panel);
        this.currentFolderPath = (TextView) contentView.findViewById(R.id.bottom_sheet_sub_title);
        this.imgExploreMode = (ImageView) contentView.findViewById(R.id.toggle_hidden_icon);
        this.imgExploreMode.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SelectAlbumBottomSheet.this.toggleExplorerMode(!SelectAlbumBottomSheet.this.exploreMode);
            }
        });
        toggleExplorerMode(false);
        ((TextView) contentView.findViewById(R.id.bottom_sheet_title)).setText(this.title);
        contentView.findViewById(R.id.done).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SelectAlbumBottomSheet.this.selectAlbumInterface.folderSelected(SelectAlbumBottomSheet.this.currentFolderPath.getText().toString());
            }
        });
        contentView.findViewById(R.id.ll_create_new_folder).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                final EditText editText = new EditText(SelectAlbumBottomSheet.this.getContext());
                Builder builder = new Builder(SelectAlbumBottomSheet.this.getActivity());
                AlertDialogsHelper.getInsertTextDialog((Activity) SelectAlbumBottomSheet.this.getActivity(), builder, editText, R.string.new_folder);
                builder.setPositiveButton((int) R.string.ok_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File folderPath = new File(SelectAlbumBottomSheet.this.currentFolderPath.getText().toString() + File.separator + editText.getText().toString());
                        if (folderPath.mkdir()) {
                            SelectAlbumBottomSheet.this.displayContentFolder(folderPath);
                        }
                    }
                });
                builder.show();
            }
        });
        dialog.setContentView(contentView);
        CoordinatorLayout.Behavior behavior = ((LayoutParams) ((View) contentView.getParent()).getLayoutParams()).getBehavior();
        if (behavior != null && (behavior instanceof BottomSheetBehavior)) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(this.mBottomSheetBehaviorCallback);
        }
        this.adapter.notifyDataSetChanged();
    }

    private void displayContentFolder(File dir) {
        this.canGoBack = false;
        if (dir.canRead()) {
            this.folders = new ArrayList();
            File parent = dir.getParentFile();
            if (parent.canRead()) {
                this.canGoBack = true;
                this.folders.add(0, parent);
            }
            File[] files = dir.listFiles(new FoldersFileFilter());
            if (files != null && files.length > 0) {
                this.folders.addAll(new ArrayList(Arrays.asList(files)));
                this.currentFolderPath.setText(dir.getAbsolutePath());
            }
            this.currentFolderPath.setText(dir.getAbsolutePath());
            this.adapter.notifyDataSetChanged();
        }
    }

    private void setExploreMode(boolean exploreMode) {
        this.exploreMode = exploreMode;
    }

    private boolean isExploreMode() {
        return this.exploreMode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void toggleExplorerMode(boolean enabled) {
        this.folders = new ArrayList();
        setExploreMode(enabled);
        if (enabled) {
            displayContentFolder(Environment.getExternalStorageDirectory());
            this.imgExploreMode.setImageResource(R.drawable.ic_folder_black_24dp);
            this.exploreModePanel.setVisibility(View.VISIBLE);
        } else {
            this.currentFolderPath.setText(R.string.local_folder);
            Iterator it = ((MyApplication) getActivity().getApplicationContext()).getAlbums().dispAlbums.iterator();
            while (it.hasNext()) {
                this.folders.add(new File(((Album) it.next()).getPath()));
            }
            this.imgExploreMode.setImageResource(R.drawable.ic_folder_black_24dp);
            this.exploreModePanel.setVisibility(View.GONE);
        }
        this.adapter.notifyDataSetChanged();
    }
}
