package com.example.githubuser

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(
    activity: AppCompatActivity,
    userName: String,
    private var pref: SettingPreferences
) :
    FragmentStateAdapter(activity) {

    private val user = userName
    override fun createFragment(position: Int): Fragment {
        return FollowerFragment.newInstance(position + 1, user, pref)
    }

    override fun getItemCount(): Int {
        return 2
    }
}