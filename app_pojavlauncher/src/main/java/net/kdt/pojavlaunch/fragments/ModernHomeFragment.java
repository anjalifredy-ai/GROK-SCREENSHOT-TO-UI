package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;

/**
 * Modern home screen matching the provided screenshot UI.
 * Sidebar + hero launch card + profile + news cards.
 */
public class ModernHomeFragment extends Fragment {

    public static final String TAG = "ModernHomeFragment";

    public ModernHomeFragment() {
        super(R.layout.fragment_home_modern);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View launchBtn = view.findViewById(R.id.btn_launch);
        if (launchBtn != null) {
            launchBtn.setOnClickListener(v -> {
                ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true);
            });
        }

        // Sidebar navigation placeholders
        View navHome = view.findViewById(R.id.nav_home);
        View navProfile = view.findViewById(R.id.nav_profile);
        View navMods = view.findViewById(R.id.nav_mods);
        View navSettings = view.findViewById(R.id.nav_settings);

        if (navHome != null) navHome.setOnClickListener(v -> {
            // already on home
        });

        if (navProfile != null) navProfile.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Profile", Toast.LENGTH_SHORT).show();
        });

        if (navMods != null) navMods.setOnClickListener(v -> {
            // open mods browser if available
            try {
                Tools.swapFragment(requireActivity(), CsModsBrowserFragment.class, "CsModsBrowser", null);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Mods browser", Toast.LENGTH_SHORT).show();
            }
        });

        if (navSettings != null) navSettings.setOnClickListener(v -> {
            try {
                Tools.swapFragment(requireActivity(), net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment.class, "SETTINGS_FRAGMENT", null);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
