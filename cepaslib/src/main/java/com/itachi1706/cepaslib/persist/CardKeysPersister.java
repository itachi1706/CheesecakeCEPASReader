package com.itachi1706.cepaslib.persist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.itachi1706.cepaslib.persist.db.model.SavedKey;

import java.util.List;

public interface CardKeysPersister {

    @NonNull
    List<SavedKey> getSavedKeys();

    @Nullable
    SavedKey getForTagId(@NonNull String tagId);

    long insert(@NonNull SavedKey savedKey);

    void delete(@NonNull SavedKey savedKey);
}
