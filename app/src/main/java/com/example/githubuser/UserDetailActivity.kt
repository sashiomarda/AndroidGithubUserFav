package com.example.githubuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubuser.database.Fav
import com.example.githubuser.databinding.ActivityUserDetailBinding
import com.example.githubuser.helper.ViewModelFactory
import com.example.githubuser.insert.FavAddUpdateViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class UserDetailActivity : AppCompatActivity(), View.OnClickListener {
    private val listFavs = ArrayList<Fav>()
    private lateinit var favAddUpdateViewModel: FavAddUpdateViewModel
    private var id: Int = 0
    private lateinit var userName: String
    private lateinit var name: String
    private lateinit var urlPhoto: String
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityUserDetailBinding
    private var fav: Fav? = null
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.detail)
        binding.btnShare.setOnClickListener(this)
        binding.btnFavUnfav.setOnClickListener(this)
        val user = intent.getParcelableExtra<User>(USER_DATA) as User
        userName = user.userName
        binding.txtUserNameDetail.text = userName
        val pref = SettingPreferences.getInstance(dataStore)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, userName, pref)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
        favAddUpdateViewModel = obtainViewModel(this@UserDetailActivity, pref)
        val factory = this.application?.let { ViewModelFactory.getInstance(it, pref) }
        mainViewModel = this.let { ViewModelProvider(it, factory!!).get(MainViewModel::class.java) }
        var followerTxt = 0
        var followingTxt = 0
        var followerFollowing: String
        mainViewModel.getDetailUser(userName, this@UserDetailActivity)
        mainViewModel.name.observe(this, { mName ->
            if (mName != null) {
                name = mName
                listFavs
                binding.txtNameDetail.text = name
            }
        })
        mainViewModel.urlPhoto.observe(this, { photo ->
            urlPhoto = photo
            Glide.with(this)
                .load(urlPhoto)
                .apply(RequestOptions().override(55, 55))
                .into(binding.imgPhotoDetail)
        })
        mainViewModel.follower.observe(this, { follower ->
            followerTxt = follower
            followerFollowing = "Follower: $followerTxt, Following: $followingTxt"
            binding.txtFollower.text = followerFollowing
        })
        mainViewModel.following.observe(this, { following ->
            followingTxt = following
            followerFollowing = "Follower: $followerTxt, Following: $followingTxt"
            binding.txtFollower.text = followerFollowing
        })
        mainViewModel.company.observe(this, { company ->
            binding.txtLocation.text = company
        })
        mainViewModel.repo.observe(this, { repo ->
            val repoTxt = "Repository: $repo"
            binding.txtRepository.text = repoTxt
        })

        mainViewModel.isLoading.observe(this, {
            showLoading(it)
        })
        mainViewModel.checkIfExists(userName).observe(this, { favList ->
            if (favList != null && favList.isNotEmpty()) {
                id = favList[0].id
                binding.btnFavUnfav.text = getString(R.string.hapus_dari_favorite)
                isFavorite = true
            } else {
                binding.btnFavUnfav.text = getString(R.string.tambahkan_ke_favorite)
                isFavorite = false
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBarDetail.visibility = View.VISIBLE
        } else {
            binding.progressBarDetail.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, userName)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            R.id.btn_fav_unfav -> {
                fav = Fav()
                fav?.id = id
                fav?.name = name
                fav?.username = userName
                fav?.photo = urlPhoto
                if (isFavorite) {
                    favAddUpdateViewModel.delete(fav as Fav)
                    showToast(getString(R.string.berhasil_dihapus_favorite))
                    binding.btnFavUnfav.text = getString(R.string.tambahkan_ke_favorite)
                    isFavorite = false
                } else {
                    favAddUpdateViewModel.insert(fav as Fav)
                    showToast(getString(R.string.berhasil_favorite))
                    binding.btnFavUnfav.text = getString(R.string.hapus_dari_favorite)
                    isFavorite = false
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(
        activity: AppCompatActivity,
        pref: SettingPreferences
    ): FavAddUpdateViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory).get(FavAddUpdateViewModel::class.java)
    }

    companion object {
        const val USER_DATA = "user_data"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}