package ru.skillbranch.devintensive.ui.group

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.getColorByThemeAttr
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.adapters.UserAdapter
import ru.skillbranch.devintensive.ui.custom.ChatItemDecoration
import ru.skillbranch.devintensive.viewmodels.GroupViewModel

class GroupActivity : AppCompatActivity() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var viewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initToolbar()
        initViews()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home){
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
            true
        } else{
            super.onOptionsItemSelected(item)

        }

    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Создание группы"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        //delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        userAdapter = UserAdapter { viewModel.handleSelectedItem(it.id) }
        val divider = ChatItemDecoration(this)
        with(rv_user_list){
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@GroupActivity)
            addItemDecoration(divider)
        }

        fab.setOnClickListener {
            viewModel.handleCreateGroup()
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        viewModel.getUsersData().observe(this, Observer { userAdapter.updateData(it) })
        viewModel.getSelectedData().observe(this, Observer {
            updateChips(it)
            toggleFab(it.size>1)
        })

    }

    private fun toggleFab(isShow: Boolean) {
        if(isShow) fab.show() else fab.hide()
    }

    private fun addChipToGroup(user: UserItem){
        val chip = Chip(this).apply {
            Glide.with(this)
                .load(user.avatar)
                .circleCrop()
                .into(object : CustomTarget<Drawable>(){
                    override fun onLoadCleared(placeholder: Drawable?) {
                        chipIcon= placeholder
                    }

                    override fun onResourceReady(resource: Drawable,transition: Transition<in Drawable>?
                    ) {
                        chipIcon = resource
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        chipIcon = resources.getDrawable(R.drawable.avatar_default, theme)
                    }
                })
            text = user.fullName
            isCloseIconVisible = true
            tag = user.id
            isClickable = true
            closeIconTint = ColorStateList.valueOf(Color.WHITE)
            chipBackgroundColor = ColorStateList.valueOf(this.getColorByThemeAttr(R.attr.colorChipBackground))
            setTextColor(Color.WHITE)
        }
        chip.setOnCloseIconClickListener{
            viewModel.handleRemoveChip(it.tag.toString())
        }
        chip_group.addView(chip)
    }

    private fun updateChips(listUsers: List<UserItem>){
        chip_group.visibility = if (listUsers.isEmpty()) View.GONE else View.VISIBLE

        val users = listUsers
            .associate { user -> user.id to user }
            .toMutableMap()

        val views = chip_group.children.associate { view -> view.tag to view }

        for ((k,v) in views){
            if (!users.containsKey(k)) chip_group.removeView(v)
            else users.remove(k)
        }

        users.forEach{(_,v)->addChipToGroup(v)}
    }
}
