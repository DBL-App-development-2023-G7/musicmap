package com.example.musicmap.util.ui;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * This class makes it easier to initialize and replace fragments inside the given fragment
 * container.
 */
public class FragmentUtil {

    /**
     * This method initializes the given container with the given fragment class.
     *
     * @param fragmentManager the given Fragment Manager
     * @param containerID     the id of the container
     * @param fragmentClass   the class of the fragment
     */
    public static void initFragment(@NonNull FragmentManager fragmentManager,
                                    @IdRes int containerID,
                                    @NonNull Class<? extends Fragment> fragmentClass) {
        initFragment(fragmentManager, containerID, fragmentClass, null);
    }

    /**
     * This method initializes the given container with the given fragment class.
     *
     * @param fragmentManager the given Fragment Manager
     * @param containerID     the id of the container
     * @param fragmentClass   the class of the fragment
     * @param args            the arguments for the fragment
     */
    public static void initFragment(@NonNull FragmentManager fragmentManager,
                                    @IdRes int containerID,
                                    @NonNull Class<? extends Fragment> fragmentClass,
                                    @Nullable Bundle args) {
        fragmentManager.beginTransaction().setReorderingAllowed(true).add(containerID,
                fragmentClass, args).commit();
    }

    /**
     * This method replaces the current fragment inside the given container with the given
     * fragment class.
     *
     * @param fragmentManager the given Fragment Manager
     * @param containerID     the id of the container
     * @param fragmentClass   the class of the fragment
     */
    public static void replaceFragment(@NonNull FragmentManager fragmentManager,
                                       @IdRes int containerID,
                                       @NonNull Class<? extends Fragment> fragmentClass) {
        // TODO don't replace if that fragment is already in the container
        fragmentManager.beginTransaction().setReorderingAllowed(true).replace(containerID,
                fragmentClass, null).commit();
    }

    /**
     * This method replaces the current fragment inside the given container with the given
     * fragment class.
     *
     * @param fragmentManager the given Fragment Manager
     * @param containerID     the id of the container
     * @param fragmentClass   the class of the fragment
     * @param args            the arguments for the fragment
     */
    public static void replaceFragment(@NonNull FragmentManager fragmentManager,
                                       @IdRes int containerID,
                                       @NonNull Class<? extends Fragment> fragmentClass,
                                       @Nullable Bundle args) {
        // TODO don't replace if that fragment is already in the container
        fragmentManager.beginTransaction().setReorderingAllowed(true).replace(containerID,
                fragmentClass, args).commit();
    }

}
