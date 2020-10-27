package com.charliechristensen.coindetail

import com.google.android.material.tabs.TabLayout

fun TabLayout.selectedTab(index: Int) {
    if (selectedTabPosition == index) {
        return
    }
    getTabAt(index)?.select()
}
