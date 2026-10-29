package net.kdt.pojavlaunch.fragments;

import static net.kdt.pojavlaunch.Tools.hasNoOnlineProfileDialog;
import static net.kdt.pojavlaunch.Tools.hasOnlineProfile;
import static net.kdt.pojavlaunch.Tools.openPath;
import static net.kdt.pojavlaunch.Tools.shareLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kdt.mcgui.mcVersionSpinner;

import net.kdt.pojavlaunch.CustomControlsActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.io.File;

public class MainMenuFragment extends Fragment {
    public static final String TAG = "MainMenuFragment";

    private mcVersionSpinner mVersionSpinner;
    private ViewGroup mRightPane;
    private View mBottomBarBg;
    private View mPlayButton;
    private View mEditProfileButton;
    private View mBottomBar;
    private OnBackPressedCallback mRightPaneBackCallback;

    private boolean isTwoPane() {
        return mRightPane != null;
    }

    public boolean isRightPaneActive() {
        return isTwoPane() && getChildFragmentManager().getBackStackEntryCount() > 0;
    }

    public void popRightPane() {
        if (!isTwoPane()) return;
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            getChildFragmentManager().popBackStack();
        }
    }

    public void clearRightPane() {
        if (!isTwoPane()) return;
        int count = getChildFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getChildFragmentManager().popBackStack(
                    getChildFragmentManager().getBackStackEntryAt(0).getName(),
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void setBottomBarVisible(boolean visible) {
        if (mBottomBar != null) {
            mBottomBar.setVisibility(visible ? View.VISIBLE : View.GONE);
            mBottomBar.requestLayout();
        }
    }

    public void refreshHomeState() {
        if (getView() == null || !isAdded()) return;
        clearRightPane();
        setBottomBarVisible(true);
        if (mVersionSpinner != null) mVersionSpinner.reloadProfiles();
    }

    public void selectInstance(String profileKey) {
        LauncherPreferences.DEFAULT_PREF.edit()
                .putString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, profileKey)
                .apply();
        ExtraCore.setValue(ExtraConstants.REFRESH_VERSION_SPINNER, profileKey);
        clearRightPane();
        if (mVersionSpinner != null) mVersionSpinner.reloadProfiles();
    }

    public void reloadSpinner() {
        if (mVersionSpinner != null) mVersionSpinner.reloadProfiles();
    }

    public void openChildPane(Class<? extends Fragment> fragmentClass, String tag,
                              @Nullable Bundle args) {
        openPane(fragmentClass, tag, args);
    }

    public boolean tryOpenInRightPane(Class<? extends Fragment> fragmentClass, String tag,
                                      @Nullable Bundle args) {
        if (!isTwoPane()) return false;
        openPane(fragmentClass, tag, args);
        return true;
    }

    private void openPane(Class<? extends Fragment> fragmentClass, String tag,
                          @Nullable Bundle args) {
        if (isTwoPane()) {
            androidx.fragment.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.fragment_enter_forward, R.anim.fragment_exit_forward,
                    R.anim.fragment_enter_back, R.anim.fragment_exit_back);
            transaction
                    .replace(R.id.right_pane_container, fragmentClass, args, tag)
                    .addToBackStack(tag)
                    .commit();
        } else {
            if (fragmentClass == ModsSearchFragment.class) {
                Tools.swapFragment(requireActivity(), fragmentClass, tag, args,
                        R.anim.fade_in_slide_up, R.anim.fade_out_slide_down,
                        R.anim.fade_in_slide_up, R.anim.fade_out_slide_down);
            } else {
                Tools.swapFragment(requireActivity(), fragmentClass, tag, args);
            }
        }
    }

    public MainMenuFragment() {
        // Use modern home layout matching the screenshot
        super(R.layout.fragment_home_modern);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRightPaneBackCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (mRightPane == null) return;
                if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                    getChildFragmentManager().popBackStackImmediate();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, mRightPaneBackCallback);
        getChildFragmentManager().addOnBackStackChangedListener(mBackStackListener);
    }

    private final androidx.fragment.app.FragmentManager.OnBackStackChangedListener
            mBackStackListener = () -> {
        mRightPaneBackCallback.setEnabled(isRightPaneActive());
        if (!isTwoPane()) return;
        boolean showBar = getChildFragmentManager().getBackStackEntryCount() == 0;
        setBottomBarVisible(showBar);
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Modern layout wiring
        View launchBtn = view.findViewById(R.id.btn_launch);
        if (launchBtn != null) {
            launchBtn.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));
        }

        View navHome = view.findViewById(R.id.nav_home);
        View navProfile = view.findViewById(R.id.nav_profile);
        View navMods = view.findViewById(R.id.nav_mods);
        View navSettings = view.findViewById(R.id.nav_settings);

        if (navHome != null) navHome.setOnClickListener(v -> {
            // already home
        });

        if (navProfile != null) navProfile.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Profile", Toast.LENGTH_SHORT).show();
        });

        if (navMods != null) navMods.setOnClickListener(v -> {
            try {
                Tools.swapFragment(requireActivity(), CsModsBrowserFragment.class, "CsModsBrowser", null);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Mods", Toast.LENGTH_SHORT).show();
            }
        });

        if (navSettings != null) navSettings.setOnClickListener(v -> {
            try {
                Tools.swapFragment(requireActivity(), net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment.class, "SETTINGS_FRAGMENT", null);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show();
            }
        });

        // Keep old references null-safe so two-pane / legacy code doesn't crash
        mVersionSpinner = null;
        mRightPane = null;
        mBottomBarBg = null;
        mPlayButton = null;
        mEditProfileButton = null;
        mBottomBar = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRightPane = null; mBottomBarBg = null; mPlayButton = null; mEditProfileButton = null; mBottomBar = null;
        getChildFragmentManager().removeOnBackStackChangedListener(mBackStackListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private File getCurrentProfileDirectory() {
        String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, null);
        if (!Tools.isValidString(currentProfile)) return new File(Tools.DIR_GAME_NEW);
        LauncherProfiles.load();
        MinecraftProfile profileObject = LauncherProfiles.mainProfileJson.profiles.get(currentProfile);
        if (profileObject == null) return new File(Tools.DIR_GAME_NEW);
        return Tools.getGameDirPath(profileObject);
    }

    private void applyPremiumTouchAnimation(View... views) {
        for (View v : views) {
            if (v == null) continue;
            v.setOnTouchListener((view, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN: view.animate().scaleX(0.94f).scaleY(0.94f).alpha(0.9f).setDuration(70).setInterpolator(new android.view.animation.AccelerateInterpolator()).start(); break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL: view.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(160).setInterpolator(new android.view.animation.PathInterpolator(0.22f, 1f, 0.36f, 1f)).start(); break;
                }
                return false;
            });
        }
    }
}
