package com.example.musicmap.utils;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * This class makes it easier to initialize and replace fragments inside the given fragment
 * container.
 */
public class FragmentUtil {

    /**
     * @param fragmentManager
     * @param containerID
     * @param fragmentClass
     */
    public static void initFragment(FragmentManager fragmentManager, @IdRes int containerID,
                                    @NonNull Class<? extends Fragment> fragmentClass) {
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(containerID, fragmentClass, null)
                .commit();
    }

    /**
     * @param fragmentManager
     * @param containerID
     * @param fragmentClass
     */
    public static void replaceFragment(FragmentManager fragmentManager, @IdRes int containerID,
                                       @NonNull Class<? extends Fragment> fragmentClass) {
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(containerID, fragmentClass, null)
                .commit();
    }
}
