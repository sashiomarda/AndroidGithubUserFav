package com.example.githubuser

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.databinding.FragmentFollowerBinding
import com.example.githubuser.helper.ViewModelFactory

class FollowerFragment : Fragment() {

    private lateinit var adapter: UserAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentFollowerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentFollowerBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)
        val user = arguments?.getString(ARG_USER_NAME)
        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        binding.rvFollower.layoutManager = LinearLayoutManager(context)
        binding.rvFollower.adapter = adapter
        val factory = activity?.application?.let { ViewModelFactory.getInstance(it, pref) }
        mainViewModel =
            activity?.let { ViewModelProvider(it, factory!!).get(MainViewModel::class.java) }!!
        var list = ""
        when (index) {
            1 -> list = "follower"
            2 -> list = "following"
        }
        showLoading(true, view)
        mainViewModel.findName(user, list, requireContext())
        mainViewModel.listItemFoll.observe(viewLifecycleOwner, { listItem ->
            val listItems = ArrayList<UserItems>()
            for (item in listItem) {
                val userItems = UserItems()
                userItems.userName = item.login
                userItems.photo = item.avatarUrl
                listItems.add(userItems)
            }
            adapter.setData(listItems)
            showLoading(false, view)
        })
        mainViewModel.isLoading.observe(viewLifecycleOwner, {
            showLoading(it, view)
        })
        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val intentUserDetail = Intent(context, UserDetailActivity::class.java)
                intentUserDetail.putExtra(UserDetailActivity.USER_DATA, data)
                startActivity(intentUserDetail)
            }
        })
    }

    private fun showLoading(state: Boolean, view: View) {
        val progressBarFragment: ProgressBar = view.findViewById(R.id.progressBarFragment)
        if (state) {
            progressBarFragment.visibility = View.VISIBLE
        } else {
            progressBarFragment.visibility = View.GONE
        }
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_USER_NAME = "user_name"
        private lateinit var pref: SettingPreferences

        @JvmStatic
        fun newInstance(index: Int, user: String, mPref: SettingPreferences) =
            FollowerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, index)
                    putString(ARG_USER_NAME, user)
                    pref = mPref
                }
            }
    }
}