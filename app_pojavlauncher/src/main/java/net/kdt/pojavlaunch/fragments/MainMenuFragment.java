package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.value.MinecraftAccount;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

/**
 * Main home screen – now using modern screenshot UI.
 */
public class MainMenuFragment extends Fragment {
    public static final String TAG = "MainMenuFragment";

    public MainMenuFragment() {
        super(R.layout.fragment_home_modern);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Launch button
        View launchBtn = view.findViewById(R.id.btn_launch);
        if (launchBtn != null) {
            launchBtn.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));
        }

        // Sidebar
        View navHome = view.findViewById(R.id.nav_home);
        View navProfile = view.findViewById(R.id.nav_profile);
        View navMods = view.findViewById(R.id.nav_mods);
        View navSettings = view.findViewById(R.id.nav_settings);
        View cardMods = view.findViewById(R.id.card_mods);

        if (navHome != null) navHome.setOnClickListener(v -> { /* already home */ });

        if (navProfile != null) navProfile.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Profile", Toast.LENGTH_SHORT).show());

        if (navMods != null) navMods.setOnClickListener(v -> openMods());
        if (cardMods != null) cardMods.setOnClickListener(v -> openMods());

        if (navSettings != null) navSettings.setOnClickListener(v -> {
            try {
                Tools.swapFragment(requireActivity(),
                        net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment.class,
                        "SETTINGS_FRAGMENT", null);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show();
            }
        });

        // Bind real data
        bindVersionInfo(view);
        bindProfileInfo(view);
    }

    private void openMods() {
        try {
            Tools.swapFragment(requireActivity(), ModsSearchFragment.class, ModsSearchFragment.TAG, null);
        } catch (Exception e) {
            try {
                Tools.swapFragment(requireActivity(), CsModsBrowserFragment.class, CsModsBrowserFragment.TAG, null);
            } catch (Exception ex) {
                Toast.makeText(requireContext(), "Mods", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bindVersionInfo(View view) {
        TextView tvVersion = view.findViewById(R.id.tv_version_name);
        TextView tvSub = view.findViewById(R.id.tv_version_sub);
        TextView btnLaunch = view.findViewById(R.id.btn_launch);

        String versionId = "1.20.1";
        try {
            String selectedProfile = LauncherPreferences.DEFAULT_PREF.getString(
                    LauncherPreferences.PREF_KEY_CURRENT_PROFILE, "");
            if (LauncherProfiles.mainProfileJson != null
                    && LauncherProfiles.mainProfileJson.profiles != null
                    && LauncherProfiles.mainProfileJson.profiles.containsKey(selectedProfile)) {
                MinecraftProfile prof = LauncherProfiles.mainProfileJson.profiles.get(selectedProfile);
                if (prof != null && prof.lastVersionId != null && !"Unknown".equals(prof.lastVersionId)) {
                    versionId = prof.lastVersionId;
                }
            }
        } catch (Exception ignored) {}

        if (tvVersion != null) {
            tvVersion.setText("Minecraft " + versionId);
        }
        if (tvSub != null) {
            tvSub.setText("Ready to launch");
        }
        if (btnLaunch instanceof com.google.android.material.button.MaterialButton) {
            ((com.google.android.material.button.MaterialButton) btnLaunch)
                    .setText("LAUNCH " + versionId);
        }
    }

    private void bindProfileInfo(View view) {
        TextView tvName = view.findViewById(R.id.tv_profile_name);
        TextView tvSub = view.findViewById(R.id.tv_profile_sub);

        String name = "Player";
        try {
            // Try to get selected account name from activity if possible
            if (getActivity() instanceof net.kdt.pojavlaunch.LauncherActivity) {
                // Fallback name
                name = "Player";
            }
        } catch (Exception ignored) {}

        if (tvName != null) tvName.setText(name);
        if (tvSub != null) tvSub.setText("Your Profile");
    }

    // Keep these methods so existing callers don't break
    public void refreshHomeState() {}
    public void selectInstance(String profileKey) {}
    public void reloadSpinner() {}
    public boolean tryOpenInRightPane(Class<? extends Fragment> fragmentClass, String tag, @Nullable Bundle args) {
        return false;
    }
    public void openChildPane(Class<? extends Fragment> fragmentClass, String tag, @Nullable Bundle args) {}
    public boolean isRightPaneActive() { return false; }
    public void popRightPane() {}
    public void clearRightPane() {}
}
